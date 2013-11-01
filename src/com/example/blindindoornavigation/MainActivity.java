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
	
	//Debug log function
	public static void d(String s){
		Log.d("debug", s);
	}
	//Debug toast function
	public void t(String s){
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
	
	/*//////////////////////////////////////////////////////////////////////////////////////////
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
	        	adapter.listenUsingInsecureRfcommWithServiceRecord("blind", t);
	        	a.connect();
	        }catch(Exception e){
	        	d(addr + " " + e.toString());
	        }	   
	    }
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////Calculate Location Thread///////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////

	private class CalculateLocation extends Thread {

		public CalculateLocation() {

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
		
	
		@SuppressLint("NewApi")
		public void run() {
			
			// Register the BroadcastReceiver
	    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
	    	IntentFilter filterTwo = new IntentFilter(BluetoothDevice.EXTRA_RSSI);
	    	registerReceiver(mReceiver, filter);// Don't forget to unregister during onDestroy   
	    	registerReceiver(mReceiver, filterTwo);// Don't forget to unregister during onDestroy   
			
			//Spin off threads for attempted connection to expected beacons
	    	//TODO make this list not hard coded, but gathered from an external map-based file.
			ArrayList<String> stringList = new ArrayList<String>();
			stringList.add("90:00:4E:FE:34:E1"); //DLaptop
			stringList.add("78:A3:E4:A8:7E:48"); //DPhone
			//stringList.add("3C:D0:F8:6B:16:32"); //Bphone 
			stringList.add("20:C9:D0:85:58:5A"); //Alap
			stringList.add("D0:23:DB:24:81:46"); //APhone 
			stringList.add("38:0A:94:A8:F8:76"); //Aandroid 
			
			
			adapter.cancelDiscovery();
			for(int i=0; i<stringList.size(); i++){
				AcceptThread at = new AcceptThread(stringList.get(i));  
				at.start();
			}
			
			//Wait 5 seconds for threads to all return (~5 seconds for time out, 2-4 for ACL connection return)
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			calculateLocation();
			
		}
		
		
		private void calculateLocation(){
			//TODO Calculations for location.  
			//AT this point in execution 'beaconsInRange' is a set of bluetooth 'beacons' (devices) that are 'seen' 
			//For our test
			
			d(beaconsInRange.toString());
			
			List<String> ls = new ArrayList<String>();
			List<String> lsSorted = new ArrayList<String>();
			Iterator<BluetoothDevice> iter = beaconsInRange.iterator();
			while (iter.hasNext()) {
				ls.add(iter.next().toString());
			}
			
			WriterUtility wt = new WriterUtility();
			d("list: " + ls.toString());
			

			
			if(ls.contains("90:00:4E:FE:34:E1")){//DLaptop
				lsSorted.add("90:00:4E:FE:34:E1");
			}else {lsSorted.add("null");}
			
			if(ls.contains("78:A3:E4:A8:7E:48")){//DPhone
				lsSorted.add("78:A3:E4:A8:7E:48");
			}else{lsSorted.add("null");}
			
			if(ls.contains("20:C9:D0:85:58:5A")){//Alap
				lsSorted.add("20:C9:D0:85:58:5A");
			}else{lsSorted.add("null");}
			
			if(ls.contains("D0:23:DB:24:81:46")){//APhone
				lsSorted.add("D0:23:DB:24:81:46");
			}else{lsSorted.add("null");}
			
			if(ls.contains("38:0A:94:A8:F8:76")){//Aandroid
				lsSorted.add("38:0A:94:A8:F8:76"); 
			}else{lsSorted.add("null");}
			
			
			
			try {
				d("before pass: " + lsSorted.toString());
				wt.writeStep(lsSorted);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}
		
	}*/
	
	
}





