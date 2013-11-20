package com.example.uipackage;

import java.util.List;

import indoorNavigationMap.MapHandler;
import indoorNavigationMap.PointofInterest;
import indoorNavigationMap.Route;
import indoorNavigationMap.VirtualSpot;

public class FloorMap {
	private MapHandler map = new MapHandler();;
	private PointofInterest poi_1, poi_2, poi_3, poi_4, poi_5, poi_6, poi_7, poi_8;
	private List<VirtualSpot> vsList;
	private Route route;
	
	public FloorMap(){
		
		VirtualSpot vs_1 = new VirtualSpot("1", 0, 0);
		VirtualSpot vs_2 = new VirtualSpot("2", 0, 0);
		VirtualSpot vs_3 = new VirtualSpot("3", 0, 0);
		VirtualSpot vs_4 = new VirtualSpot("4", 0, 0);
		VirtualSpot vs_5 = new VirtualSpot("5", 0, 0);
		VirtualSpot vs_6 = new VirtualSpot("6", 0, 0);
		VirtualSpot vs_7 = new VirtualSpot("7", 0, 0);
		VirtualSpot vs_8 = new VirtualSpot("8", 0, 0);
		
		vsList.add(vs_1);
		vsList.add(vs_2);
		vsList.add(vs_3);
		vsList.add(vs_4);
		vsList.add(vs_5);
		vsList.add(vs_6);
		vsList.add(vs_7);
		vsList.add(vs_8);
		
		route = new Route(vsList);
		
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
		poi_4 = new PointofInterest("911", "room 911", vs_3);
		poi_5 = new PointofInterest("912", "room 912", vs_3);
		poi_6 = new PointofInterest("913", "room 913", vs_3);
		poi_7 = new PointofInterest("914", "room 914", vs_3);
		poi_8 = new PointofInterest("915", "room 915", vs_3);
		
		vs_1.addPointofInterest(poi_1, 9);
		vs_2.addPointofInterest(poi_2, 9);
		vs_3.addPointofInterest(poi_3, 9);
		vs_4.addPointofInterest(poi_4, 9);
		vs_5.addPointofInterest(poi_5, 9);
		vs_6.addPointofInterest(poi_6, 9);
		vs_7.addPointofInterest(poi_7, 9);
		vs_8.addPointofInterest(poi_8, 9);
		
		
		vs_1.setNextVirtualSpotByDirection(vs_2, 12);
		vs_2.setNextVirtualSpotByDirection(vs_3, 12);
		vs_3.setNextVirtualSpotByDirection(vs_4, 12);
		vs_5.setNextVirtualSpotByDirection(vs_6, 12);
		vs_6.setNextVirtualSpotByDirection(vs_7, 12);
		vs_7.setNextVirtualSpotByDirection(vs_8, 12);
	}
	
	public MapHandler getMap(){
		return map;
	}
	
	public Route getRoute(){
		return route;
	}
	
	
}