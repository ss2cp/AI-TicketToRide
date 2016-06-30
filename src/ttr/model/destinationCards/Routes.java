package ttr.model.destinationCards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import ttr.model.player.Player;
import ttr.model.trainCards.TrainCardColor;

/**
 * The valid routes in the game. Each is a tuple of Destinations (see Destination.java)
 * */
public class Routes {
	
	/* Input File where list of routes can be found */
	private final String ROUTES_INPUT_FILE = "resources" + File.separator + "txt" + File.separator + "Routes.txt";
	
	/* Singleton pattern */
	private static Routes instance = null;

	/* List of valid routes */
	private ArrayList<Route> routes = new ArrayList<Route>();
	
	/**
	 * Constructor. Routes are currently hard coded in
	 * */
	public Routes(){
			
		/* Load all the routes one at a time */
		System.out.println("Routes.java: Loading routes from input file...");
		readRoutesFromFile();
		System.out.println("Done!");
	}
	
	public static void initialise(){
		if(instance == null){
			instance = new Routes();
		}
	}
	
	public static void reset(){
		instance = new Routes();
	}
	
	/**
	 * Two methods, return true iff given route is a valid route on the map. These are hardcoded
	 * in the resources/txt/Routes.txt file
	 * */
	public boolean isValidRoute(Route route){
		if(route == null) return false;
		
		for(Route validRoute : this.routes){
			if(route.equals(validRoute)) return true;
		}
		return false;
	}
	public boolean isValidRoute(Destination destination1, Destination destination2, TrainCardColor color){ //Params can be given in ANY order
		return isValidRoute(new Route(destination1, destination2, 1, color)); //cost and color do not matter here
	}	
	
	/**
	 * Returns true iff this route is already claimed. Includes routes with two tracks (i.e., if one is claimed then
	 * both are claimed
	 * */
	public boolean isRouteClaimed(Route route){
		ArrayList<Route> routes = getRoutes(route.getDest1(), route.getDest2());
		
		for(Route nextRoute : routes){
			if(nextRoute.isClaimed()) return true;
		}
		
		return false;
	}
	
	public void claimRoute(Route route, Player playerClaiming){
		ArrayList<Route> routes = getRoutes(route.getDest1(), route.getDest2());
		
		for(Route nextRoute : routes){
			if(!nextRoute.isClaimed()){
				nextRoute.claimThisRoute(playerClaiming);;
			}
		}
	}
	
	/**
	 * Returns ALL of the routes in a list. Don't take advantage of this!
	 * */
	public ArrayList<Route> getAllRoutes(){
		ArrayList<Route> allRoutes = new ArrayList<Route>();
		
		for(Route route : this.routes){
			allRoutes.add(route.clone());
		}
		
		return allRoutes;
	}
	
	/**
	 * Returns the route objects between the given cities if it exists (returns more than one if more than one track
	 * exists, but only one if there are two tracks of the same color)
	 * */
	public ArrayList<Route> getRoutes(Destination destination1, Destination destination2){
		ArrayList<Route> toReturn = new ArrayList<Route>();
		for(Route route : routes){
			if(route.getDest1() == destination1 && route.getDest2() == destination2) toReturn.add(route);
			if(route.getDest2() == destination1 && route.getDest1() == destination2) toReturn.add(route);
				
		}
		return toReturn;
	}
	
	public Player getOwner(Route route){
		ArrayList<Route> routes = getRoutes(route.getDest1(), route.getDest2());
		for(Route realRoute : routes){
			if(realRoute.getOwner() != null) return realRoute.getOwner();
		}
		return null;
	}
	
	public boolean ownsRoute(Player player, Route route){
		return getOwner(route) == player;
	}
	
	/**
	 * Returns the shortest path cost between two cities. Used to determine the value of a destination ticket
	 * */
	public int shortestPathcost(Destination from, Destination to){
		/* If same, just return false */
		if(from == to) return 0;
		
		/* Open and Closed lists (breadth first search) */
		HashMap<Destination, Integer> openList = new HashMap<Destination, Integer>();
		HashMap<Destination, Integer> closedList = new HashMap<Destination, Integer>();
		
		openList.put(from, 0);
		
		while(openList.size() > 0){
			
			/* Pop something off the open list, if destination then return true */
			Destination next = null;
			int minCost = 9999;
			for(Destination key : openList.keySet()){
				if(openList.get(key) < minCost){
					next = key;
					minCost = openList.get(key);
				}
			
			}
			
			/* Take it off the open list and put on the closed list */
			openList.remove(next);
			closedList.put(next, minCost);
			
			/* If this is the destination, then return!!!! */
			if(next == to) return closedList.get(next);
			
			/* Get all the neighbors of the next city that aren't on open or closed lists already */
			for(Destination neighbor : getNeighbors(next)){
				if(closedList.containsKey(neighbor)) continue;
				
				/* get route between next and neighbor and see if better than neighbor's value */
				ArrayList<Route> routesToNeighbor = this.getRoutes(next, neighbor);
				for(Route routeToNeighbor : routesToNeighbor){
					int newCost = closedList.get(next) + routeToNeighbor.getCost();
					
					if(openList.containsKey(neighbor)){	
						if(newCost < openList.get(neighbor)){
							openList.put(neighbor, newCost);
						}
					}
					else{
						openList.put(neighbor, newCost);
					}
				}
			}
		}
		
		return 0;
	}
	
	/**
	 * Returns true iff the given player has a valid connected between the two 
	 * given cities
	 * */
	public boolean hasCompletedRoute(Player player, Destination from, Destination to){
		
		/* If same, just return false */
		if(from == to) return false;
		
		/* Open and Closed lists (breadth first search) */
		ArrayList<Destination> openList = new ArrayList<Destination>();
		ArrayList<Destination> closedList = new ArrayList<Destination>();
		
		openList.add(from);
		
		while(openList.size() > 0){
			
			/* Pop something off the open list, if destination then return true */
			Destination next = openList.remove(openList.size()-1);
			closedList.add(next);
			if(next == to) return true;
			
			/* Get all the neighbors of the next city that aren't on open or closed lists already */
			for(Destination neighbor : getNeighborsByClaimedPlayer(player, next)){
				if(!openList.contains(neighbor) && !closedList.contains(neighbor)){
					openList.add(neighbor);
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Returns all the neighbors of a given city
	 * */
	public ArrayList<Destination> getNeighbors(Destination city){
		ArrayList<Destination> toReturn = new ArrayList<Destination>();
		
		for(Route route : this.routes){
			if(route.getDest1() == city && !toReturn.contains(route.getDest2()))
				toReturn.add(route.getDest2());
			
			if(route.getDest2() == city && !toReturn.contains(route.getDest1()))
				toReturn.add(route.getDest1());
		}
		
		return toReturn;
	}
	
	/**
	 * Returns all neighboring cities iff the given player has claimed the route from
	 * the param city and the city to be returned. Essentially returns all neighbors of a city
	 * to which this player has a direct connection
	 * */
	public ArrayList<Destination> getNeighborsByClaimedPlayer(Player player, Destination city){
		/* First, get all neighbors of this city */
		ArrayList<Destination> neighbors = getNeighbors(city);
		
		/* Make a list of ones to remove */
		ArrayList<Destination> toReturn = new ArrayList<Destination>();
		for(Destination neighbor : neighbors){
			ArrayList<Route> routes = getRoutes(city, neighbor);
			for(Route route : routes){
				if(route != null && (route.getOwner() == player)){
					toReturn.add(neighbor);
					break;
				}
			}
		}
		
		return toReturn;
	}
	
	
	/**
	 * Get the singleton instance
	 * */
	public static Routes getInstance(){ return instance; }
	
	/**
	 * reads in the routes from an input file
	 * */
	private void readRoutesFromFile(){
		/* Precautionary init / clear of the data structure */
		if(routes == null) routes = new ArrayList<Route>();
		routes.clear();
		
		/* Open the file and start reading */
		try(BufferedReader br = new BufferedReader(new FileReader(ROUTES_INPUT_FILE))) {
	        String line = br.readLine();
	        while (line != null && !line.equals("%")) {
	            processLine(line);
	            line = br.readLine();
	        }
	    }
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Processes a single line of the input file (when initializing routes)
	 * */
	private void processLine(String line){
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		
		Destination dest1 = Destination.valueOf(tokenizer.nextToken());
		Destination dest2 = Destination.valueOf(tokenizer.nextToken());
		int cost = Integer.parseInt(tokenizer.nextToken());
		TrainCardColor color = TrainCardColor.valueOf(tokenizer.nextToken());
		
		/* If not a duplicate then add...otherwise ignore */
		if(!isValidRoute(dest1, dest2, color)) this.routes.add(new Route(dest1, dest2, cost, color));
	}
	
	
}
