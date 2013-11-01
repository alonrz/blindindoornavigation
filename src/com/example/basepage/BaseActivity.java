package com.example.basepage;

import com.example.blindindoornavigation.MainActivity;
import com.example.blindindoornavigation.R;
import com.example.blindindoornavigation.R.layout;
import com.example.blindindoornavigation.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);
		return true;
	}
	
	public void onClick_Backend(View view)
	{
		Intent intent = new Intent(this, MainActivity.class);
		this.startActivity(intent);
	}

}
