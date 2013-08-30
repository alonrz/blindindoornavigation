package com.example.blindindoornavigation;

import android.os.Bundle;
import android.app.Activity;
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
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if(BluetoothDevice.ACTION_FOUND.equals(action))
			{
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device  = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a ListView
				TextView txtDeviceOne = (TextView)findViewById(R.id.txtDeviceOne);
				short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
				
				txtDeviceOne.setText(device.getName() + "\n" + device.getAddress() + 
				"\nRSSI value: " + rssi);
				
			}
		}
	};
	
    public void onClick_Discover(View view)
    {
    	if(adapter.startDiscovery()==false)
    	{
    		Toast.makeText(this, "Error discovering devices", Toast.LENGTH_SHORT).show();
    	}
    	// Register the BroadcastReceiver
    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    	registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    	Toast.makeText(this, "registered to receive device info", Toast.LENGTH_SHORT).show();
    }
    
    @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}

	public void onClick_Undiscover(View view)
    {
    	unregisterReceiver(mReceiver);
    }
	
	public void onClick_GoToTestActivity(View view)
	{
		Intent myIntent = new Intent(this, FieldTester.class);
		this.startActivity(myIntent);
		
		
	}
}
