package com.example.blindindoornavigation;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Note:  Backend demo was a quick-and-dirty way to demonstrate the backend systems are functional and returning correct data. 
 * 		  Therefore any duct tape/questionable programming was intentionally overlooked. Use at your own risk.
 *
 */
public class BackendDemo extends Activity implements LocationListener, StepsListener {


	/////ORIENTATION
	private static StringBuilder sb;
	private static int n = 0; // n = the number of records (lines) added.

	private SensorManager mSensorManager;
	private Sensor sensor;
	private float mAzimuth;
	private Context mContext;
	//////

	ProgressBar pb;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backend_demo);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		pb = (ProgressBar) findViewById(R.id.progress_bar);

		DiscoverLocation d = new DiscoverLocation(this);
		d.InitBluetoothLocationServices();
		d.registerLocationListener(this);
		d.scanForDevices();


		StepsManager s = StepsManager.getStepsManager();
		try {
			s.registerListener(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		startOrientation();
	}

	@Override
	public void newLocationHasBeenCalculated(final String LocationCalculated) {

		d("Newlocationhasbeencalculated in callback: " + LocationCalculated);
		final Integer loc;
		if(LocationCalculated != null)
			loc = Integer.parseInt(LocationCalculated);
		else
			loc = -1;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {	

				ImageView i = (ImageView) findViewById(R.id.imageView1);


				switch(loc){
				case 0:
					i.setImageResource(R.drawable.loc1);
					break;
				case 3:
					i.setImageResource(R.drawable.loc2); 
					break;
				case 4:
					i.setImageResource(R.drawable.loc3);
					break;
				case 5:
					i.setImageResource(R.drawable.loc4);
					break;
				case 6:
					i.setImageResource(R.drawable.loc5);
					break;
				case 7:
					i.setImageResource(R.drawable.loc6);
					break;
				case 8:
					i.setImageResource(R.drawable.loc7);
					break;
				}

				TextView ts = (TextView) findViewById(R.id.txtBluetoothLocation);
				ts.setText(LocationCalculated);
			}
		});
	}


	@Deprecated
	public void startOrientation() {
		sb = new StringBuilder();

		// Sensor init
		mContext = this;
		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);

		sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		if (sensor != null) {
			mSensorManager.registerListener(mySensorEventListener, sensor,
					SensorManager.SENSOR_DELAY_UI);
			d("Registerered for ORIENTATION Sensor");

		} else {
			d("ORIENTATION Sensor not found");
			Toast.makeText(mContext, "ORIENTATION Sensor not found",
					Toast.LENGTH_LONG).show();
		}
	}

	private SensorEventListener mySensorEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(final SensorEvent event) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {	

					mAzimuth = (int) event.values[0];
					TextView ts = (TextView) findViewById(R.id.txtOrientation); //INTENTIONAL
					ts.setText(Float.toString(mAzimuth));
				}
			});
		}
	};

	//Debug log function
	public static void d(String s){  
		Log.d("debug", s);
	}

	@Override
	public void stepHasBeenTaken(final int steps) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {	
				TextView ts = (TextView) findViewById(R.id.txtStepsTaken);
				ts.setText(Integer.toString(steps));
			}
		});

	}

	@Override
	public void stepHasBeenTaken(boolean hasAccurateDistance, int totalDistance) {
		// TODO Auto-generated method stub
		//use this for calibrated estimation (backend demo doesnt use calibration)
	}
}
