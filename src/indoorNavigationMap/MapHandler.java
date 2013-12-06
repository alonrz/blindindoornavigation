package indoorNavigationMap;

import indoorNavigationMap.VirtualSpot.AdjacentVS;

import java.io.*;
import java.util.*;

import android.util.*;
/*
 * To create a map first all the vSpots should be created and added to the hash.
 * Next, all the pois should be created, added to the hash and to the relevant VirtualSpot. (addPoI
 * takes care of both these).
 * Then, for each VirtualSpot, a SparseArray<String> using the clock direction as keys should
 * be created with the names of adjacent VirtualSpots. These are passed to addVStoMap to add to the map.
 * 
 * A hashmap of all PoIs can obtained with getPointsofInterest; this could probably be used to search for
 * specific Points of Interest
 */

public class MapHandler {

        private Map<String,PointofInterest> poIs;
        private Map<String,VirtualSpot> vSpots;
        
        public MapHandler()
        {
                poIs = new HashMap<String, PointofInterest>();
                vSpots = new HashMap<String,VirtualSpot>();
        }
        
        //add a poi to the hash
        public void addPoI(PointofInterest poi, int direction)
        {
                poIs.put(poi.getName(), poi);
                poi.getVirtualSpot().addPointofInterest(poi, direction);
        }
        
        //add vs to hash
        public void addVS(VirtualSpot vs)
        {
                if(!vSpots.containsKey(vs.getName()))
                        vSpots.put(vs.getName(), vs);
        }
        
        //return a hashmap of all pois
        public Map<String, PointofInterest> getPointsofInterest()
        {
                return poIs;
        }
        
        //return a hashmap of all vs's
        public Map<String,VirtualSpot> getVirtualSpots()
        {
                return vSpots;
        }
        
        //add a vs to the map, adjSpots is a SparseArray with key = direction, value = VirtualSpot in that
        //direction
        public void addVStoMap(VirtualSpot vs, SparseArray<VirtualSpot> adjSpots)
        {
                for(int i = 0; i < adjSpots.size(); i++)
                {
                        vs.setNextVirtualSpotByDirection(adjSpots.valueAt(i), adjSpots.keyAt(i));
                }
                
                addVS(vs);
        }
        
        //not finished!
        //Should follow the order listed at the top:
        //get virtual spot names and locations, create and add them to the hashmap
        //get poi names, descriptions, and associated vs, create and add them to the hashmap
        //get virtual spot names and adjacent virtual spot names and directions, populate a SparseArray
        //using direction as key and name as value:
        //SparseArray<VirtualSpot> sa = new SparseArray<VirtualSpot>();
        //... (get data from database)
        //sa.put(direction, adjname);   //repeat for each adjacent virtual spot
        //addVStoMap(vs, sa)            //vs is the virtual spot from the hashmap using name as key
        public void loadMapFromDB()
        {
                
        }

/*        public List<VirtualSpot> getRoute(String start, String dest)
        {
                List<VirtualSpot> route = new ArrayList<VirtualSpot>();
                int i;
                route.add(vSpots.get(start));
                
                VirtualSpot currentSpot = vSpots.get(start);
                while(true)
                {
                        SparseArray<AdjacentVS> nextto = currentSpot.getAdjacentVirtualSpots();
                        
                        int size = nextto.size();
                        
                        for(i = 0; i < size; i++)
                        {
                                if(nextto.get(i).getVS().getName() == dest)
                                {
                                        route.add(vSpots.get(dest));
                                        break;
                                }
                                
                                
                        }
                        if(nextto.get(i).getVS().getName() == dest)
                        {
                                break;
                        }
                }
        }*/
        
        private class ToDestination implements Comparator<VirtualSpot>
        {
                private VirtualSpot goal;
                
                public ToDestination(VirtualSpot goal)
                {
                        this.goal = goal;
                }
                
                public void setGoal(VirtualSpot goal)
                {
                        this.goal = goal;
                }

                @Override
                public int compare(VirtualSpot vs1, VirtualSpot vs2)
                {
                        double dist1 = vs1.distFrom(goal);
                        double dist2 = vs2.distFrom(goal);
                                        
                        if(Math.abs(dist1 - dist2) < 0.000001)
                                return 0;
                        if(dist1 < dist2)
                                return -1;
                        else
                                return 1;
                }
                
        }
        
        public Route getRouteVStoPoI(String start, String dest)
        {
                String destvs = poIs.get(dest).getVirtualSpot().getName();
                return getRouteVStoVS(start, destvs);
        }
        
        //A*
        public Route getRoute(String start, String dest)
        {
                String startvs = poIs.get(start).getVirtualSpot().getName();
                String destvs = poIs.get(dest).getVirtualSpot().getName();
                return getRouteVStoVS(startvs, destvs);
        }
        
        public Route getRouteVStoVS(String start, String dest)
        {
                PriorityQueue<VirtualSpot> tovisit;
                
//                VirtualSpot vstart = poIs.get(start).getVirtualSpot();
//                VirtualSpot vdest = poIs.get(dest).getVirtualSpot();
                
                VirtualSpot vstart = vSpots.get(start);
                VirtualSpot vdest = vSpots.get(dest);
                
                ToDestination todest = new ToDestination(vdest);
                tovisit = new PriorityQueue<VirtualSpot>(20, todest);
                
                tovisit.add(vstart);
                
                List<VirtualSpot> visited = new ArrayList<VirtualSpot>();
                Map<VirtualSpot, VirtualSpot> camefrom= new HashMap<VirtualSpot, VirtualSpot>();
                
                Map<VirtualSpot, Double> gscore = new HashMap<VirtualSpot, Double>();
                Map<VirtualSpot, Double> fscore = new HashMap<VirtualSpot, Double>();
                
                gscore.put(vstart, Double.valueOf(0.0));
                fscore.put(vstart, Double.valueOf(gscore.get(vstart)+vstart.distFrom(vdest)));
                
                VirtualSpot currentVspot;
                
                while(!tovisit.isEmpty())
                {
                        currentVspot = tovisit.poll();
                        
                        if(currentVspot.equals(vdest))
                                return new Route(createRoute(camefrom, vdest));
                        
                        visited.add(currentVspot);
                        
                        SparseArray<AdjacentVS> neighbors = currentVspot.getAdjacentVirtualSpots();
                        
                        for(int i = 0; i < neighbors.size(); i++)
                        {
                                VirtualSpot neighbor = neighbors.valueAt(i).getVS();
                                double tempgscore = gscore.get(currentVspot).doubleValue() + currentVspot.distFrom(neighbor);
                                double tempfscore = tempgscore + neighbor.distFrom(vdest);
                                
                                if(visited.contains(neighbor) && tempfscore >= fscore.get(neighbor))
                                        continue;
                                
                                if(!tovisit.contains(neighbor) || tempfscore < fscore.get(neighbor))
                                {
                                        camefrom.put(neighbor, currentVspot);
                                        gscore.put(neighbor, Double.valueOf(tempgscore));
                                        fscore.put(neighbor, Double.valueOf(tempfscore));
                                        
                                        if(!tovisit.contains(neighbor))
                                                tovisit.add(neighbor);
                                }
                        }
                }
                
                return null;
        }
        
        private List<VirtualSpot> createRoute(Map<VirtualSpot, VirtualSpot> camefrom, VirtualSpot vcur)
        {
                List<VirtualSpot> route = new ArrayList<VirtualSpot>();
                
                if(camefrom.containsKey(vcur))
                {
                        route.addAll(createRoute(camefrom, camefrom.get(vcur)));
                        route.add(vcur);
                }
                else
                {
                        route.add(vcur);
                }
                return route;
        }
        

        public void loadMapFromFile(InputStream iStream) throws IOException
        {
                BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
                String line;
                
                while(!(line = reader.readLine()).contentEquals("endofvs"))
                {
                        double x, y;
                        
                        x = Double.parseDouble(reader.readLine());
                        y = Double.parseDouble(reader.readLine());
                                
                        addVS(new VirtualSpot(line, x, y));
                }
                
                while((line = reader.readLine()) != null)
                {
                        SparseArray<VirtualSpot> adTemp = new SparseArray<VirtualSpot>();
                        
                        String name = line;
                        
                        while(!(line = reader.readLine()).contentEquals("div"))
                        {
                                int dir = Integer.parseInt(reader.readLine());
                        
                                adTemp.put(dir, getVirtualSpots().get(line));
                        }
                        
                        addVStoMap(getVirtualSpots().get(name), adTemp);
                        adTemp.clear();
                }        
                        
                /*                String name, description, vsname;
                                char dir;
                                VirtualSpot vs;
                                PointofInterest poi;
                                        
                                name = temp;
                                description = file.readLine();
                                vsname = file.readLine();
                                temp = file.readLine();
                                dir = temp.charAt(0);
                                        
                                vs = vSpots.get(vsname);
                                poi = new PointofInterest(name, description,vs);
                                vs.addPointofInterest(poi, dir);
                                        
                                addPoI(poi);*/
                                        
        }
}