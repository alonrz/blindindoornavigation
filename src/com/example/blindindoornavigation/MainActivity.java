package com.example.blindindoornavigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class MainActivity extends Activity {
	
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
	
	public void onClick_Discover(View view)
    {   	
    	//Create a BroadcastReceiver for ACL connection success
    	final BroadcastReceiver mReceiver = new BroadcastReceiver() {
    	    public void onReceive(Context context, Intent intent) {
    	        String action = intent.getAction();
    			
    	        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
    	        	d("ACL success for: " + intent.getExtras().get("android.bluetooth.device.extra.DEVICE").toString());
    	        }
    	    }
    	};
    	
    	// Register the BroadcastReceiver
    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
    	registerReceiver(mReceiver, filter);// Don't forget to unregister during onDestroy    	
    	
    	//Spin off threads for attempted connection to expected beacons
    	//TODO make this list not hard coded, but gathered from an external map-based file.
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.add("90:00:4E:FE:34:E1");//Laptop
		stringList.add("78:A3:E4:A8:7E:48");//Phone
		
		for(int i=0; i<stringList.size(); i++){
			AcceptThread at = new AcceptThread(stringList.get(i));
			at.start();
		}
    }
    
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
	
	//Debug log function
	public static void d(String s){
		Log.d("debug", s);
	}
	//Debug toast function
	public void t(String s){
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////ACL Connection Thread Class///////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	
	private class AcceptThread extends Thread {
	    private String addr;
	 
	    public AcceptThread(String macAddress) {
	        addr = macAddress;
	    }
	 
	    @SuppressLint("NewApi")
		public void run() {
	        
	    	BluetoothSocket a;
	    	
	        BluetoothDevice macTest;
			macTest = adapter.getRemoteDevice(addr);//phone
			
	        d("macTest: " + macTest.toString());
	        UUID t = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  
	        
	        //Attempt to form a "connection" so that the low level ACL data will be sent -- which is all we want
	        //This try/catch block will always end in an IOexception. 
	        try{ 
	        	a = macTest.createInsecureRfcommSocketToServiceRecord(t);
	        	adapter.cancelDiscovery();
	        	adapter.listenUsingInsecureRfcommWithServiceRecord("blind", t);
	        	a.connect();
	        }catch(Exception e){
	        	d(addr + " " + e.toString());
	        }	   
	    }
	}
}





