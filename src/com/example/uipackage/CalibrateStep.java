package com.example.uipackage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.example.blindindoornavigation.R;
import com.example.blindindoornavigation.StepsListener;
import com.example.blindindoornavigation.StepsManager;

public class CalibrateStep extends Activity implements StepsListener {

	StepsManager mStepsManager = StepsManager.getStepsManager();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_calibrate_layout);
		try {
			mStepsManager.registerListener(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	protected void onStop(){
		super.onStop();
		mStepsManager.unregisterListener();
		
		

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mStepsManager.unregisterListener();
	}

	@Override
	protected void onStart(){
		super.onStart();
	}
	

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void stepHasBeenTaken(int steps) {
		// TODO Auto-generated method stub
		TextView currLoc = (TextView) findViewById(R.id.calcStepsTaken);
		currLoc.setText(""+steps);
	}


	@Override
	public void stepHasBeenTaken(boolean hasAccurateDistance, int totalDistance) {
		// TODO Auto-generated method stub
		if(hasAccurateDistance == false)
			return;
		TextView calcDistance = (TextView) findViewById(R.id.calcDistanceTaken);
		calcDistance.setText(""+ totalDistance);
		
	}


	
	
	public void onClick_StartCalibrate(View view) {
		// Ask use for file name
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Calibrate Steps");
		alertBuilder
				.setMessage("Press 'OK' and walk normally for 9m (~30f). Press Stop once you're done.");
		final CalibrateStep tmp = this;
		
		alertBuilder.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// Start calibration - reset fields
				mStepsManager.reset();
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
	
	public void onClick_StopCalibrate(View view) throws Exception 
	{	
		TextView txt = (TextView) findViewById(R.id.calcDistanceTaken);
		txt.setText("One step is: "+String.valueOf(mStepsManager.stopCalibration())+"\"");
	}
	
}