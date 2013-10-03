package com.example.blindindoornavigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FieldTester extends Activity {
	Hashtable<String, Short> rssiVal = new Hashtable<String, Short>();
	Hashtable<String, String> deviceName = new Hashtable<String, String>();
	Hashtable<Integer, String> colOrder= new Hashtable<Integer, String>(); //used for pilot test to keep certain beacons in column order
	Hashtable<String, String> pointsOfInterest = new Hashtable<String, String>(); //This will most likely be pulled from a database, but currently its just hard coded
	
	BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	Set<BluetoothDevice> beaconsInRange = new HashSet<BluetoothDevice>();
	
	boolean isUndoEnabled = false;
	WriterUtility mWriter = new WriterUtility();
	ProgressBar pb;
	
	int n = 0;//debug

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.field_test);
		pb = (ProgressBar) findViewById(R.id.progress_bar);
		
	}

	public void onClick_StartWrite(View view) throws IOException {
		mWriter.startTest();
		Button btnStart = (Button) findViewById(R.id.StartWrite);
		btnStart.setEnabled(false);
		Button btnStop = (Button) findViewById(R.id.StopAndSave);
		btnStop.setEnabled(true);

		Button btnRecordStep = (Button) findViewById(R.id.RecordStep);
		btnRecordStep.setEnabled(true);
		Button btnUndo = (Button) findViewById(R.id.Undo);
		btnUndo.setEnabled(false);
	}

	public void onClick_StopAndSave(View view) throws IOException {
		mWriter.endTestAndSave();
		Button btnStart = (Button) findViewById(R.id.StartWrite);
		btnStart.setEnabled(true);
		Button btnStop = (Button) findViewById(R.id.StopAndSave);
		btnStop.setEnabled(false);

		Button btnRecordStep = (Button) findViewById(R.id.RecordStep);
		btnRecordStep.setEnabled(false);
		Button btnUndo = (Button) findViewById(R.id.Undo);
		btnUndo.setEnabled(false);
	}

	public void onClick_RecordStep(View view) throws IOException {
	
		beaconsInRange.clear();
		rssiVal.clear();
		adapter.cancelDiscovery();
		
		pb.setVisibility(ProgressBar.VISIBLE);
		LinearLayout buttonsLayout = (LinearLayout) findViewById(R.id.linear_layout2);
		buttonsLayout.setVisibility(View.INVISIBLE);
		
		CalculateLocation cl = new CalculateLocation();
		cl.start();//Starts the location finding algorithm
		
		if (isUndoEnabled == false) {
			Button btnUndo = (Button) findViewById(R.id.Undo);
			btnUndo.setEnabled(true);
			isUndoEnabled = true;
		}

		pb.setVisibility(ProgressBar.INVISIBLE);
		buttonsLayout.setVisibility(View.VISIBLE);
		
		// Change number on button
		Button btnRecordStep = (Button) findViewById(R.id.RecordStep);
		btnRecordStep.setText("Record Step #" + (n + 1));
		
		//Toast based counter
		//for(int i = 0; i <6;i++)
			//Toast.makeText(this, Integer.toString(i), Toast.LENGTH_SHORT).show();
	}

	public void onClick_Undo(View view) {
		int n = mWriter.undoStep();
		if (n == 0) {
			Button btnUndo = (Button) findViewById(R.id.Undo);
			btnUndo.setEnabled(false);
			isUndoEnabled = false;
		}
		Button btnRecordStep = (Button) findViewById(R.id.RecordStep);
		btnRecordStep.setText("Record Step #" + (n + 1));
	}
		
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		d("stopping");
	}
	
	public void onDestroy(){
		super.onDestroy();
		d("Destroying");	
	}
	
	//Debug log function
	public static void d(String s){
		Log.d("debug", s);
	}
	
//////////////////////////////////////////////////////////////////////////////////////////
////////////////////////ACL Connection Thread Class///////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

	/*
	 * Attempted to use the RFCOMM connection to simulate an ACL link 'ping' -- doesnt work using the built in bluetooth stack.  
	 * each ping takes 5seconds, and can't be done concurrently.
	 * 
	    private class AcceptThread extends Thread {
		private String addr;

		public AcceptThread(String macAddress) {
			addr = macAddress;
		}

		@SuppressLint("NewApi")
		public void run() {

			BluetoothSocket a;

			BluetoothDevice macTest;
			macTest = adapter.getRemoteDevice(addr);//phone

			d("macTest: " + macTest.toString());
			UUID t = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  

			//Attempt to form a "connection" so that the low level ACL data will be sent -- which is all we want
			//This try/catch block will always end in an IOexception. 
			try{ 
				adapter.cancelDiscovery();
				a = macTest.createInsecureRfcommSocketToServiceRecord(t);
				//adapter.listenUsingInsecureRfcommWithServiceRecord("blind", t);
				
				a.connect(); 
			}catch(Exception e){
				//d(addr + " " + e.toString()); 
			}	   
		}
	}
	
	*/
	
//////////////////////////////////////////////////////////////////////////////////////////
////////////////////////Calculate Location Thread///////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

	private class CalculateLocation extends Thread {
	
		public CalculateLocation() {
	
		}
	
		//Create a BroadcastReceiver for ACL connection success
		final BroadcastReceiver mReceiver = new BroadcastReceiver() {
			@SuppressLint("NewApi")
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction(); 
	
				//If this is true, then an ACL response was given by one of the beacons.  To get the beacon MAC that responded use intent.getExtras().get("android.bluetooth.device.extra.DEVICE").toString()
				if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
					BluetoothDevice bd = (BluetoothDevice) intent.getExtras().get("android.bluetooth.device.extra.DEVICE");
					beaconsInRange.add(bd);	
				}
				
				//This if clause is used for the default discovery scan
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice bd = (BluetoothDevice) intent.getExtras().get("android.bluetooth.device.extra.DEVICE");
					
					rssiVal.put(bd.toString(), intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
					beaconsInRange.add(bd);
					
					locatePosition();
				}
				
				if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
				{
					d("discovery started");
				}
				if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
				{
					d("discovery finished");
				}
				
				
				
			}
		};
	
	
		@SuppressLint("NewApi")
		public void run() {
			

			// Register the BroadcastReceiver
			//IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
			IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			IntentFilter filter4 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
			
			// TODO Don't forget to unregister during onDestroy  (currently never unregisters)
			registerReceiver(mReceiver, filter2);
			registerReceiver(mReceiver, filter3);
			registerReceiver(mReceiver, filter4);
	
			deviceName.put("90:00:4E:FE:34:E1", "David's Laptop");
			deviceName.put("78:A3:E4:A8:7E:48", "David's iPhone");
			deviceName.put("20:C9:D0:85:58:5A", "Alon's Laptop");
			deviceName.put("D0:23:DB:24:81:46", "Alon's iPhone");
			deviceName.put("38:0A:94:A8:F8:76", "Alon's Android");
			deviceName.put("CC:08:E0:A8:02:27", "B old iphone4");
			deviceName.put("CC:08:E0:96:34:4D", "D old iphone4");
			
			/////////////////////////////////////////////////////////////////////////
			//TODO Currently only three beacons and their mid points are hard coded
			//TODO this kind of information should be pulled from a database
			//TODO this current chunk of code is for demo purposes only.
			////////////////////////////////////////////////////////////////////////
			String tempString;
			tempString = "corner near CS office";
			pointsOfInterest.put("CC:08:E0:96:34:4D", tempString);
			tempString = "CS office (206, 207)";
			pointsOfInterest.put("CC:08:E0:96:34:4DCC:08:E0:A8:02:27", tempString); // between b and d old iphone4
			pointsOfInterest.put("CC:08:E0:A8:02:27CC:08:E0:96:34:4D", tempString); // between b and d old iphone4
			tempString = "room 208, 209";
			pointsOfInterest.put("CC:08:E0:A8:02:27", tempString);
			tempString = "room 210, 211";
			pointsOfInterest.put("CC:08:E0:A8:02:2778:A3:E4:A8:7E:48", tempString);
			pointsOfInterest.put("78:A3:E4:A8:7E:48CC:08:E0:A8:02:27", tempString);
			tempString = "room 212, 213";
			pointsOfInterest.put("78:A3:E4:A8:7E:48", tempString);
			
			//////////////////////////////////////////////////////////////////////////////////////////////
			//colOrder is used to make sure the write-to-database is always written in the correct order
			//so rows/col stay static
			//////////////////////////////////////////////////////////////////////////////////////////////
			colOrder.put(0, "David's Laptop");
			colOrder.put(1, "David's iPhone");
			colOrder.put(2, "Alon's Laptop");
			colOrder.put(3, "Alon's iPhone");
			colOrder.put(4, "Alon's Android");
			colOrder.put(5, "B old iphone4");
			colOrder.put(6, "D old iphone4");
		
			
			////////////////////////////////////////////////////////////////////////////////
			//start an inquiry scan and then wait for ~11 seconds for the result
			////////////////////////////////////////////////////////////////////////////////
			d("::::::Starting::::::");
			adapter.startDiscovery();
			try {
				Thread.sleep(11000); 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block 
				e.printStackTrace();
			}
		}
	
		private void locatePosition(){
			//////////////////////////////////////////////////////////
			//Find the closest beacon
			//////////////////////////////////////////////////////////
			Iterator<BluetoothDevice> biri = beaconsInRange.iterator();
			
			BluetoothDevice tbd;
			Short temp;
			int currentHighest = -9999;
			BluetoothDevice closestBeacon = null;
			while(biri.hasNext())
			{
				tbd = biri.next();
				temp = rssiVal.get(tbd.toString());
				
				if(temp > currentHighest){
					closestBeacon = tbd;
					currentHighest = temp;
				}
			}
			final BluetoothDevice tempClosestBeacon = closestBeacon;
			d(rssiVal.toString());
			
			///////////////////////////////////////////////////////////
			//Find the second closest beacon
			///////////////////////////////////////////////////////////
			Iterator<BluetoothDevice> biri2 = beaconsInRange.iterator();
			int tempCurrentHighest = -9999;
			BluetoothDevice secondClosestBeacon = null;
			while(biri2.hasNext())
			{
				tbd = biri2.next();
				temp = rssiVal.get(tbd.toString());
				if((temp > tempCurrentHighest) ){
					if(tbd != tempClosestBeacon)
					{
						secondClosestBeacon = tbd;
						tempCurrentHighest = temp;
					}
					
				}
			}
			
			final BluetoothDevice tempSecondClosestBeacon = secondClosestBeacon;
			
			//////////////////////////////////////////////////////////////////////////////////////////
			//runOnUiThread is used currently for a quick and dirty way to output using button texts
			///////////////////////////////////////////////////////////////////////////////////////////
			runOnUiThread(new Runnable() {
			      @Override
			          public void run() {	
			    	  		//Toast.makeText(FieldTester.this, "The closest beacon is: " + deviceName.get(tempClosestBeacon.toString()), Toast.LENGTH_LONG).show();
			    	  		
			    	  		/*Button btnUndo = (Button) findViewById(R.id.StopAndSave);
			    	  		if(tempClosestBeacon != null && tempSecondClosestBeacon != null)
			    	  			btnUndo.setText("closest:" + deviceName.get(tempClosestBeacon.toString()) + "\nsecond:" + deviceName.get(tempSecondClosestBeacon.toString()));
			    	  		else 
			    	  			btnUndo.setText("didnt find two beacons");*/
							
			    	  		
			    	  		
							Button btnRecordStep = (Button) findViewById(R.id.StartWrite);
							btnRecordStep.setText("David's Laptop: " + rssiVal.get("90:00:4E:FE:34:E1") +
									"\nDavid's iPhone: " + rssiVal.get("78:A3:E4:A8:7E:48")+
									"\nAlon's Laptop: " + rssiVal.get("20:C9:D0:85:58:5A")+
									"\nAlon's iPhone: " + rssiVal.get("D0:23:DB:24:81:46")+
									"\nAlon's Android: " + rssiVal.get("38:0A:94:A8:F8:76")+
									"\nB old iphone4: " + rssiVal.get("CC:08:E0:A8:02:27")+
									"\nD old iphone4: " + rssiVal.get("CC:08:E0:96:34:4D")
									);
			          }
			   });
			
			
			///////////////////////////////////////
			//Find location
			///////////////////////////////////////
			if(tempClosestBeacon !=null && tempSecondClosestBeacon !=null)
			{
				int rssiOne = rssiVal.get(tempClosestBeacon.toString());
				int rssiTwo = rssiVal.get(tempSecondClosestBeacon.toString());
				
				final int  BEACON_LOCATION_SENSITIVITY = 7;
				if((rssiOne - rssiTwo) > BEACON_LOCATION_SENSITIVITY)
				{
					////////////////////////////////////////////////////////////////////////////////////
					//closer to rssiOne beacon, so pull the correct data for that location
					////////////////////////////////////////////////////////////////////////////////////
					runOnUiThread(new Runnable() {
					      @Override
					          public void run() {	
						    	  	Button btnUndo = (Button) findViewById(R.id.StopAndSave);
					    	  		if(tempClosestBeacon != null && tempSecondClosestBeacon != null)
					    	  		{
					    	  			btnUndo.setText(pointsOfInterest.get(tempClosestBeacon.toString()));
					    	  		}else {
					    	  			btnUndo.setText("didnt find two beacons");
					    	  		}
					          }
					   });
				}
				else if ((rssiOne - rssiTwo) > -(BEACON_LOCATION_SENSITIVITY) && (rssiOne - rssiTwo) < BEACON_LOCATION_SENSITIVITY)
				{
					////////////////////////////////////////////////////////////////////////////////////
					//Halfway between two closest beacons, so pull the correct data for that location
					////////////////////////////////////////////////////////////////////////////////////
					runOnUiThread(new Runnable() {
					      @Override
					          public void run() {	
					    	  Button btnUndo = (Button) findViewById(R.id.StopAndSave);
				    	  		if(tempClosestBeacon != null && tempSecondClosestBeacon != null)
				    	  		{
				    	  			btnUndo.setText(pointsOfInterest.get(tempClosestBeacon.toString() + tempSecondClosestBeacon.toString()));
				    	  		}
				    	  		else {
				    	  			btnUndo.setText("didnt find two beacons");
				    	  		}
					          }
					   });
				}
			}
		}
		

		
		//TODO
		//TODO NOT CURRENTLY USING THIS FOR PROTOTYPING SESSIONS, NEED TO ADD IT AGAIN TO DO PILOT DATA!!!!!
		//TODO
		private void calculateLocation(){
			//TODO Calculations for location.  
			//AT this point in execution 'beaconsInRange' is a set of bluetooth 'beacons' (devices) that are 'seen' 
			//For our test
	
			d(beaconsInRange.toString());
	
			List<String> ls = new ArrayList<String>();
			List<String> lsSorted = new ArrayList<String>();
			Iterator<BluetoothDevice> iter = beaconsInRange.iterator();
			while (iter.hasNext()) {
				ls.add(iter.next().toString());
			}
	
			
			String macTemp;
			for(int i = 0; i<colOrder.size();i++)
			{
				if(ls.contains(colOrder.get(i))){
					macTemp = ls.get(ls.indexOf(colOrder.get(i)));
					lsSorted.add(macTemp);
					lsSorted.add(Short.toString(rssiVal.get(macTemp)));
				}
				else
				{
					lsSorted.add("null");
					lsSorted.add("null");
				}
			}		
	
			try {
				d("Writing: " + lsSorted.toString());
				WriterUtility.writeStep(lsSorted);
				
				//Clear beacons seen
				lsSorted.clear(); 
				ls.clear();
				beaconsInRange.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	
		}
	
	}
}
