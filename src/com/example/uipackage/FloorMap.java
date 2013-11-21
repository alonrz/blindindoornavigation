package com.example.uipackage;

import java.util.HashMap;
import java.util.List;

import indoorNavigationMap.MapHandler;
import indoorNavigationMap.PointofInterest;
import indoorNavigationMap.Route;
import indoorNavigationMap.VirtualSpot;

public class FloorMap {
	private MapHandler map = new MapHandler();
	private PointofInterest poi_1, poi_2, poi_3, poi_4, poi_5, poi_6, poi_7, poi_8;
	private Route route;
	private HashMap<String, VirtualSpot> mapOfVirtualSpots = new HashMap<String, VirtualSpot>();
	
	
	public FloorMap(){
		
		VirtualSpot vs_1 = new VirtualSpot("1", 1, 2);
		VirtualSpot vs_2 = new VirtualSpot("2", 3, 4);
		VirtualSpot vs_3 = new VirtualSpot("3", 5, 6);
		VirtualSpot vs_4 = new VirtualSpot("4", 7, 8);
		VirtualSpot vs_5 = new VirtualSpot("5", 9, 10);
		VirtualSpot vs_6 = new VirtualSpot("6", 11, 12);
		VirtualSpot vs_7 = new VirtualSpot("7", 13, 14);
		VirtualSpot vs_8 = new VirtualSpot("8", 15, 16);
		
		map.addVS(vs_1);
		map.addVS(vs_2);
		map.addVS(vs_3);
		map.addVS(vs_4);
		map.addVS(vs_5);
		map.addVS(vs_6);
		map.addVS(vs_7);
		map.addVS(vs_8);
		
		poi_1 = new PointofInterest("909", "Puder's office", vs_1);
		poi_2 = new PointofInterest("910", "Yoon's office", vs_2);
		poi_3 = new PointofInterest("906", "room 906", vs_3);
		poi_4 = new PointofInterest("911", "room 911", vs_4);
		poi_5 = new PointofInterest("912", "room 912", vs_5);
		poi_6 = new PointofInterest("913", "room 913", vs_6);
		poi_7 = new PointofInterest("914", "room 914", vs_7);
		poi_8 = new PointofInterest("915", "room 915", vs_8);
		
		
		map.addPoI(poi_1, 9);
		map.addPoI(poi_2, 9);
		map.addPoI(poi_3, 9);
		map.addPoI(poi_4, 9);
		map.addPoI(poi_5, 9);
		map.addPoI(poi_6, 9);
		map.addPoI(poi_7, 9);
		map.addPoI(poi_8, 9);
		
		
		vs_1.setNextVirtualSpotByDirection(vs_2, 12);
		vs_2.setNextVirtualSpotByDirection(vs_1, 6);
		vs_2.setNextVirtualSpotByDirection(vs_3, 12);
		vs_3.setNextVirtualSpotByDirection(vs_2, 6);
		vs_3.setNextVirtualSpotByDirection(vs_4, 12);
		vs_4.setNextVirtualSpotByDirection(vs_3, 6);
		vs_5.setNextVirtualSpotByDirection(vs_6, 12);
		vs_6.setNextVirtualSpotByDirection(vs_5, 6);
		vs_6.setNextVirtualSpotByDirection(vs_7, 12);
		vs_7.setNextVirtualSpotByDirection(vs_6, 6);
		vs_7.setNextVirtualSpotByDirection(vs_8, 12);
		vs_8.setNextVirtualSpotByDirection(vs_7, 6);
		
		mapOfVirtualSpots.put("1", vs_1);
		mapOfVirtualSpots.put("2", vs_2);
		mapOfVirtualSpots.put("3", vs_3);
		mapOfVirtualSpots.put("4", vs_4);
		mapOfVirtualSpots.put("5", vs_5);
		mapOfVirtualSpots.put("6", vs_6);
		mapOfVirtualSpots.put("7", vs_7);
		mapOfVirtualSpots.put("8", vs_8);
		
		
	}
	
	public MapHandler getMap(){
		return map;
	}
	
	public HashMap<String, VirtualSpot> getHashMapOfVirtualSpots(){
		return mapOfVirtualSpots;
	}
	
	public Route getRoute(){
		return route;
	}
	
	
}