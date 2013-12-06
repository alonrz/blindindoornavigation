package com.example.uipackage;

import java.util.HashMap;

import indoorNavigationMap.*;
import indoorNavigationMap.VirtualSpot.AdjacentVS;

import com.example.blindindoornavigation.DiscoverLocation;
import com.example.blindindoornavigation.R;
import com.example.blindindoornavigation.StepsManager;
import com.example.blindindoornavigation.R.menu;
import com.example.blindindoornavigation.StepsListener;
import com.example.blindindoornavigation.LocationListener;

import android.media.ExifInterface;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class CurrentLocationActivity extends Activity implements
SensorEventListener, StepsListener, LocationListener {

	private HashMap<String, VirtualSpot> mapOfVirtualSpots;
	private VirtualSpot currentVirtualSpot, prevVirtualSpot = null;

	private int stepped;
	int stepsUntilNextVSdeducted;
	int stepsRemainingDeducted;
	private double stepsUntilNextVS, stepsRemaining;
	private int location, northRef = 1, direction;
	private int requiredNumOfSteps, forwardSteps;
	private boolean isDestinationReach, isNewVS = true, routeSet = false;
	private SensorManager mSensorManager;
	private Sensor mOrientation, mAccelerometer;
	private float[] mOrientation2 = new float[3];
	private float[] mRotationM = new float[9];
	private float[] mRemapedRotationM = new float[9];
	private float[] mGravs = new float[3];
	private float[] mGeoMags = new float[3];
	private float mAzimuth;
	private boolean mFailed;
	private FloorMap floorMap = new FloorMap();
	private MapHandler ninethFloor;
	private DiscoverLocation cl;

	private Route route;

	StepsManager mStepsManager = StepsManager.getStepsManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_main_layout);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mOrientation = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mapOfVirtualSpots = floorMap.getHashmapOfVirtualSpots();
		location = getIntent().getIntExtra("roomNumber", -1);
		
		ninethFloor = floorMap.getMap();
		TextView ts = (TextView) findViewById(R.id.calcDistanceTaken_cl);
		if (location != -1)
			ts.setText("Destination: " + location);
		isDestinationReach = false;
		requiredNumOfSteps = 10;
		forwardSteps = 0;
		stepsUntilNextVS = 0;
		stepsRemaining = 0;
		stepped = 0;
		stepsUntilNextVSdeducted = 0;
		stepsRemainingDeducted = 0;

		// ts.setText(route.getDirections());
		mStepsManager.reset();
		try {
			mStepsManager.registerListener(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cl = new DiscoverLocation(this);
		cl.InitBluetoothLocationServices();
		cl.registerLocationListener(this);
		cl.scanForDevices();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mOrientation,
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		try {
			mStepsManager.registerListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		mStepsManager.unregisterListener();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
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
		//TextView azi = (TextView) findViewById(R.id.azimuth);
		int degree = (int) (mAzimuth + 180) % 12;
		Matrix matrix = new Matrix();
		arrowView.setScaleType(ScaleType.MATRIX);
		matrix.postRotate(mAzimuth,
				arrowView.getDrawable().getBounds().width() / 2, arrowView
				.getDrawable().getBounds().height() / 2);
		arrowView.setImageMatrix(matrix);

		// if (index == 0)
		int tmp = Math.round(mAzimuth / 30) + 12;
		
		if (tmp > 12 && tmp <= 18) {
		//	azi.setText("" + (Math.round(mAzimuth / 30)));
			northRef = Math.round(mAzimuth / 30);
		} else {
		//	azi.setText("" + (Math.round(mAzimuth / 30) + 12));
			northRef = Math.round(mAzimuth / 30) + 12;
		}
		TextView dd = (TextView) findViewById(R.id.directDescription);
		TextView ts = (TextView) findViewById(R.id.remainDistance);
		if (routeSet) {
			int currentDirection = route.getVSList().get(0)
					.directionTo(route.getVSList().get(1));
			direction = (12 - (currentDirection - northRef)) % 12 + 1;
			if (stepsUntilNextVS - stepped >= 0) {
				stepsUntilNextVSdeducted = (int) stepsUntilNextVS - stepped;
				stepsRemainingDeducted = (int) stepsRemaining - stepped;
				if (stepsRemainingDeducted == 0){
					isDestinationReach = true;
				}
			}
			dd.setText("Turn to " + direction + " o'clock and walk straight");
			ts.setText("Go for " + stepsUntilNextVSdeducted
					+ " steps to reach next VS. \n" + stepsRemainingDeducted
					+ " steps remaining to reach destination.");
		}
		// displayDirection(direction);
	}

	private void displayDirection(int dir) {
		int offset = 1;
		TextView ts = (TextView) findViewById(R.id.calcDistanceTaken_cl);
		if (!isDestinationReach) {
			ts.setText("" + (12 - (northRef - direction)) % 12);
		} else {
			ts.setText("You've reached your destination");
		}

	}

	@Override
	public void stepHasBeenTaken(int steps) {
		// TODO Auto-generated method stub
		 if (direction > 9 && direction < 13 || direction > 0 && direction < 3)
			 stepped++;

		TextView currLoc = (TextView) findViewById(R.id.stepsCount);
		currLoc.setText("Steps: " + steps);
	}

	@Override
	public void stepHasBeenTaken(boolean hasAccurateDistance, int totalDistance) {
		// TODO Auto-generated method stub
		if (hasAccurateDistance == false)
			return;
		TextView calcDistance = (TextView) findViewById(R.id.calcDistanceTaken_cl);
		calcDistance.setText("Distance: " + totalDistance);

	}

	public void onClickStart(View view) throws Exception {
		TextView reset = (TextView) findViewById(R.id.stepsCount);
		reset.setText("Steps: 0");
		mStepsManager.reset();
	}

	public void onClickStartBT(View view) throws Exception {
		cl.InitBluetoothLocationServices();
		cl.registerLocationListener(this);
		cl.scanForDevices();

	}

	@Override
	public void newLocationHasBeenCalculated(final String LocationCalculated) {
		// TODO Auto-generated method stub
		final TextView vs = (TextView) findViewById(R.id.virtualSpot);
		d("newLocation = " + LocationCalculated);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//vs.setText("You're near virtual spot #" + "4");
				vs.setText("You're near virtual spot #" + LocationCalculated);
				//currentVirtualSpot = mapOfVirtualSpots.get("4");
				currentVirtualSpot = mapOfVirtualSpots.get(LocationCalculated);
				if (location != -1 && currentVirtualSpot != null) {
					if (prevVirtualSpot != currentVirtualSpot) {
						prevVirtualSpot = currentVirtualSpot;
					//	route = ninethFloor.getRouteVStoPoI("4",
					//			"" + location);
						route = ninethFloor.getRouteVStoPoI(LocationCalculated,
								"" + location);
						route.setCurrentLoc(currentVirtualSpot);
						stepsRemaining = route.stepsRemaining();
						stepsUntilNextVS = route.getDistNextTurnInSteps();
						routeSet = true;
						stepped = 0;
						mStepsManager.reset();
					}
					// ts.setText(""+direction);
					// ts.setText(currentVirtualSpot.getFirstPOI());
				} else {
					if (location == -1 && currentVirtualSpot != null) {
						vs.setText("Current location is near "+ currentVirtualSpot.getFirstPOI());
					} else
						vs.setText("You are out of this world!");
					
					routeSet = false;
				}

			}
		});

	}

	public static void d(String s) {
		Log.d("debug", s);
	}

}
