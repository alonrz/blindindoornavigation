package com.example.blindindoornavigation;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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

public class SensorActivity extends Activity implements SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mOrientation, mAccelerometer, mLinearAccelerometer;
	private float[] mOrientation2 = new float[3];
	private float[] mRotation = new float[9];
	private float[] mRemapedRotation = new float[9];
	private float[] mAccel = new float[3];
	private float[] mLinearAccel = new float[3];
	private float[] mGeoMagnetic = new float[3];
	private float mAzimuth;

	private float offsetY, offsetX, offsetZ;
	private boolean offset_set = false;
	private boolean mFailed, isSensorRunning = true;

	TextView txtAzimuth, txtZ_axis, txtX_axis, txtY_axis, txtVelocity, txtSteps;
	Button btnSave;
	DecimalFormat df = new DecimalFormat();
	WriterUtility writer;

	long interval, lastEvent;
	double lastVelocity, lastAccel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor_activity_layout);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mOrientation = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mLinearAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		df.setMaximumFractionDigits(4);
		this.onPause(); //stop the sensors. 
		

	}

	protected void onResume() {
		super.onResume();
		
	}

	private void registerListeners() {
		isSensorRunning = true;
		mSensorManager.registerListener(this, mOrientation,
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mLinearAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

		txtAzimuth = (TextView) findViewById(R.id.txtAzimuth);
		txtZ_axis = (TextView) findViewById(R.id.txtZ_axis);
		txtX_axis = (TextView) findViewById(R.id.txtX_axis);
		txtY_axis = (TextView) findViewById(R.id.txtY_axis);
		txtVelocity = (TextView) findViewById(R.id.txtVelocity);
		btnSave = (Button) findViewById(R.id.btnSaveToFile);
		txtSteps = (TextView) findViewById(R.id.txtSteps);
		
		writer = new WriterUtility(this);
		writer.startTest();
	}

	protected void onPause() {
		super.onPause();
		isSensorRunning = false;
		mSensorManager.unregisterListener(this);
	}

	void onSuccess() {
		Log.i(this.getClass().toString(), "onSuccess entered");
		// if (mFailed)
		// mFailed = false;
		// // Convert the azimuth to degrees in 0.5 degree resolution.
		// mAzimuth = (float) -Math.round((Math.toDegrees(mOrientation2[0])) *
		// 2) / 2;
		// // Adjust the range: 0 < range <= 360 (from: -180 < range <= 180).
		// //mAzimuth = (mAzimuth+360)%360; // alternative: mAzimuth
		// =mAzimuth>=0
		// // ? mAzimuth : mAzimuth+360;

	}

	void onFailure() {
		Log.i(this.getClass().toString(), "onFailure entered");
		// if (!mFailed) {
		// mFailed = true;
		// }
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			for (int i = 0; i < 3; i++)
				mAccel[i] = event.values[i];
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			for (int i = 0; i < 3; i++)
				mGeoMagnetic[i] = event.values[i];
			break;
		case Sensor.TYPE_LINEAR_ACCELERATION:
			for (int i = 0; i < 3; i++)
				mLinearAccel[i] = event.values[i];
			break;
		default:
			return;
		}

		// Register offsets:
		if (offset_set == false) {
			offsetX = mLinearAccel[0];
			offsetY = mLinearAccel[1];
			offsetZ = mLinearAccel[2];
		}
		offset_set = true;

		/*
		 * values[0]: Acceleration minus Gx on the x-axis values[1]:
		 * Acceleration minus Gy on the y-axis values[2]: Acceleration minus Gz
		 * on the z-axis
		 */
		txtX_axis.setText("X axis: " + df.format(mLinearAccel[0])
				+ "\n  (with offset = "
				+ (df.format(mLinearAccel[0] - offsetX)) + ")");
		txtY_axis.setText("Y axis: " + df.format(mLinearAccel[1])
				+ "\n  (with offset = "
				+ (df.format(mLinearAccel[1] - offsetY)) + ")");
		txtZ_axis.setText("Z axis: " + df.format(mLinearAccel[2])
				+ "\n  (with offset = "
				+ (df.format(mLinearAccel[2] - offsetZ)) + ")");

		long now = System.currentTimeMillis();
		interval = Math.abs(lastEvent - now);
		lastEvent = now;

		lastVelocity = lastVelocity + lastAccel * (interval / 1000000.);
		/*
		 * V = V_0 + AT where A is the previous accel applied on the interval
		 * passed To calculate the velocity traveled. V_0 is the initial
		 * velocity at each time interval passed so we keep updating it.
		 */
		//lastAccel = mLinearAccel[1] - offsetY; // Y- axis
		lastAccel = mLinearAccel[1] - offsetY + mLinearAccel[0]-offsetX +
				mLinearAccel[2]-offsetZ; // ALL axis

		txtVelocity.setText("velocity: " + df.format(lastVelocity) + " m/s");
		Log.i("Accel", Double.toString(lastAccel));
		writer.writeValue(Double.toString(lastAccel));
		CalculateSteps(lastAccel);
	}

	
	/*
	 * onClick Events
	 */
	//Given file name is the time now. 
	String lastFileNameSaved = new Time() {{ setToNow(); }}.format2445();
	public void onClick_RestartSensors(View view)
	{
		registerListeners();
	}
	
	public void onClick_Save(View view)
	{
		//Pause activity
		this.onPause();
		
		//Ask use for file name
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Save to File");
		alertBuilder.setMessage("Enter File Name (no extention): ");
		
		final EditText input = new EditText(this);
		input.setText(lastFileNameSaved); //insert last text
		input.selectAll();
		alertBuilder.setView(input);
		
		alertBuilder.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				lastFileNameSaved = input.getText().toString();
				
				//Save to file
				try {
					writer.endTestAndSave(lastFileNameSaved + " " + new Time() {{ setToNow(); }}.format2445());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		alertBuilder.setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		AlertDialog alert = alertBuilder.create();
		alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		alert.show();
	}
	
	public void onClick_Cancel(View view)
	{
		finish();
	}
	//vars for calculating steps
	double maxTotal, minTotal, maxLocal, minLocal, deltaPositive = 1, deltaNegative = 1;
	int mSteps=0;
	List<StepsListener> mListeners = new ArrayList<StepsListener>();
	boolean isPositive = true;
	
	public void addStepsListener(StepsListener listener)
	{
		mListeners.add(listener);
	}
	void CalculateSteps(double lastAccel)
	{
		//if acceleration is in delta (buffer) it should be ignored. 
		if(lastAccel > 0 && lastAccel < deltaPositive) return;
		if(lastAccel < 0 && lastAccel > deltaNegative) return;
		
		//total max and min
		if(maxTotal<lastAccel)
		{
			maxTotal = lastAccel;
			deltaPositive = maxTotal/3;
		}
		if(minTotal>lastAccel) 
		{
			minTotal = lastAccel;
			deltaNegative = minTotal/3;
		}
		
		//Check if transitioned from positive to negative or other way
		if(isPositive == true)
		{
			if(lastAccel < deltaNegative) isPositive = false;
		}
		else
		{
			if(lastAccel > deltaPositive) 
			{
				isPositive = true;
				stepHasBeenTaken(++mSteps);
			}
		}
		
		
		//When a step has been taken, call the interface
		
		
		
	}
	
	void stepHasBeenTaken(int steps)
	{
		//Call all listeners
		for(StepsListener sl : mListeners)
			sl.stepHasBeenTaken(steps);
		
		//Write to UI for debug
		txtSteps.setText("Steps: " + steps);
	}
}


