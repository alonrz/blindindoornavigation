package com.example.blindindoornavigation;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class StepsManager implements SensorEventListener {

	/************* Singleton Section ******************/
	private static StepsManager mStepsManager = null;

	private StepsManager() {
	}

	/**
	 * Getter for Singleton class StepsManager
	 * 
	 * @return a singleton instance of StepsManager
	 */
	public static StepsManager getStepsManager() {
		if (mStepsManager == null)
			mStepsManager = new StepsManager();
		return mStepsManager;
	}

	/************ end Singleton Section ******************/

	private Activity mActivity;
	private SensorManager mSensorManager;
	private Sensor mOrientation, mAccelerometer, mLinearAccelerometer;
	private float[] mAccel = new float[3];
	private float[] mLinearAccel = new float[3];
	private float[] mGeoMagnetic = new float[3];

	private float offsetY, offsetX, offsetZ;
	private boolean offset_set = false;
	private boolean mFailed, isSensorRunning = false;

	WriterUtility writer;

	long interval, lastEvent;
	double lastVelocity, lastAccel;

	// vars for calculating steps
	double maxTotal = 0.8, minTotal = -0.8, maxLocal, minLocal,
			deltaPositive = 1, deltaNegative = -1;
	int mSteps = 0;
	boolean isPositive = true;

	// Given file name is formatted to reflect the time now.
	private int mStepLength; // The size of each step in cm
	private int mDistanceWalked = 0;
	private SharedPreferences mSharedPreferences;
	private boolean mHasStepLength = false; // means callibration was performed.

	/**
	 * Register your Activity class to get event calls.
	 * 
	 * @param activity
	 *            - Must be a subclass of Activity as well as implement the
	 *            interface
	 * @throws Exception
	 *             If activity is not a subclass of Activity
	 */
	public void registerListener(StepsListener activity) throws Exception {
		if(isSensorRunning == true)
			return;
		if (!(activity instanceof Activity))
			throw new Exception(
					"Not a subclass of Activity. Must be for use of SharedPreferences etc...");
		mActivity = (Activity) activity;

		// Get step length is available
		mSharedPreferences = mActivity.getPreferences(mActivity.MODE_PRIVATE);
		mStepLength = mSharedPreferences.getInt("mStepLength", 60);

		mSensorManager = (SensorManager) mActivity
				.getSystemService(mActivity.SENSOR_SERVICE);
		mOrientation = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mLinearAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		registerListeners();
		isSensorRunning = true;
	}

	/**
	 * Unregister your instance from event alerts
	 * 
	 * @param listener
	 *            an instance of StepsListener
	 */
	public void unregisterListener() {
		mSensorManager.unregisterListener(this);
		isSensorRunning =false;
		reset();
		mActivity = null;
	}

	/**
	 * Register to all sensors are here. Also allocating UI objects.
	 */
	private void registerListeners() {
		isSensorRunning = true;
		mSensorManager.registerListener(this, mOrientation,
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mLinearAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

		writer = new WriterUtility(mActivity);
		writer.startTest();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

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

		// txtX_axis.setText("X axis: " + df.format(mLinearAccel[0])
		// + "\n  (with offset = "
		// + (df.format(mLinearAccel[0] - offsetX)) + ")");
		// txtY_axis.setText("Y axis: " + df.format(mLinearAccel[1])
		// + "\n  (with offset = "
		// + (df.format(mLinearAccel[1] - offsetY)) + ")");
		// txtZ_axis.setText("Z axis: " + df.format(mLinearAccel[2])
		// + "\n  (with offset = "
		// + (df.format(mLinearAccel[2] - offsetZ)) + ")");

		long now = System.currentTimeMillis();
		interval = Math.abs(lastEvent - now);
		lastEvent = now;
		
		// lastAccel = mLinearAccel[1] - offsetY; // Y- axis
		lastAccel = mLinearAccel[1] - offsetY + mLinearAccel[0] - offsetX
				+ mLinearAccel[2] - offsetZ; // ALL axis

		if(lastAccel>0.5) 
			Log.i("Accel", Double.toString(lastAccel));
		writer.writeValue(Double.toString(lastAccel));
		CalculateSteps(lastAccel);

	}

	void CalculateSteps(double lastAccel) {
		// if acceleration is in delta (buffer) it should be ignored.
		if (lastAccel > 0 && lastAccel < deltaPositive)
			return;
		if (lastAccel < 0 && lastAccel > deltaNegative)
			return;

		// total max and min
		if (maxTotal < lastAccel) {
			maxTotal = lastAccel;
			deltaPositive = maxTotal / 4;
		}
		if (minTotal > lastAccel) {
			minTotal = lastAccel;
			deltaNegative = minTotal / 4;
		}

		// Check if transitioned from positive to negative or other way
		if (isPositive == true) {
			if (lastAccel < deltaNegative)
				isPositive = false;
		} else {
			if (lastAccel > deltaPositive) {
				isPositive = true;
				stepDetected(++mSteps);
			}
		}
	}

	/**
	 * This method will call all listeners and alert them of the number of steps
	 * 
	 * @param steps
	 *            - takes an integer off total number of steps.
	 */
	void stepDetected(int steps) {
		((StepsListener) mActivity).stepHasBeenTaken(steps);
		((StepsListener) mActivity).stepHasBeenTaken(mHasStepLength, steps
				* mStepLength);
	}

	/**
	 * This method will reset both the steps counter and distance measured.
	 * Events will still be fired until unregister is complete.
	 */
	public void reset() {
		mSteps = 0;
		mDistanceWalked = 0;
	}

	/**
	 * Start calibration for 9 m
	 */
	public void startCalibration() {
		mSteps = 0;
		mDistanceWalked = 0;
	}

	/**
	 * Stop calibration test after 9m.
	 * 
	 * @return length of a single step
	 */
	public int stopCalibration() {
		if (mSteps != 0) {
			mStepLength = 900 / mSteps;
			mSharedPreferences.edit().putInt("mStepLength", mStepLength);
			mHasStepLength = true;
		}
		return mStepLength;
	}

	public void writeToFile(String lastFileNameSaved) {
		try {
			writer.endTestAndSave(lastFileNameSaved + " " + new Time() {
				{
					setToNow();
				}
			}.format2445());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
