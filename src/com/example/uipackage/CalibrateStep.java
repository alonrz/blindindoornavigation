package com.example.uipackage;

import android.app.Activity;
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
		setContentView(R.layout.ui_main_layout);

	}


	@Override
	protected void onStop(){
		super.onStop();
		
		

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
		TextView currLoc = (TextView) findViewById(R.id.stepsCount);
		currLoc.setText(""+steps);
	}


	@Override
	public void stepHasBeenTaken(boolean hasAccurateDistance, int totalDistance) {
		// TODO Auto-generated method stub
		if(hasAccurateDistance == false)
			return;
		TextView calcDistance = (TextView) findViewById(R.id.calcDistance);
		calcDistance.setText("Distance: " + totalDistance);
		
	}


	public void onClickStart(View view) throws Exception{
		mStepsManager.registerListener(this);
	}
	
	
}