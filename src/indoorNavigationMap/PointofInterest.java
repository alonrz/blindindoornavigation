package indoorNavigationMap;

/*
 * Simple class containing only a name, description, and the Virtual Spot it is associated with.
 * Possibly this could be expanded to accommodate different search types or patterns; maybe a getSearchTerms
 * method or something.
 */

public class PointofInterest {
	
	private String name, description;
	private VirtualSpot vspot;
	
	public PointofInterest(String name, String description, VirtualSpot vs)
	{
		this.name = name;
		this.description = description;
		vspot = vs;
	}
	
	public String toString()
	{
		return name + ": " + description;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDesc()
	{
		return description;
	}
	
	public VirtualSpot getVirtualSpot()
	{
		return vspot;
	}
}
