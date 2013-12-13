package com.example.blindindoornavigation;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BackendDemo extends Activity implements LocationListener, StepsListener {


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
		
		
	}

	@Override
	public void newLocationHasBeenCalculated(final String LocationCalculated) {
		d("Newlocationhasbeencalculated in callback: " + LocationCalculated);

		final Integer loc = Integer.parseInt(LocationCalculated);
		
		
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
			
				
				
				
				
				
				TextView ts = (TextView) findViewById(R.id.editText1);
				ts.setText(LocationCalculated);
			}
		});
	}

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
				TextView ts = (TextView) findViewById(R.id.editText2);
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
