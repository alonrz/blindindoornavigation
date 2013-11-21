package indoorNavigationMap;

import java.util.*;

import android.util.*;

/*
 * Virtual Spots are nodes in an undirected graph. Each Virtual Spot contains information about
 * adjacent VSs in the 12 clock directions; each direction can only have a single VS. A VS also
 * has lists of Points of Interest in each direction.
 */

public class VirtualSpot {
	
	private double x, y;
	private String name;
	private SparseArray<List<PointofInterest>> pois;
	private SparseArray<AdjacentVS> vSpots;
	
	//create virtual spot at the current location, currently unknown how that is obtained
	public VirtualSpot(String name)
	{
		this.x = BlackBoxForCoords.getCurrentX();
		this.y = BlackBoxForCoords.getCurrentY();
		this.name = name;

		pois = new SparseArray<List<PointofInterest>>();
		
		vSpots = new SparseArray<AdjacentVS>();
	}
	
	//create a virtual spot at the specified location
	public VirtualSpot(String name, double x, double y)
	{
		this.x = x;
		this.y = y;
		this.name = name;
		
		pois = new SparseArray<List<PointofInterest>>();
		
		vSpots = new SparseArray<AdjacentVS>();
	}
	
	//add a point of interest in a specific direction to this virtual spot
	//if there is no list of pois in that direction, the list will be created
	public void addPointofInterest(PointofInterest poi, int direction)
	{		
		if(pois.get(direction) != null)
		{
			pois.get(direction).add(poi);
		}
		else
		{
			List<PointofInterest> aList = new ArrayList<PointofInterest>();
			aList.add(poi);
			pois.put(direction, aList);
		}
	}
	
	//returns the list of pois in any direction
	//returns null if there are no pois listed in that direction
	public List<PointofInterest> getPoIsByDirection(int direction)
	{
			return pois.get(direction);
	}
	
	//connects this virtual spot to another
	//currently only creates a unidirectional edge, this must be called for the other node as well
	public void setNextVirtualSpotByDirection(VirtualSpot vs, int dir)
	{
		double dist = distFrom(vs);
		int oppositeDir = (dir + 6)%12;
		
		if(oppositeDir == 0)
		{
			oppositeDir = 12;
		}
		
		if(vSpots.get(dir) == null || !vSpots.get(dir).vs.equals(vs))
		{
			vSpots.put(dir, new AdjacentVS(vs, dist));
		}
		else
		{
			AdjacentVS adjVS = vSpots.get(dir);
			
			if(dist < adjVS.dist)
			{
				vSpots.put(dir, new AdjacentVS(vs, dist));	
			
				vs.setNextVirtualSpotByDirection(this, oppositeDir);
			
				adjVS.vs.setNextVirtualSpotByDirection(vs, oppositeDir);
			}
			else
			{
				adjVS.vs.setNextVirtualSpotByDirection(vs, dir);
			}
		}
	}
	
	//returns the virtual spot in that direction or null if there is none
	public AdjacentVS getNextVirtualSpotByDirection(int direction)
	{
		return vSpots.get(direction);
	}
	
	public SparseArray<AdjacentVS> getAdjacentVirtualSpots()
	{
		return vSpots;
	}
	/*
	 * return poi=======================================================================================
	 * I need to have method get POI list, from Thinh
	 * the method below is just temporary 
	*/
	public String getFirstPOI()
	{
		return pois.valueAt(0).toString();
	}
	//==================================================================================================
	
	
	public double distFrom(VirtualSpot vs)
	{
		return Math.sqrt((Math.pow(x - vs.getX(), 2) + Math.pow(y - vs.getY(), 2)));
	}
	
	public int directionTo(VirtualSpot vs)
	{
		for(int i = 0; i < vSpots.size(); i++)
		{
			if(vs == vSpots.valueAt(i).getVS())
			{
				return vSpots.keyAt(i);
			}
		}
		
		return -1;
	}
	
	public String toString()
	{
		String temp = "name: " + name + ", coordinates: " + x + ", " + y + "\nNext to:\n";
		for(int i = 0; i < vSpots.size(); i++)
		{
			temp = temp + vSpots.valueAt(i).vs.name + " at " + vSpots.keyAt(i) + ", distance: "
					+ vSpots.valueAt(i).getDist() + "\n";
		}
		
		temp = temp + "Points of Interest:\n";
		
		for(int i = 0; i < pois.size(); i++)
		{
			temp = temp + pois.valueAt(i).toString() + " at " + pois.keyAt(i) + "\n";
		}
		return temp;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public String getName()
	{
		return name;
	}
	
	//a nested class to keep track of the distance to the next virtual spot
	public class AdjacentVS
	{
		double dist;
		VirtualSpot vs;
		
		public AdjacentVS(VirtualSpot vs, double dist)
		{
			this.vs = vs;
			this.dist = dist;
		}
		
		public VirtualSpot getVS()
		{
			return vs;
		}
		
		public  double getDist()
		{
			return dist;
		}
	}

}
