package com.example.blindindoornavigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FieldTester extends Activity {

	boolean isUndoEnabled = false;
	WriterUtility mWriter = new WriterUtility(this);

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.field_test);
	}

	public void onClick_StartWrite(View view) throws IOException 
	{
		mWriter.startTest();
		Button btnStart = (Button) findViewById(R.id.StartWrite);
		btnStart.setEnabled(false);
		Button btnStop = (Button) findViewById(R.id.StopAndSave);
		btnStop.setEnabled(true);

		Button btnRecordStep = (Button) findViewById(R.id.RecordStep);
		btnRecordStep.setEnabled(true);
		Button btnUndo = (Button) findViewById(R.id.Undo);
		btnUndo.setEnabled(false);
	}

	public void onClick_StopAndSave(View view) throws IOException 
	{
		mWriter.endTestAndSave();
		Button btnStart = (Button) findViewById(R.id.StartWrite);
		btnStart.setEnabled(true);
		Button btnStop = (Button) findViewById(R.id.StopAndSave);
		btnStop.setEnabled(false);
		
		Button btnRecordStep = (Button) findViewById(R.id.RecordStep);
		btnRecordStep.setEnabled(false);
		Button btnUndo = (Button) findViewById(R.id.Undo);
		btnUndo.setEnabled(false);
	}

	public void onClick_RecordStep(View view) throws IOException 
	{
		int n = mWriter.writeStep(new ArrayList<String>(Arrays.asList(
				"name of device", "RSSI", "MAC address")));
		
		if (isUndoEnabled == false) {
			Button btnUndo = (Button) findViewById(R.id.Undo);
			btnUndo.setEnabled(true);
			isUndoEnabled = true;
		}
		
		//Change number on button
		Button btnRecordStep = (Button) findViewById(R.id.RecordStep);
		btnRecordStep.setText("Record Step #" + (n+1)); 
	}

	public void onClick_Undo(View view) {
		int n = mWriter.undoStep();
		if(n == 0){
			Button btnUndo = (Button) findViewById(R.id.Undo);
			btnUndo.setEnabled(false);
			isUndoEnabled = false;
		}
		Button btnRecordStep = (Button) findViewById(R.id.RecordStep);
		btnRecordStep.setText("Record Step #" + (n+1));
	}
}
