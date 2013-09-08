package com.example.blindindoornavigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FieldTester extends Activity {

	boolean isUndoEnabled = false;
	WriterUtility mWriter = new WriterUtility();
	ProgressBar pb;

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

		
		
		
		pb.setVisibility(ProgressBar.VISIBLE);
		LinearLayout buttonsLayout = (LinearLayout) findViewById(R.id.linear_layout2);
		buttonsLayout.setVisibility(View.INVISIBLE);
		
		startACL();
		
		if (isUndoEnabled == false) {
			Button btnUndo = (Button) findViewById(R.id.Undo);
			btnUndo.setEnabled(true);
			isUndoEnabled = true;
		}

		int n = mWriter.writeStep(new ArrayList<String>(Arrays.asList(
				"name of device", "RSSI", "MAC address")));

		pb.setVisibility(ProgressBar.INVISIBLE);
		buttonsLayout.setVisibility(View.VISIBLE);
		
		// Change number on button
		Button btnRecordStep = (Button) findViewById(R.id.RecordStep);
		btnRecordStep.setText("Record Step #" + (n + 1));
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
	
	
	
	
	
	
	
	
	
	
	
	BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	Set<BluetoothDevice> beaconsInRange = new HashSet<BluetoothDevice>();
	
	public void startACL()
    {   	
		CalculateLocation cl = new CalculateLocation();
		cl.start();
    }
	
	//Debug log function
		public static void d(String s){
			Log.d("debug", s);
		}
	
//////////////////////////////////////////////////////////////////////////////////////////
////////////////////////ACL Connection Thread Class///////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////

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
				adapter.listenUsingInsecureRfcommWithServiceRecord("blind", t);
				a.connect();
				a.close(); 
			}catch(Exception e){
				//d(addr + " " + e.toString());
			}	   
		}
	}

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
				d("ACL success for: " + bd.toString());
				beaconsInRange.add(bd);	
				d("adding: size:" + Integer.toString(beaconsInRange.size()));
			}
		}
	};


	@SuppressLint("NewApi")
	public void run() {

		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
		filter.setPriority(9999);
		registerReceiver(mReceiver, filter);// Don't forget to unregister during onDestroy   

		//Spin off threads for attempted connection to expected beacons
		//TODO make this list not hard coded, but gathered from an external map-based file.
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("90:00:4E:FE:34:E1"); //DLaptop
		stringList.add("78:A3:E4:A8:7E:48"); //DPhone
		stringList.add("3C:D0:F8:6B:16:32"); //Bphone 
		stringList.add("20:C9:D0:85:58:5A"); //Alap
		stringList.add("D0:23:DB:24:81:46"); //APhone 
		stringList.add("38:0A:94:A8:F8:76"); //Aandroid
		stringList.add("90:00:4E:F8:70:79"); //BLaptop 


		adapter.cancelDiscovery();
		for(int i=0; i<stringList.size(); i++){
			AcceptThread at = new AcceptThread(stringList.get(i));  
			at.start();
		}

		//Wait 5 seconds for threads to all return (~5 seconds for time out, 2-4 for ACL connection return)
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		calculateLocation();
		
	}


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

		WriterUtility wt = new WriterUtility();
		d("list: " + ls.toString());

		if(ls.contains("90:00:4E:FE:34:E1")){//DLaptop
			lsSorted.add("90:00:4E:FE:34:E1");
		}else {lsSorted.add("null");}

		if(ls.contains("78:A3:E4:A8:7E:48")){//DPhone
			lsSorted.add("78:A3:E4:A8:7E:48");
		}else{lsSorted.add("null");}

		if(ls.contains("20:C9:D0:85:58:5A")){//Alap
			lsSorted.add("20:C9:D0:85:58:5A");
		}else{lsSorted.add("null");}

		if(ls.contains("D0:23:DB:24:81:46")){//APhone
			lsSorted.add("D0:23:DB:24:81:46");
		}else{lsSorted.add("null");}

		if(ls.contains("38:0A:94:A8:F8:76")){//Aandroid
			lsSorted.add("38:0A:94:A8:F8:76"); 
		}else{lsSorted.add("null");}



		try {
			d("before pass: " + lsSorted.toString());
			wt.writeStep(lsSorted);
			
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
