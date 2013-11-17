package indoorNavigationMap;

import java.util.*;

public class Route
{
	private List<VirtualSpot> route;
	private VirtualSpot currentLoc;
	private int currentIndex = 0;
	
	public Route(List<VirtualSpot> route)
	{
		this.route = route;
		currentLoc = route.get(0);
	}
	
	private double nextTurn(int start)
	{
		int current = start + 1;
		int direction = route.get(start).directionTo(route.get(current));
		
		while(current != route.size()-1 && route.get(current).directionTo(route.get(current+1)) == direction)
		{
			current++;
		}
		
		currentLoc = route.get(current);
		currentIndex = current;
		
		return route.get(start).distFrom(route.get(current));
	}
	
	public String getDirections()
	{
		int direction = route.get(0).directionTo(route.get(1));
		String directions = "Turn to " + direction + " o' clock.\n";
		
		while(currentIndex != route.size()-1)
		{
			directions = directions + "Go " + nextTurn(currentIndex) + " steps.\n";
			if(currentIndex != route.size()-1)
			{
				int currentDirection = route.get(currentIndex-1).directionTo(route.get(currentIndex));
				int nextDirection = route.get(currentIndex).directionTo(route.get(currentIndex + 1));
				
				int turnto = (12 - (currentDirection - nextDirection))%12;
				
				directions = directions + "Turn to " + turnto + " o' clock.\n";
			}
		}
		
		return directions;
		
		/*
		String directions = "";
		
		int currentDirection = 12;
		double currentDistance = 0.0;
		
		for(int i = 1; i < route.size() - 1; i++)
		{
			if(route.get(i-1).directionTo(route.get(i)) != currentDirection)
			{
				currentDirection = route.get(i).directionTo(route.get(i+1));
				directions = directions + "Turn to " + currentDirection + " o' clock.\n";
			}

			{
				
			}
				currentDistance += route.get(i).getNextVirtualSpotByDirection(currentDirection).getDist();
			
	//		else
			{
				currentDirection = route.get(i+1).directionTo(route.get(i+2));
				directions = directions + "Go " + currentDistance + " steps, then turn to ";
			}
		}
		
		directions = directions + "Go " + currentDistance + " steps.";
		
		return directions;*/
	}

	public List<VirtualSpot> getVSList()
	{
		return route;
	}
	
	public void setCurrentLoc(VirtualSpot currentLoc)
	{
		this.currentLoc = currentLoc;
	}
}