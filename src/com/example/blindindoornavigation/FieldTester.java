package com.example.blindindoornavigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class FieldTester extends Activity{

	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.field_test);
	        
	 }
	  
	 public void onClick_StartWrite(View view) throws IOException{
		 List<String> items = new ArrayList<String>();
		 items.add("hello");
		 items.add("from");
		 items.add("list");
		 WriterUtility.writeStep(items);
	 }
	        
}
