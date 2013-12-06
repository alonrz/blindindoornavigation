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
		
		String[] values = new String[] { "Room 935", "Room 934", "Room 906", "Room 907",
				"Room 911", "Room 913", "Room 914"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		        android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
		
	}
	
	@Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
	    String item = (String) getListAdapter().getItem(position);
	    if (item.equals("Room 935")){
	    	Intent intent = new Intent(this, CurrentLocationActivity.class);
	    	intent.putExtra("roomNumber", 935);
	    	this.startActivity(intent);
	    }
	    else if (item.equals("Room 934")){
	    	Intent intent = new Intent(this, CurrentLocationActivity.class);
	    	intent.putExtra("roomNumber", 934);
	    	this.startActivity(intent);
	    }
	    else if (item.equals("Room 906")){
	    	Intent intent = new Intent(this, CurrentLocationActivity.class);
	    	intent.putExtra("roomNumber", 906);
	    	this.startActivity(intent);
	    }
	    else if (item.equals("Room 907")){
	    	Intent intent = new Intent(this, CurrentLocationActivity.class);
	    	intent.putExtra("roomNumber", 907);
	    	this.startActivity(intent);
	    }
	    else if (item.equals("Room 911")){
	    	Intent intent = new Intent(this, CurrentLocationActivity.class);
	    	intent.putExtra("roomNumber", 911);
	    	this.startActivity(intent);
	    }
	    else if (item.equals("Room 913")){
	    	Intent intent = new Intent(this, CurrentLocationActivity.class);
	    	intent.putExtra("roomNumber", 913);
	    	this.startActivity(intent);
	    }
	    else if (item.equals("Room 914")){
	    	Intent intent = new Intent(this, CurrentLocationActivity.class);
	    	intent.putExtra("roomNumber", 914);
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
