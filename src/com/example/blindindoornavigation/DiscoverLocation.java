package com.example.blindindoornavigation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;



public class DiscoverLocation {
	
	Hashtable<String, Short> rssiVal = new Hashtable<String, Short>();
	//Hashtable<String, String> deviceName = new Hashtable<String, String>();
	Hashtable<Integer, String> colOrder= new Hashtable<Integer, String>(); //used for pilot test to keep certain beacons in column order
	Hashtable<String, String> pointsOfInterest = new Hashtable<String, String>(); //This will most likely be pulled from a database, but currently its just hard coded
	List<String> lsSorted = new ArrayList<String>();
	List<String> temp = new ArrayList<String>();
	boolean firstTimeBoolean = true;
	String CalculatedLocation = "-1";
	Context activityContext;

	BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	Set<BluetoothDevice> beaconsInRange = new HashSet<BluetoothDevice>();

	LocationListener mListener = null;
	
	

	/**
	 * expects a array of physical hardware MAC addresses in the order they are set up in a building.  
	 * I.e. if beacon 1 is next to beacon 2 which is next to beacon 3, the list has to have MAC of beacon 
	 * one, MAC of beacon two, MAC of beacon three... etc. 
	 * 
	 * @param ListOfBluetoothBeacons
	 * @return 
	 */
	void InitBluetoothLocationServices(String[] ListOfBluetoothBeacons)
	{

		int locationCounter=0;
		for(int i = 0; i < ListOfBluetoothBeacons.length; i++){
		
				pointsOfInterest.put(ListOfBluetoothBeacons[i], Integer.toString(locationCounter++));	
				
				if(i < ListOfBluetoothBeacons.length - 1){
					pointsOfInterest.put(ListOfBluetoothBeacons[i].concat(ListOfBluetoothBeacons[i+1]), Integer.toString(locationCounter));
					pointsOfInterest.put(ListOfBluetoothBeacons[i+1].concat(ListOfBluetoothBeacons[i]), Integer.toString(locationCounter++));
				}

		}
		//d(pointsOfInterest.toString());
	}
	
	int InitBluetoothLocationServices()
	{
		/*deviceName.put("90:00:4E:FE:34:E1", "David's Laptop");
		deviceName.put("78:A3:E4:A8:7E:48", "David's iPhone"); 
		deviceName.put("20:C9:D0:85:58:5A", "Alon's Laptop");
		deviceName.put("D0:23:DB:24:81:46", "Alon's iPhone");
		deviceName.put("38:0A:94:A8:F8:76", "Alon's Android");
		deviceName.put("CC:08:E0:A8:02:27", "B old iphone4");
		deviceName.put("CC:08:E0:96:34:4D", "D old iphone4");
		deviceName.put("00:02:72:C6:A3:C6", "D Laptop beacon");
		deviceName.put("00:02:72:C6:A3:A4", "D old laptop beacon");
		deviceName.put("00:02:72:C6:A3:89", "B old laptop beacon");
		deviceName.put("00:02:72:3F:4E:8B", "Beacon 4");*/

		//////////////////////////////////////////////////////////////////////////////////////////////
		//colOrder is used to make sure the write-to-database is always written in the correct order
		//so rows/col stay static
		//////////////////////////////////////////////////////////////////////////////////////////////
		
		
		colOrder.put(0, "90:00:4E:FE:34:E1");
		colOrder.put(1, "78:A3:E4:A8:7E:48");
		colOrder.put(2, "20:C9:D0:85:58:5A");
		colOrder.put(3, "D0:23:DB:24:81:46");
		colOrder.put(4, "38:0A:94:A8:F8:76");  
		colOrder.put(5, "CC:08:E0:A8:02:27");
		colOrder.put(6, "CC:08:E0:96:34:4D");
		colOrder.put(7, "00:02:72:C6:A3:C6");
		colOrder.put(8, "00:02:72:C6:A3:A4");
		colOrder.put(9, "00:02:72:C6:A3:89");
		colOrder.put(10, "00:02:72:3F:4E:8B"); 


		for(int i=0;i<colOrder.size(); i++){
			temp.add("syncPulse");
		}
		
		
		pointsOfInterest.put("CC:08:E0:96:34:4D", "0");
		pointsOfInterest.put("CC:08:E0:96:34:4DCC:08:E0:A8:02:27", "1"); // between b and d old iphone4
		pointsOfInterest.put("CC:08:E0:A8:02:27CC:08:E0:96:34:4D", "1"); // between b and d old iphone4
		
		pointsOfInterest.put("CC:08:E0:A8:02:27", "2");
		
		
		pointsOfInterest.put("00:02:72:C6:A3:A4", "At New Laptop");
		pointsOfInterest.put("00:02:72:C6:A3:A490:00:4E:FE:34:E1", "between");
		pointsOfInterest.put("90:00:4E:FE:34:E100:02:72:C6:A3:A4", "between");
		
		pointsOfInterest.put("90:00:4E:FE:34:E1", "at Old Laptop");
		
		/////////////////////////////////////////////////////////////////////////
		//TODO Currently only three beacons and their mid points are hard coded.
		//TODO this kind of information should be pulled from a database.
		//TODO this current chunk of code is for demo purposes only.
		////////////////////////////////////////////////////////////////////////
		/*pointsOfInterest.put("CC:08:E0:96:34:4D", "0");
		pointsOfInterest.put("CC:08:E0:96:34:4DCC:08:E0:A8:02:27", "1"); // between b and d old iphone4
		pointsOfInterest.put("CC:08:E0:A8:02:27CC:08:E0:96:34:4D", "1"); // between b and d old iphone4
		
		pointsOfInterest.put("CC:08:E0:A8:02:27", "2");
		
		pointsOfInterest.put("CC:08:E0:A8:02:2778:A3:E4:A8:7E:48", "3");
		pointsOfInterest.put("78:A3:E4:A8:7E:48CC:08:E0:A8:02:27", "3");
		
		pointsOfInterest.put("78:A3:E4:A8:7E:48", "4");

		pointsOfInterest.put("78:A3:E4:A8:7E:4838:0A:94:A8:F8:76", "5");
		pointsOfInterest.put("38:0A:94:A8:F8:7678:A3:E4:A8:7E:48", "5");

		pointsOfInterest.put("38:0A:94:A8:F8:76", "6");
		
		pointsOfInterest.put("38:0A:94:A8:F8:7620:C9:D0:85:58:5A", "7");
		pointsOfInterest.put("20:C9:D0:85:58:5A38:0A:94:A8:F8:76", "7");

		pointsOfInterest.put("20:C9:D0:85:58:5A", "8");

		pointsOfInterest.put("20:C9:D0:85:58:5A90:00:4E:FE:34:E1", "9");
		pointsOfInterest.put("90:00:4E:FE:34:E120:C9:D0:85:58:5A", "9");

		pointsOfInterest.put("90:00:4E:FE:34:E1", "10");

		pointsOfInterest.put("90:00:4E:FE:34:E1CC:08:E0:96:34:4D", "11");
		pointsOfInterest.put("CC:08:E0:96:34:4D90:00:4E:FE:34:E1", "11"); 

		pointsOfInterest.put("00:02:72:C6:A3:89", "12");
		
		pointsOfInterest.put("00:02:72:C6:A3:8900:02:72:3F:4E:8B", "13");
		pointsOfInterest.put("00:02:72:3F:4E:8B00:02:72:C6:A3:89", "13");

		pointsOfInterest.put("00:02:72:3F:4E:8B", "14");
		
		pointsOfInterest.put("00:02:72:3F:4E:8B00:02:72:C6:A3:C6", "15");
		pointsOfInterest.put("00:02:72:C6:A3:C600:02:72:3F:4E:8B", "15");
		
		pointsOfInterest.put("00:02:72:C6:A3:C6", "16");
		
		pointsOfInterest.put("00:02:72:C6:A3:C600:02:72:C6:A3:A4", "17");
		pointsOfInterest.put("00:02:72:C6:A3:A400:02:72:C6:A3:C6", "17");
		
		pointsOfInterest.put("00:02:72:C6:A3:A4", "18");*/




		return 0;
	}

	
	
	
	
	
	
	DiscoverLocation(Context c){
		activityContext = c;
		
		//TODO remove hardcoded mac addresses
		//For testing purposes, D laptop, D iphone, D old laptop beacon, B old laptop beacon, Beacon 4	
		String[] DeviceOrder = {"90:00:4E:FE:34:E1","78:A3:E4:A8:7E:48","00:02:72:C6:A3:A4","00:02:72:C6:A3:89","00:02:72:3F:4E:8B"};
				
		//InitBluetoothLocationServices();
		InitBluetoothLocationServices(DeviceOrder);
		
	}
	//Debug log function
		public static void d(String s){
			Log.d("debug", s);
		}

		public void scanForDevices(){
			
			beaconsInRange.clear();
			rssiVal.clear();
			adapter.cancelDiscovery();

			
			ScanForDevices ct = new ScanForDevices();
			ct.start();//Starts the location finding algorithm
		}
		
		//TODO
		//TODO NOT CURRENTLY USING THIS FOR PROTOTYPING SESSIONS, NEED TO ADD IT AGAIN TO DO PILOT DATA!!!!!
		//TODO
		public void sortColumns(){
			//TODO Calculations for location.  
			//AT this point in execution 'beaconsInRange' is a set of bluetooth 'beacons' (devices) that are 'seen' 
			//For our test

			d(beaconsInRange.toString());

			List<String> ls = new ArrayList<String>();

			Iterator<BluetoothDevice> iter = beaconsInRange.iterator();
			while (iter.hasNext()) {
				ls.add(iter.next().toString());
			}

			String macTemp;
			for(int i = 0; i<colOrder.size();i++)
			{
				if(ls.contains(colOrder.get(i))){
					macTemp = ls.get(ls.indexOf(colOrder.get(i)));
					lsSorted.add(macTemp);
					lsSorted.add(Short.toString(rssiVal.get(macTemp)));
				}
				else
				{
					lsSorted.add("null");
					lsSorted.add("null");
				}
			}		

			lsSorted.add(CalculatedLocation);

			ls.clear();



		}

		public void registerLocationListener(LocationListener listener)
	    {
	            mListener = listener;
	    }
		
		private class discover extends Thread {

			public discover() {

			}

			@SuppressLint("NewApi")
			public void run() {

				d("birhererere: " + beaconsInRange.toString());
				beaconsInRange.clear();
				rssiVal.clear();
				lsSorted.clear(); 


				adapter.startDiscovery();
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				adapter.cancelDiscovery();
				//try {
					
					//sortColumns();
					locatePosition();
					//mWriter.writeStep(lsSorted);
					d("writeStep lsSroted:" + lsSorted);
				//} catch (IOException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				//}
					
				
				newLocationHasBeenCalculated();
				discover d = new discover();
				d.start();  
			}


			void newLocationHasBeenCalculated()
	        {
				if(mListener != null)
					mListener.newLocationHasBeenCalculated(CalculatedLocation);
				else
					d("Error! mlisteners is null!");
	        }
			
			private void locatePosition(){
				//////////////////////////////////////////////////////////
				//Find the closest beacon
				//////////////////////////////////////////////////////////
				Iterator<BluetoothDevice> biri = beaconsInRange.iterator();

				BluetoothDevice tbd;
				Short temp;
				int currentHighest = -9999;
				BluetoothDevice closestBeacon = null;
				while(biri.hasNext())
				{
					tbd = biri.next();
					temp = rssiVal.get(tbd.toString());

					if(temp > currentHighest){
						closestBeacon = tbd;
						currentHighest = temp;
					}
				}
				final BluetoothDevice tempClosestBeacon = closestBeacon;
				d(rssiVal.toString());

				///////////////////////////////////////////////////////////
				//Find the second closest beacon
				///////////////////////////////////////////////////////////
				Iterator<BluetoothDevice> biri2 = beaconsInRange.iterator();
				int tempCurrentHighest = -9999;
				BluetoothDevice secondClosestBeacon = null;
				while(biri2.hasNext())
				{
					tbd = biri2.next();
					temp = rssiVal.get(tbd.toString());
					if((temp > tempCurrentHighest) ){
						if(tbd != tempClosestBeacon)
						{
							secondClosestBeacon = tbd;
							tempCurrentHighest = temp;
						}

					}
				}

				final BluetoothDevice tempSecondClosestBeacon = secondClosestBeacon;

				d("tempclosest: " + tempClosestBeacon);
				///////////////////////////////////////
				//Find location
				///////////////////////////////////////
				if(tempClosestBeacon !=null && tempSecondClosestBeacon !=null)
				{
					int rssiOne = rssiVal.get(tempClosestBeacon.toString());
					int rssiTwo = rssiVal.get(tempSecondClosestBeacon.toString());

					final int  BEACON_LOCATION_SENSITIVITY = 7;
					if((rssiOne - rssiTwo) > BEACON_LOCATION_SENSITIVITY)
					{
						////////////////////////////////////////////////////////////////////////////////////
						//closer to rssiOne beacon, so pull the correct data for that location
						////////////////////////////////////////////////////////////////////////////////////
						//TODO runonuithread
						/*runOnUiThread(new Runnable() {
							@Override
							public void run() {	
								Button btnUndo = (Button) findViewById(R.id.StopAndSave);
								*/if(tempClosestBeacon != null && tempSecondClosestBeacon != null) 
								{
									//btnUndo.setText(pointsOfInterest.get(tempClosestBeacon.toString()));
									CalculatedLocation = pointsOfInterest.get(tempClosestBeacon.toString());
								}else {
									//btnUndo.setText("didnt find two beacons");
								}/*
							}
						});*/
					}
					else if ((rssiOne - rssiTwo) > -(BEACON_LOCATION_SENSITIVITY) && (rssiOne - rssiTwo) < BEACON_LOCATION_SENSITIVITY)
					{
						////////////////////////////////////////////////////////////////////////////////////
						//Halfway between two closest beacons, so pull the correct data for that location
						////////////////////////////////////////////////////////////////////////////////////
						//TODO runonuithread
						/*runOnUiThread(new Runnable() {
							@Override
							public void run() {	
								Button btnUndo = (Button) findViewById(R.id.StopAndSave);
								if(tempClosestBeacon != null && tempSecondClosestBeacon != null)
								{
									btnUndo.setText(pointsOfInterest.get(tempClosestBeacon.toString() + tempSecondClosestBeacon.toString()));
									CalculatedLocation = pointsOfInterest.get(tempClosestBeacon.toString() + tempSecondClosestBeacon.toString());
								}
								else { 
									btnUndo.setText("didnt find two beacons");
								}
							}
						});*/
					}
				}
			}
		}


		

		//////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////Calculate Location Thread///////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////

		private class ScanForDevices extends Thread   {
			
			public ScanForDevices() {

			}

			//Create a BroadcastReceiver for ACL connection success
			final BroadcastReceiver mReceiver = new BroadcastReceiver() {
				@SuppressLint("NewApi")
				public void onReceive(Context context, Intent intent) {
					String action = intent.getAction(); 

					if (BluetoothDevice.ACTION_FOUND.equals(action)) {
						BluetoothDevice bd = (BluetoothDevice) intent.getExtras().get("android.bluetooth.device.extra.DEVICE");

						rssiVal.put(bd.toString(), intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
						beaconsInRange.add(bd);

						//////////////////////////////////////////////////////////////////////////////////////////
						//runOnUiThread is used currently for a quick and dirty way to output using button texts
						///////////////////////////////////////////////////////////////////////////////////////////
						//TODO runonuithread
						
						/*runOnUiThread(new Runnable() {
							@Override
							public void run() {	
								//Toast.makeText(FieldTester.this, "The closest beacon is: " + deviceName.get(tempClosestBeacon.toString()), Toast.LENGTH_LONG).show();




								Button btnRecordStep = (Button) findViewById(R.id.StartWrite);
								btnRecordStep.setText("David's Laptop: " + rssiVal.get("90:00:4E:FE:34:E1") +
										"\nDavid's iPhone: " + rssiVal.get("78:A3:E4:A8:7E:48")+
										//"\nAlon's Laptop: " + rssiVal.get("20:C9:D0:85:58:5A")+
										//"\nAlon's iPhone: " + rssiVal.get("D0:23:DB:24:81:46")+
										//"\nAlon's Android: " + rssiVal.get("38:0A:94:A8:F8:76")+
										"\nB old iphone4: " + rssiVal.get("CC:08:E0:A8:02:27")+
										"\nD old iphone4: " + rssiVal.get("CC:08:E0:96:34:4D")+
										"\nD new laptop: " + rssiVal.get("00:02:72:C6:A3:C6")+
										"\nD old laptop: " + rssiVal.get("00:02:72:C6:A3:A4")+
										"\nB old laptop: " + rssiVal.get("00:02:72:C6:A3:89")+
										"\nBeacon 4: " + rssiVal.get("00:02:72:3F:4E:8B")
										);


							}
						});*/


					}

					if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
					{
						d("discovery started");
					}
					if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
					{
						d("discovery finished");
					}



				}
			};


			@SuppressLint("NewApi")
			public void run() {


				// Register the BroadcastReceiver
				//IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
				IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
				IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
				IntentFilter filter4 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);

				// TODO Don't forget to unregister during onDestroy  (currently never unregisters)
				activityContext.registerReceiver(mReceiver, filter2);
				activityContext.registerReceiver(mReceiver, filter3);
				activityContext.registerReceiver(mReceiver, filter4);


				////////////////////////////////////////////////////////////////////////////////
				//start an inquiry scan and then wait for ~11 seconds for the result
				////////////////////////////////////////////////////////////////////////////////
				d("::::::Starting::::::");
				//adapter.startDiscovery();
				discover d = new discover();
				d.start();

				//sortColumns();
				//runOnUiThread(new Runnable() {
					//@Override
					//public void run() {	
						//Toast.makeText(FieldTester.this, "Write is done.", Toast.LENGTH_LONG).show();	
						//Button btnRecordStep = (Button) findViewById(R.id.RecordStep);
						//btnRecordStep.setEnabled(true);

						//Button btnUndo = (Button) findViewById(R.id.Undo);
						//btnUndo.setText(CalculatedLocation);

					//}
				//});
			}







		}
		
		
		
		
		
}
