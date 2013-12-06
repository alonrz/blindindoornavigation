package com.example.uipackage;

//import com.example.indoorNavigationMap.*;
import com.example.blindindoornavigation.R;
import com.example.blindindoornavigation.R.menu;
import com.example.blindindoornavigation.StepsListener;


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;


public class RouteActivity extends Activity implements SensorEventListener, StepsListener, LocationListener {

	private SensorManager mSensorManager;
	private Sensor mOrientation, mAccelerometer;
	private float[] mOrientation2 = new float[3];
	private float[] mRotationM = new float[9];
	private float[] mRemapedRotationM = new float[9];
	private float[] mGravs = new float[3];
	private float[] mGeoMags = new float[3];
	private float mAzimuth;
	private boolean mFailed;
//	private MapHandler map = new MapHandler();;
//	private PointofInterest poi_1, poi_2, poi_3, poi_4, poi_5, poi_6, poi_7, poi_8;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_main_layout);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mOrientation = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	/*	
		VirtualSpot vs_1 = new VirtualSpot("1", 0, 0);
		VirtualSpot vs_2 = new VirtualSpot("2", 0, 0);
		VirtualSpot vs_3 = new VirtualSpot("3", 0, 0);
		VirtualSpot vs_4 = new VirtualSpot("4", 0, 0);
		VirtualSpot vs_5 = new VirtualSpot("5", 0, 0);
		VirtualSpot vs_6 = new VirtualSpot("6", 0, 0);
		VirtualSpot vs_7 = new VirtualSpot("7", 0, 0);
		VirtualSpot vs_8 = new VirtualSpot("8", 0, 0);
		
		map.addVS(vs_1);
		map.addVS(vs_2);
		map.addVS(vs_3);
		map.addVS(vs_4);
		map.addVS(vs_5);
		map.addVS(vs_6);
		map.addVS(vs_7);
		map.addVS(vs_8);
		
		poi_1 = new PointofInterest("909", "Puder's office", vs_1);
		poi_2 = new PointofInterest("910", "Yoon's office", vs_2);
		poi_3 = new PointofInterest("906", "room 906", vs_3);
		poi_4 = new PointofInterest("911", "room 911", vs_3);
		poi_5 = new PointofInterest("912", "room 912", vs_3);
		poi_6 = new PointofInterest("913", "room 913", vs_3);
		poi_7 = new PointofInterest("914", "room 914", vs_3);
		poi_8 = new PointofInterest("915", "room 915", vs_3);
		
		vs_1.addPointofInterest(poi_1, 9);
		vs_2.addPointofInterest(poi_2, 9);
		vs_3.addPointofInterest(poi_3, 9);
		vs_4.addPointofInterest(poi_4, 9);
		vs_5.addPointofInterest(poi_5, 9);
		vs_6.addPointofInterest(poi_6, 9);
		vs_7.addPointofInterest(poi_7, 9);
		vs_8.addPointofInterest(poi_8, 9);
		
		
		vs_1.setNextVirtualSpotByDirection(vs_2, 12);
		vs_2.setNextVirtualSpotByDirection(vs_3, 12);
		vs_3.setNextVirtualSpotByDirection(vs_4, 12);
		vs_5.setNextVirtualSpotByDirection(vs_6, 12);
		vs_6.setNextVirtualSpotByDirection(vs_7, 12);
		vs_7.setNextVirtualSpotByDirection(vs_8, 12);
		
		*/
		
	}


	@Override
	protected void onStop(){
		super.onStop();

	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mOrientation,
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	protected void onStart(){
		super.onStart();
	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){

	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui_main, menu);
		return true;
	}


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}


	private void onSuccess() {
		if (mFailed)
			mFailed = false;
		mAzimuth = (float) -Math.round((Math.toDegrees(mOrientation2[0])) * 2) / 2;

	}

	private void onFailure() {
		if (!mFailed) {
			mFailed = true;
		}
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			for (int i = 0; i < 3; i++)
				mGravs[i] = event.values[i];
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			for (int i = 0; i < 3; i++)
				mGeoMags[i] = event.values[i];
			break;
		default:
			return;
		}

		if (SensorManager.getRotationMatrix(mRotationM, null, mGravs, mGeoMags)) {

			SensorManager.remapCoordinateSystem(mRotationM,
					SensorManager.AXIS_X, SensorManager.AXIS_Y,
					mRemapedRotationM);
			SensorManager.getOrientation(mRemapedRotationM, mOrientation2);
			onSuccess();

		} else
			onFailure();
		ImageView arrowView = (ImageView) findViewById(R.id.arrowContainer);
		arrowView.setImageResource(R.drawable.arrow);
	//	TextView azi = (TextView) findViewById(R.id.azimuth);
	//	azi.setText("" + mAzimuth);
		Matrix matrix = new Matrix();
		arrowView.setScaleType(ScaleType.MATRIX);
		matrix.postRotate(mAzimuth,
				arrowView.getDrawable().getBounds().width() / 2, arrowView
				.getDrawable().getBounds().height() / 2);
		arrowView.setImageMatrix(matrix);

	}


	@Override
	public void stepHasBeenTaken(int steps) {
		TextView currLoc = (TextView) findViewById(R.id.stepsCount);
		currLoc.setText(""+steps);
		
		
	}


	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void stepHasBeenTaken(boolean hasAccurateDistance, int totalDistance) {
		// TODO Auto-generated method stub
		
	}

}
