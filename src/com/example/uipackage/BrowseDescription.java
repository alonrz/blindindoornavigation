package com.example.uipackage;

import com.example.blindindoornavigation.R;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BrowseDescription extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.ui_main_layout);
		
		String[] values = new String[] { "Prof Yoon's Office", "Prof Puder's Office", "CS Office"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		        android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
		
	}
	
	@Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
	    String item = (String) getListAdapter().getItem(position);
	    if (item.equals("Prof Yoon's Office")){
	    	Intent intent = new Intent(this, CurrentLocationActivity.class);
	    	intent.putExtra("roomNumber", 910);
	    	this.startActivity(intent);
	    }
	    else if (item.equals("Prof Puder's Office")){
	    	Intent intent = new Intent(this, CurrentLocationActivity.class);
	    	intent.putExtra("roomNumber", 909);
	    	this.startActivity(intent);
	    }
	    else if (item.equals("CS Office")){
	    	Intent intent = new Intent(this, CurrentLocationActivity.class);
	    	intent.putExtra("roomNumber", 906);
	    	this.startActivity(intent);
	    }
	    
	  }
	
	@Override
	protected void onStop(){
		super.onStop();
		
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui_main, menu);
		return true;
	}

}
