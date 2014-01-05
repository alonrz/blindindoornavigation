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

	/**Singleton Class */
	private static DiscoverLocation mDiscLoc = null;
	private DiscoverLocation() {
		
	}

	/**
	 * Getter for Singleton class DiscoverLocation
	 * 
	 * @return a singleton instance of DiscoverLocation
	 */
	public static DiscoverLocation getStepsManager() {
		if (mDiscLoc == null)
			mDiscLoc = new DiscoverLocation();
		
		return mDiscLoc;
	}
	
	
	Hashtable<String, Short> rssiVal = new Hashtable<String, Short>();
	Hashtable<Integer, String> colOrder= new Hashtable<Integer, String>(); //used for pilot test to keep certain beacon data in column order
	Hashtable<String, String> pointsOfInterest = new Hashtable<String, String>(); //This will most likely be passed to DiscoverLocation, but currently its just hard coded
	Set<BluetoothDevice> beaconsInRange = new HashSet<BluetoothDevice>();
	
	boolean isScanning = false;
	boolean firstTimeBoolean = true;
	
	Context activityContext;
	LocationListener mListener = null;
	ScanForDevices ct = new ScanForDevices();
	BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	String CalculatedLocation = "-1";
	
	
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

	/** default constructor currently is hard-coded for test beacons
	 * 
	 * @return
	 */
	public int InitBluetoothLocationServices()
	{		
		String[] hardcodedBeacons = new String[5];
		
		hardcodedBeacons[0] = "00:02:72:3F:40:5F";
		hardcodedBeacons[1] = "00:02:72:3F:4E:8B";
		hardcodedBeacons[2] = "00:02:72:C6:A3:A4";
		hardcodedBeacons[3] = "00:02:72:C6:A3:89";
		hardcodedBeacons[4] = "00:02:72:C6:A3:C6";
		
		
		//DEBUG, REMOVE BEFORE RUNNING TESTS
		hardcodedBeacons[0] = "90:00:4E:FE:34:E1"; // D laptop
		hardcodedBeacons[1] = "78:A3:E4:A8:7E:48"; // D iphone
		
		InitBluetoothLocationServices(hardcodedBeacons);
		
		return 0;
	}

	public DiscoverLocation(Context c){
		activityContext = c;

		//NOTE: for prototyping we are currently just using default constructor and hard-coded beacons. 
		//using the InitBluetoothLocationServices(String[] ListOfBluetoothBeacons) function is more ideal.
		InitBluetoothLocationServices();
	}

	//Debug log function
	public static void d(String s){
		Log.d("debug", s);
	}

	/** This function is the entry point to the blue-tooth beacon scanning loop. 
	 *  Once called beacons will be scanned for periodically
	 * 
	 */
	public void scanForDevices(){
		
		//Ensure only one scan loop is ever started.
		if(isScanning == false){
			isScanning = true;
			
			beaconsInRange.clear();
			rssiVal.clear();
			adapter.cancelDiscovery();

			ct.start();//Starts the location finding algorithm
		}
		
	}
	
	/** stops the discovery cycle.
	 * 
	 */
	public void stopDiscovery(){
		ct.stopDiscovery();
	}
	
	/** unregisters the Location Listener.
	 * 
	 */
	public void unregisterLocationListener(){
		ct.unregisterReciever();
	}

	/** registers a Location Listener callback
	 * 
	 * @param listener
	 */
	public void registerLocationListener(LocationListener listener)
	{
		mListener = listener;
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////Calculate Location Thread/////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	/** sets up and begins the scan cycle for bluetooth beacons in range.
	 * 
	 */
	private class ScanForDevices extends Thread   {

		boolean isRegistered = false;
		discover discoveryLoop = new discover();

		public ScanForDevices() {

		}

		/** A broadcast receiver for bluetooth beacon callbacks.
		 * 
		 */
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

			activityContext.registerReceiver(mReceiver, filter2);
			activityContext.registerReceiver(mReceiver, filter3);
			activityContext.registerReceiver(mReceiver, filter4);
			isRegistered = true;
			
			////////////////////////////////////////////////////////////////////////////////
			//start an inquiry scan loop
			////////////////////////////////////////////////////////////////////////////////
			d("::::::Starting::::::");
			discoveryLoop.start();
			

		}
		
		/** stops the discovery cycle
		 * 
		 */
		public void stopDiscovery(){
			discoveryLoop.stopDiscovery();
		}
		
		/** unregisters the receiver
		 * 
		 */
		public void unregisterReciever(){
			if(isRegistered == true)
				activityContext.unregisterReceiver(mReceiver);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////Discover Location Thread (used as loop)///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	private class discover extends Thread {
		boolean keepGoing = true;
		
		public discover() {

		}
		
		public void stopDiscovery(){
			keepGoing = false;
		}
		
		@SuppressLint("NewApi")
		public void run() {

			//Clear data storage from discovered devices from previous loop
			beaconsInRange.clear();
			rssiVal.clear();

			//Sleep for 5 seconds to allow bluetooth device discovery.
			//Note, this 5sec is arbitrary and may need to be modified depending on the UI algorithm.
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

			//Restart the discovery loop if applicable
			if(keepGoing == true){
				discover d = new discover();
				d.start();
			}
		}

		/** callback function to pass location calculated back to listeners
		 * 
		 */
		void newLocationHasBeenCalculated()
		{
			if(mListener != null)
				mListener.newLocationHasBeenCalculated(CalculatedLocation);
			else
				d("Error! mlisteners is null!");
		}

		/** locate the closest two beacons and determine physical location based off their RSSI values.
		 * if only one beacon is present, the system automatically assumes it is the closest beacon. 
		 * 
		 */
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
					////////////////////////////////////////////////////////////////////////////////////
					//equally close to both beacons, so pull the correct data for the midpoint
					////////////////////////////////////////////////////////////////////////////////////
					if(tempClosestBeacon != null && tempSecondClosestBeacon != null)
					{
						CalculatedLocation = pointsOfInterest.get(tempClosestBeacon.toString() + tempSecondClosestBeacon.toString());
					}
					else {
						d("didnt find two beacons");
					}
				}
			}
			else if(tempClosestBeacon != null)
			{
				////////////////////////////////////////////////////////////////////////////////////
				//If only one beacon is seen, it is by default the closest.
				////////////////////////////////////////////////////////////////////////////////////
				CalculatedLocation = pointsOfInterest.get(tempClosestBeacon.toString());
			}
			
		}
	}	
}
