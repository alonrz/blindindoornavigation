package com.example.blindindoornavigation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class WriterUtility {

	private static StringBuilder sb;
	private static int n = 0; // n = the number of records (lines) added.

	private SensorManager mSensorManager;
	private Sensor sensor;
	private float mAzimuth;
	private Context mContext;

	public WriterUtility(Context context) {
		mContext = context;
	}

	public void startTest() {
		sb = new StringBuilder();

		// Sensor init
		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		if (sensor != null) {
			mSensorManager.registerListener(mySensorEventListener, sensor,
					SensorManager.SENSOR_DELAY_UI);
			Log.i("Compass MainActivity", "Registerered for ORIENTATION Sensor");

		} else {
			Log.e("Compass MainActivity", "ORIENTATION Sensor not found");
			Toast.makeText(mContext, "ORIENTATION Sensor not found",
					Toast.LENGTH_LONG).show();
		}
	}

	public void endTestAndSave() throws IOException {
		Time time = new Time() {
			{
				setToNow();
			}
		};
		endTestAndSave("beacon_data_" + time.format2445());
	}

	public void endTestAndSave(String filename) throws IOException {
		try {
			File rootDirectory = Environment.getExternalStorageDirectory();
			File myDirectory = new File(rootDirectory.getAbsolutePath()
					+ "/indoorNavData");
			if (myDirectory.exists() == false) {
				myDirectory.mkdir();
			}

			// Create file name with time stamp.
			// write new file with time stamp
			File myFile = new File(myDirectory + "/" + filename + ".csv");

			if (myFile.exists() == false) {
				myFile.createNewFile();
			}
			FileWriter fw = new FileWriter(myFile.getAbsoluteFile());
			BufferedWriter writer = new BufferedWriter(fw);

			writer.write(sb.toString());
			writer.newLine();
			writer.close();

		} catch (IOException e) {
			throw e;
		} finally {
			if (sensor != null) {
				mSensorManager.unregisterListener(mySensorEventListener);
			}
		}

	}

	public int writeStep(List<String> itemsToWrite) throws IOException {

		n++;

		// *** Add more data such as sensors here ***

		// Add time
		sb.append(new Time() {
			{
				setToNow();
			}
		}.format2445());
		// Add compass heading
		// angle between the magnetic north direction
		// 0=North, 90=East, 180=South, 270=West
		sb.append("," + mAzimuth);

		// size of list is always 6 beacons X 3 value = 18 values. //Fill in
		// rest of non existing values with -999999
		int i = 0;
		for (; i < itemsToWrite.size(); i++) {
			sb.append("," + itemsToWrite.get(i));

		}
		for (; i < 18; i++) {
			sb.append(",-999999");
		}

		sb.append('\n');
		return n;

	}

	/*
	 * Write a single value at a time into an never ending string. 
	 */
	public void writeValue(String valueToWrite)
	{
		sb.append(valueToWrite + ",");
	}

	public int undoStep() {
		int lastNewLine = sb.lastIndexOf("\n");
		sb.delete(lastNewLine, sb.length());
		lastNewLine = sb.lastIndexOf("\n");
		sb.delete(lastNewLine + 1, sb.length());
		n--;
		return n;
	}

	private SensorEventListener mySensorEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			mAzimuth = (int) event.values[0];

		}
	};
}
