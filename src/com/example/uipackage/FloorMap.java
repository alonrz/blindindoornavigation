package com.example.uipackage;

import java.util.HashMap;
import java.util.List;

import indoorNavigationMap.MapHandler;
import indoorNavigationMap.PointofInterest;
import indoorNavigationMap.Route;
import indoorNavigationMap.VirtualSpot;

public class FloorMap {
	private MapHandler map = new MapHandler();
	private PointofInterest poi_0, poi_1, poi_2, poi_3, poi_4, poi_5, poi_6, poi_7;
	private Route route;
	private HashMap<String, VirtualSpot> mapOfVS = new HashMap<String, VirtualSpot>();
	
	
	public FloorMap(){
		
		VirtualSpot vs_0 = new VirtualSpot("0", 1, 2);
		VirtualSpot vs_1 = new VirtualSpot("1", 3, 4);
		VirtualSpot vs_2 = new VirtualSpot("2", 5, 6);
		VirtualSpot vs_3 = new VirtualSpot("3", 7, 8);
		VirtualSpot vs_4 = new VirtualSpot("4", 9, 10);
		VirtualSpot vs_5 = new VirtualSpot("5", 11, 12);
		VirtualSpot vs_6 = new VirtualSpot("6", 13, 14);
		VirtualSpot vs_7 = new VirtualSpot("7", 15, 16);
		
		map.addVS(vs_0);
		map.addVS(vs_1);
		map.addVS(vs_2);
		map.addVS(vs_3);
		map.addVS(vs_4);
		map.addVS(vs_5);
		map.addVS(vs_6);
		map.addVS(vs_7);
		
		poi_0 = new PointofInterest("935", "", vs_0);
		poi_1 = new PointofInterest("934", "e", vs_1);
		poi_2 = new PointofInterest("906", "e", vs_2);
		poi_3 = new PointofInterest("907", "e", vs_3);
		poi_4 = new PointofInterest("908", "room 911", vs_4);
		poi_5 = new PointofInterest("909", "room 912", vs_5);
		poi_6 = new PointofInterest("911", "room 913", vs_6);
		poi_7 = new PointofInterest("912", "room 914", vs_7);
		
		map.addPoI(poi_0, 9);
		map.addPoI(poi_1, 9);
		map.addPoI(poi_2, 9);
		map.addPoI(poi_3, 9);
		map.addPoI(poi_4, 9);
		map.addPoI(poi_5, 9);
		map.addPoI(poi_6, 9);
		map.addPoI(poi_7, 9);
		
		
		vs_0.setNextVirtualSpotByDirection(vs_1, 12, 5.0);
		vs_1.setNextVirtualSpotByDirection(vs_0, 6, 5.0);
		vs_1.setNextVirtualSpotByDirection(vs_2, 3, 7.0);
		vs_2.setNextVirtualSpotByDirection(vs_1, 9, 7.0);
		vs_2.setNextVirtualSpotByDirection(vs_3, 12, 7.0);
		vs_3.setNextVirtualSpotByDirection(vs_2, 6, 7.0);
		vs_3.setNextVirtualSpotByDirection(vs_4, 12, 8.0);
		vs_4.setNextVirtualSpotByDirection(vs_3, 6, 8.0);
		vs_5.setNextVirtualSpotByDirection(vs_6, 12, 9.0);
		vs_6.setNextVirtualSpotByDirection(vs_5, 6, 9.0);
		vs_6.setNextVirtualSpotByDirection(vs_7, 12, 10.0);
		vs_7.setNextVirtualSpotByDirection(vs_6, 6, 10.0);
		
		
		mapOfVS.put("0", vs_0);
		mapOfVS.put("1", vs_1);
		mapOfVS.put("2", vs_2);
		mapOfVS.put("3", vs_3);
		mapOfVS.put("4", vs_4);
		mapOfVS.put("5", vs_5);
		mapOfVS.put("6", vs_6);
		mapOfVS.put("7", vs_7);
		
		
	}
	
	public MapHandler getMap(){
		return map;
	}
	
	public HashMap<String, VirtualSpot> getHashmapOfVirtualSpots(){
		return mapOfVS;
	}
	
	public Route getRoute(){
		return route;
	}
	
	
}