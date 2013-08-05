package com.example.blindindoornavigation;

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.*;
import android.content.Intent;

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


    protected void onActivityResult (int requestCode, int resultCode, Intent data)
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
    
}
