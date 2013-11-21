package com.example.blindindoornavigation;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SensorActivity extends Activity implements SensorEventListener, StepsListener {

	StepsManager mStepsManager = StepsManager.getStepsManager();
	
	TextView txtAzimuth, txtZ_axis, txtX_axis, txtY_axis, txtVelocity,
			txtSteps, txtDistance;
	Button btnSave;
	DecimalFormat df = new DecimalFormat();
	String lastFileNameSaved = new Time() {{ setToNow(); }}.format2445();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor_activity_layout);

		txtAzimuth = (TextView) findViewById(R.id.txtAzimuth);
		txtZ_axis = (TextView) findViewById(R.id.txtZ_axis);
		txtX_axis = (TextView) findViewById(R.id.txtX_axis);
		txtY_axis = (TextView) findViewById(R.id.txtY_axis);
		txtVelocity = (TextView) findViewById(R.id.txtVelocity);
		btnSave = (Button) findViewById(R.id.btnSaveToFile);
		txtSteps = (TextView) findViewById(R.id.txtSteps);
		txtDistance = (TextView) findViewById(R.id.txtDistance);

		df.setMaximumFractionDigits(4);
		
		this.onPause(); // stop the sensors.
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onPause() {
		super.onPause();
		//isSensorRunning = false;
	}

	void onSuccess() {
		Log.i(this.getClass().toString(), "onSuccess entered");
	}

	void onFailure() {
		Log.i(this.getClass().toString(), "onFailure entered");
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Nothing happens here
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

	
	}


	/************************************
	 ** onClick Events below 
	 ************************************/

	public void onClick_Start(View view) throws Exception
	{
		mStepsManager.registerListener(this);
	}
	
	public void onClick_Cancel(View view) {
		mStepsManager.unregisterListener();
		finish();
	}
	
	public void onClick_RestartSensors(View view) {
		mStepsManager.reset();
	}

	public void onClick_Save(View view) {
		// Pause activity
		this.onPause();

		// Ask use for file name
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Save to File");
		alertBuilder.setMessage("Enter File Name (no extention): ");

		final EditText input = new EditText(this);
		input.setText(lastFileNameSaved); // insert last text
		input.selectAll();
		alertBuilder.setView(input);

		alertBuilder.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				lastFileNameSaved = input.getText().toString();

				// Save to file
				mStepsManager.writeToFile(lastFileNameSaved);
				
			}
		});
		alertBuilder.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});

		AlertDialog alert = alertBuilder.create();
		alert.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		alert.show();
	}

	public void onClick_StartCalibrate(View view) {
		// Ask use for file name
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Calibrate Steps");
		alertBuilder
				.setMessage("Press 'OK' and walk normally for 9m (~30f). Press Stop once you're done.");

		alertBuilder.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// Start calibration
				mStepsManager.startCalibration();
			}
		});
		alertBuilder.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do thing. Cancel was pressed.
			}
		});

		AlertDialog alert = alertBuilder.create();
		alert.show();
	}

	public void onClick_StopCalibrate(View view) 
	{	
		txtDistance.setText(String.valueOf(mStepsManager.stopCalibration()));
	}

	@Override
	public void stepHasBeenTaken(int steps) 
	{
		txtSteps.setText("Steps: " + steps);
	}

	@Override
	public void stepHasBeenTaken(boolean hasAccurateDistance, int totalDistance) {
		if(hasAccurateDistance == false)
			return;
		
		txtDistance.setText("Distance: " + totalDistance);
		
	}
}
