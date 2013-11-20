package com.example.blindindoornavigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FieldTester extends Activity implements LocationListener {
	Hashtable<String, Short> rssiVal = new Hashtable<String, Short>();
	//Hashtable<String, String> deviceName = new Hashtable<String, String>();
	Hashtable<Integer, String> colOrder= new Hashtable<Integer, String>(); //used for pilot test to keep certain beacons in column order
	Hashtable<String, String> pointsOfInterest = new Hashtable<String, String>(); //This will most likely be pulled from a database, but currently its just hard coded
	List<String> lsSorted = new ArrayList<String>();
	List<String> temp = new ArrayList<String>();
	boolean firstTimeBoolean = true;
	String CalculatedLocation = "-1";

	


	BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	Set<BluetoothDevice> beaconsInRange = new HashSet<BluetoothDevice>();

	boolean isUndoEnabled = false;
	//Context context = getBaseContext();
	WriterUtility mWriter = new WriterUtility(this);
	ProgressBar pb;

	int n = 0;//debug
 



	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.field_test);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		pb = (ProgressBar) findViewById(R.id.progress_bar);
	}

	public void onClick_StartWrite(View view) throws IOException { 

		if(firstTimeBoolean){
			mWriter.startTest();
			firstTimeBoolean = false;
		}


		//mWriter.writeStep(temp);

		Button btnStart = (Button) findViewById(R.id.StartWrite);
		//btnStart.setEnabled(false);
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

	
		
		pb.setVisibility(ProgressBar.VISIBLE);
		LinearLayout buttonsLayout = (LinearLayout) findViewById(R.id.linear_layout2);
		buttonsLayout.setVisibility(View.INVISIBLE);

		//Start e
		DiscoverLocation cl = new DiscoverLocation(this); 
		cl.InitBluetoothLocationServices();
		cl.registerLocationListener(this);
		cl.scanForDevices();//Starts the location finding algorithm


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
	public void newLocationHasBeenCalculated(String LocationCalculated) {
		d("Newlocationhasbeencalculated in callback: " + LocationCalculated);
		
	}

	//Debug log function
			public static void d(String s){ 
				Log.d("debug", s);
			}
	/*public static void scanForDevices(){
		FieldTester f = new FieldTester();
		f.scanForDevicestwo();
		
	}
	private void scanForDevicestwo(){
		DiscoverLocation ct = new DiscoverLocation(this);
		ct.scanForDevices();//Starts the location finding algorithm
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

	//TODO
	//TODO NOT CURRENTLY USING THIS FOR PROTOTYPING SESSIONS, NEED TO ADD IT AGAIN TO DO PILOT DATA!!!!!
	//TODO
	public void sortColumns(){
		//TODO Calculations for location.  
		//AT this point in execution 'beaconsInRange' is a set of bluetooth 'beacons' (devices) that are 'seen' 
		//For our test

		d(beaconsInRange.toString());

		List<String> ls = new ArrayList<String>();

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

		lsSorted.add(CalculatedLocation);

		ls.clear();



	}

	static List<LocationListener> mListeners = new ArrayList<LocationListener>();
	public static void addLocationListener(LocationListener listener)
    {
            mListeners.add(listener);
    }
	
	private class discover extends Thread {

		public discover() {

		}

		@SuppressLint("NewApi")
		public void run() {

			d("bir: " + beaconsInRange.toString());
			beaconsInRange.clear();
			rssiVal.clear();
			lsSorted.clear(); 


			adapter.startDiscovery();
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			adapter.cancelDiscovery();
			//try {
				
				//sortColumns();
				locatePosition();
				//mWriter.writeStep(lsSorted);
				d("writeStep lsSroted:" + lsSorted);
			//} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
				
			
			newLocationHasBeenCalculated();
			discover d = new discover();
			d.start();  
		}


		void newLocationHasBeenCalculated()
        {
                //Call all listeners
                for(LocationListener sl : mListeners)
                        sl.newLocationHasBeenCalculated(CalculatedLocation);
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
								CalculatedLocation = pointsOfInterest.get(tempClosestBeacon.toString());
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
								CalculatedLocation = pointsOfInterest.get(tempClosestBeacon.toString() + tempSecondClosestBeacon.toString());
							}
							else { 
								btnUndo.setText("didnt find two beacons");
							}
						}
					});
				}
			}
		}
	}


	

	//////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////Calculate Location Thread///////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	private class ScanForDevices extends Thread {

		public ScanForDevices() {

		}

		//Create a BroadcastReceiver for ACL connection success
		final BroadcastReceiver mReceiver = new BroadcastReceiver() {
			@SuppressLint("NewApi")
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction(); 

				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice bd = (BluetoothDevice) intent.getExtras().get("android.bluetooth.device.extra.DEVICE");

					rssiVal.put(bd.toString(), intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
					beaconsInRange.add(bd);

					//////////////////////////////////////////////////////////////////////////////////////////
					//runOnUiThread is used currently for a quick and dirty way to output using button texts
					///////////////////////////////////////////////////////////////////////////////////////////
					runOnUiThread(new Runnable() {
						@Override
						public void run() {	
							//Toast.makeText(FieldTester.this, "The closest beacon is: " + deviceName.get(tempClosestBeacon.toString()), Toast.LENGTH_LONG).show();

							



							Button btnRecordStep = (Button) findViewById(R.id.StartWrite);
							btnRecordStep.setText("David's Laptop: " + rssiVal.get("90:00:4E:FE:34:E1") +
									"\nDavid's iPhone: " + rssiVal.get("78:A3:E4:A8:7E:48")+
									//"\nAlon's Laptop: " + rssiVal.get("20:C9:D0:85:58:5A")+
									//"\nAlon's iPhone: " + rssiVal.get("D0:23:DB:24:81:46")+
									//"\nAlon's Android: " + rssiVal.get("38:0A:94:A8:F8:76")+
									"\nB old iphone4: " + rssiVal.get("CC:08:E0:A8:02:27")+
									"\nD old iphone4: " + rssiVal.get("CC:08:E0:96:34:4D")+
									"\nD new laptop: " + rssiVal.get("00:02:72:C6:A3:C6")+
									"\nD old laptop: " + rssiVal.get("00:02:72:C6:A3:A4")+
									"\nB old laptop: " + rssiVal.get("00:02:72:C6:A3:89")+
									"\nBeacon 4: " + rssiVal.get("00:02:72:3F:4E:8B")
									);


						}
					});


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


			////////////////////////////////////////////////////////////////////////////////
			//start an inquiry scan and then wait for ~11 seconds for the result
			////////////////////////////////////////////////////////////////////////////////
			d("::::::Starting::::::");
			//adapter.startDiscovery();
			discover d = new discover();
			d.start();

			//sortColumns();
			//runOnUiThread(new Runnable() {
				//@Override
				//public void run() {	
					//Toast.makeText(FieldTester.this, "Write is done.", Toast.LENGTH_LONG).show();	
					//Button btnRecordStep = (Button) findViewById(R.id.RecordStep);
					//btnRecordStep.setEnabled(true);

					//Button btnUndo = (Button) findViewById(R.id.Undo);
					//btnUndo.setText(CalculatedLocation);

				//}
			//});
		}







	}
	
	
	
	
	
	*/
}
