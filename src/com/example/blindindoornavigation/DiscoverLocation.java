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
	}

	public int InitBluetoothLocationServices()
	{
	
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

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//create points of interest based off of beacons in system (default constructor is just using hardcoded beacon ids)
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		pointsOfInterest.put("00:02:72:3F:40:5F", "0");
		pointsOfInterest.put("00:02:72:3F:40:5F00:02:72:3F:4E:8B", "1"); 
		pointsOfInterest.put("00:02:72:3F:4E:8B00:02:72:3F:40:5F", "1");

		pointsOfInterest.put("00:02:72:3F:4E:8B", "2");
		pointsOfInterest.put("00:02:72:3F:4E:8B00:02:72:C6:A3:A4", "3"); 
		pointsOfInterest.put("00:02:72:C6:A3:A400:02:72:3F:4E:8B", "3");

		pointsOfInterest.put("00:02:72:C6:A3:A4", "4");
		pointsOfInterest.put("00:02:72:C6:A3:A400:02:72:C6:A3:89", "5"); 
		pointsOfInterest.put("00:02:72:C6:A3:8900:02:72:C6:A3:A4", "5");

		pointsOfInterest.put("00:02:72:C6:A3:89", "6");
		pointsOfInterest.put("00:02:72:C6:A3:8900:02:72:C6:A3:C6", "7"); 
		pointsOfInterest.put("00:02:72:C6:A3:C600:02:72:C6:A3:89", "7");

		pointsOfInterest.put("00:02:72:C6:A3:C6", "8");
		/*USE THIS CHUNK FOR ALONS LAST TWO
		pointsOfInterest.put("", "9"); 
		pointsOfInterest.put("", "9");

		pointsOfInterest.put("", "10");
		pointsOfInterest.put("", "11"); 
		pointsOfInterest.put("", "11");

		pointsOfInterest.put("", "12");*/
		///////////////////////////////////////

		return 0;
	}

	public DiscoverLocation(Context c){
		activityContext = c;

		//NOTE: for prototyping we are currently just using default constructor and hard-coded beacons. 
		//"void InitBluetoothLocationServices(String[] ListOfBluetoothBeacons)" is more 'ideal' way

		InitBluetoothLocationServices();
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

	public void registerLocationListener(LocationListener listener)
	{
		mListener = listener;
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////Discover Location Thread (used as loop)///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	private class discover extends Thread {

		public discover() {

		}

		@SuppressLint("NewApi")
		public void run() {

			//////////////////////////////////////////////////////////////////
			//Clear data storage from discovered devices from previous loop
			//////////////////////////////////////////////////////////////////
			beaconsInRange.clear();
			rssiVal.clear();
			lsSorted.clear(); 

			/////////////////////////////////////////////////////////////////////////////////////////////////////
			//Sleep for 5 seconds to allow bluetooth device discovery.
			//Note, this is arbitrary and may need to be modified depending on the UI algorithm.
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			adapter.startDiscovery();
			try {
				sleep(5000); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			adapter.cancelDiscovery();

			//calculate the position
			locatePosition();

			//trigger callback
			newLocationHasBeenCalculated();

			//Restart the discovery loop
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

			////////////////////////////////////////////////////////////////
			//Find location based off closes and second closest beacon
			////////////////////////////////////////////////////////////////
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
					if(tempClosestBeacon != null && tempSecondClosestBeacon != null) 
					{
						CalculatedLocation = pointsOfInterest.get(tempClosestBeacon.toString());
					}else {
						d("didn't find two beacons");
					}
				}
				else if ((rssiOne - rssiTwo) > -(BEACON_LOCATION_SENSITIVITY) && (rssiOne - rssiTwo) < BEACON_LOCATION_SENSITIVITY)
				{
					if(tempClosestBeacon != null && tempSecondClosestBeacon != null)
					{
						CalculatedLocation = pointsOfInterest.get(tempClosestBeacon.toString() + tempSecondClosestBeacon.toString());
					}
					else {
						d("didnt find two beacons");
					}
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

					//Filter for only expected bluetooth devices
					if(pointsOfInterest.containsKey(bd.getAddress())){
						rssiVal.put(bd.toString(), intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
						beaconsInRange.add(bd);
					}
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
			IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			IntentFilter filter4 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);

			// TODO Don't forget to unregister during onDestroy  (currently never unregisters)
			activityContext.registerReceiver(mReceiver, filter2);
			activityContext.registerReceiver(mReceiver, filter3);
			activityContext.registerReceiver(mReceiver, filter4);

			////////////////////////////////////////////////////////////////////////////////
			//start an inquiry scan loop
			////////////////////////////////////////////////////////////////////////////////
			d("::::::Starting::::::");
			discover d = new discover();
			d.start();
		}
	}	
}
