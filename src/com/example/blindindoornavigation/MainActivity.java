package com.example.blindindoornavigation;

import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	Set<BluetoothDevice> beaconsInRange = new HashSet<BluetoothDevice>();
	BluetoothAdapter adapter;
	int REQUEST_ENABLE_BT = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView txtBTIndicatr = (TextView) findViewById(R.id.txtBTIndicator);
        adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter == null)
        {
        	Toast.makeText(this, "BluetoothAdapter returned NULL", Toast.LENGTH_LONG).show();
        	return;
        }
        //Check if bluetooth is enabled. If not, request it to be on
        if(adapter.isEnabled() == false)
        {
        	txtBTIndicatr.setText("BT is disabled");
        	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(enableBtIntent,  REQUEST_ENABLE_BT);  	
        }
        else
        {
        	txtBTIndicatr.setText("BT is enabled");
        }
               
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	TextView txtBTIndicatr = (TextView) findViewById(R.id.txtBTIndicator);
    	if(resultCode == Activity.RESULT_OK)
    		txtBTIndicatr.setText("BT is enabled");
    	else
    		txtBTIndicatr.setText("Error: BT is still disabled");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
  //Create a BroadcastReceiver for ACL connection success
	final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    @SuppressLint("NewApi")
		public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
			
	        //If this is true, then an ACL response was given by one of the beacons.  To get the beacon MAC that responded use intent.getExtras().get("android.bluetooth.device.extra.DEVICE").toString()
	        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
	        	BluetoothDevice bd = (BluetoothDevice) intent.getExtras().get("android.bluetooth.device.extra.DEVICE");
	        	d("ACL success for: " + bd.toString());
	        	beaconsInRange.add(bd);	
	        	d("adding: size:" + Integer.toString(beaconsInRange.size()));
	        }
	    }
	};
    
    
	/*public void onClick_Discover(View view)
    {   	
		CalculateLocation cl = new CalculateLocation();
		cl.start();
    	
    }*/
    
	
    @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@SuppressLint("NewApi")
	public void onClick_Undiscover(View view)
    {
		//TODO need to make sure correct registering unregistering of receiver.
		//unregisterReceiver(mReceiver);	
    }
	
	public void onClick_GoToTestActivity(View view)
	{
		Intent myIntent = new Intent(this, FieldTester.class);
		this.startActivity(myIntent);
	}

	public void onClick_ToSensorLand(View view)
	{
		Intent myIntent = new Intent(this, SensorActivity.class);
		this.startActivity(myIntent);
	}
	
	public void onClick_BackendDemo(View view)
	{
		Intent myIntent = new Intent(this, BackendDemo.class);
		this.startActivity(myIntent);
	}
	
	//Debug log function
	public static void d(String s){
		Log.d("debug", s);
	}
	//Debug toast function
	public void t(String s){
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}	
	
}





