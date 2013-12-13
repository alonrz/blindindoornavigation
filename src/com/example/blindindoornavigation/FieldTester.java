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
import android.widget.TextView;
import android.widget.Toast;

public class FieldTester extends Activity implements LocationListener {
	Hashtable<String, Short> rssiVal = new Hashtable<String, Short>();
	Hashtable<Integer, String> colOrder= new Hashtable<Integer, String>(); //used for pilot test to keep certain beacons in column order
	Hashtable<String, String> pointsOfInterest = new Hashtable<String, String>(); //This will most likely be pulled from a database, but currently its just hard coded
	List<String> lsSorted = new ArrayList<String>();
	List<String> temp = new ArrayList<String>();
	boolean firstTimeBoolean = true;
	String CalculatedLocation = "-1";




	BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	Set<BluetoothDevice> beaconsInRange = new HashSet<BluetoothDevice>();

	boolean isUndoEnabled = false;
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

		//Button btnStart = (Button) findViewById(R.id.StartWrite);
		//btnStart.setEnabled(false);
		
		//Button btnStop = (Button) findViewById(R.id.StopAndSave);
		//btnStop.setEnabled(true);
		
		final Button next = (Button) findViewById(R.id.StopAndSave);
		next.setBackgroundResource(R.drawable.loc1);

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
	public void newLocationHasBeenCalculated(final String LocationCalculated) {
		d("Newlocationhasbeencalculated in callback: " + LocationCalculated);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {	
				TextView ts = (TextView) findViewById(R.id.testbt);
				ts.setText(LocationCalculated);
			}
		});
	}

	//Debug log function
	public static void d(String s){ 
		Log.d("debug", s);
	}
}
