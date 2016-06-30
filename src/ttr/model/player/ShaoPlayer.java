package ttr.model.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import com.sun.corba.se.impl.orbutil.graph.Node;

import ttr.model.destinationCards.Destination;
import ttr.model.destinationCards.DestinationTicket;
import ttr.model.destinationCards.Route;
import ttr.model.destinationCards.Routes;
import ttr.model.trainCards.TrainCardColor;

public class ShaoPlayer extends Player {

	/**
	 * Need to have this constructor so the player has a name, you can use no
	 * parameters and pass the name of your player to the super constructor, or
	 * just take in the name as a parameter. Both options are shown here.
	 * */
	public ShaoPlayer(String name) {
		super(name);
	}

	public ShaoPlayer() {
		super("Shao Player");
	}

	/**
	 * MUST override the makeMove() method and implement it.
	 * */
	@Override
	public void makeMove() {
		// return a stack of the shortest path from Dest to Dest
		shortestPath(Destination.Toronto, Destination.Charleston);
		// TODO starts coding from here
		// TODO buy ticket routes first
		// TODO write another find shortest path method to deal with claimed
		// routes

		// Store all affordable routes in the ArrayList
		ArrayList<Route> possRoutes = possibleRoutes();

		// sort the list in descending order of cost
		possRoutes = sortDescen(possRoutes);

		/**
		 * test and see if any destination ticket is affordable
		 */
		// iterate through the tickets
		for (DestinationTicket ticket : super.getDestinationTickets()) {
			// TODO check possibility and affordability of each owned ticket
		}

		if (!possRoutes.isEmpty()) {
			// System.out.println("Player has "
			// + super.getNumTrainCardsByColor(possRoutes.get(0)
			// .getColor()) + " " + possRoutes.get(0).getColor()
			// + " cards.\nThe cost of desired path is "
			// + possRoutes.get(0).getCost());
			super.claimRoute(possRoutes.get(0), possRoutes.get(0).getColor());
		}

		/*
		 * Always draw train cards (0 means we are drawing from the pile, not
		 * from the face-up cards)
		 */
		super.drawTrainCard(0);

		/* This call would allow player to draw destination tickets */
		super.drawDestinationTickets();

		/*
		 * Something like this will allow an AI to attempt to buy a route on the
		 * board. The first param is the route they wish to buy, the second
		 * param is the card color they wish to pay for the route with (some
		 * routes have options here)
		 */
		super.claimRoute(new Route(Destination.Atlanta, Destination.Miami, 6,
				TrainCardColor.blue), TrainCardColor.blue);

		/*
		 * NOTE: This is just an example, a player cannot actually do all three
		 * of these things in one turn. The simulator won't allow it
		 */

	}

	/**
	 * Returns all the neighbors of a given city
	 * */
	public ArrayList<Destination> getNeighbors(Destination city) {
		ArrayList<Destination> toReturn = new ArrayList<Destination>();

		for (Route route : Routes.getInstance().getAllRoutes()) {
			if (route.getDest1() == city
					&& !toReturn.contains(route.getDest2()))
				toReturn.add(route.getDest2());

			if (route.getDest2() == city
					&& !toReturn.contains(route.getDest1()))
				toReturn.add(route.getDest1());
		}

		return toReturn;
	}

	/**
	 * Returns a stack of the shortest path between two cities.
	 * */
	public Stack<Route> shortestPath(Destination from, Destination to) {
		Stack<Route> rt = new Stack<Route>();

		/* If same, just return an empty stack */
		if (from == to)
			return rt;

		/* Open and Closed lists (breadth first search) */
		HashMap<Destination, Integer> openList = new HashMap<Destination, Integer>();
		HashMap<Destination, Integer> closedList = new HashMap<Destination, Integer>();
		// initiate a hashmap to store each node's parent, key is the child,
		// object is the parent
		HashMap<Destination, Destination> parent = new HashMap<Destination, Destination>();

		openList.put(from, 0);

		while (openList.size() > 0) {

			/* Pop something off the open list, if destination then return true */
			Destination next = null;
			int minCost = 9999;
			for (Destination key : openList.keySet()) {
				if (openList.get(key) < minCost) {
					next = key;
					minCost = openList.get(key);
				}

			}

			/* Take it off the open list and put on the closed list */
			openList.remove(next);
			closedList.put(next, minCost);

			/* If this is the destination, then return!!!! */
			if (next == to) {
				// System.out.println("test "+closedList);
				break;
			}

			/*
			 * Get all the neighbors of the next city that aren't on open or
			 * closed lists already
			 */
			for (Destination neighbor : getNeighbors(next)) {
				if (closedList.containsKey(neighbor))
					continue;

				/*
				 * get route between next and neighbor and see if better than
				 * neighbor's value
				 */
				ArrayList<Route> routesToNeighbor = this.getRoutes(next,
						neighbor);
				for (Route routeToNeighbor : routesToNeighbor) {
					int newCost = closedList.get(next)
							+ routeToNeighbor.getCost();

					if (openList.containsKey(neighbor)) {
						if (newCost < openList.get(neighbor)) {
							parent.put(neighbor, next);
							openList.put(neighbor, newCost);
						}
					} else {
						openList.put(neighbor, newCost);
						parent.put(neighbor, next);
					}
				}
			}
		}

		// push the last city to destination to the stack
		rt.push(getOneWayRoute(parent.get(to), to));
		// recursively push route to the stack until the starting city
		return printRoute(parent, parent.get(to), rt);
	}

	/*
	 * A recursive method to push routes of the child and parent city to the
	 * stack
	 */
	public Stack<Route> printRoute(HashMap<Destination, Destination> parent,
			Destination child, Stack<Route> rt) {
		if (parent.get(child) == null) {
			return rt;
		}
		rt.push(getOneWayRoute(parent.get(child), child));
		return printRoute(parent, parent.get(child), rt);
	}

	/*
	 * Sort the given ArrayList of Route to descending order of cost
	 */
	public ArrayList<Route> sortDescen(ArrayList<Route> possRoutes) {
		// sort the list in ascending order of cost
		((List<Route>) possRoutes).sort((o1, o2) -> Integer.compare(
				o1.getCost(), o2.getCost()));
		// reverse the list so that the larger cost will be bought first
		Collections.reverse(possRoutes);
		return possRoutes;
	}

	/*
	 * Return an ArrayList of Routes that has not been claimed
	 */
	public ArrayList<Route> possibleRoutes() {
		// get an instance of all routes
		ArrayList<Route> allRoutes = Routes.getInstance().getAllRoutes();
		// the return ArrayList
		ArrayList<Route> ret = new ArrayList<Route>();

		// iterate through all the routes
		for (Route nextRoute : allRoutes) {
			// if some route has not been claimed
			if (!isRouteClaimed(nextRoute)) {
				// save the current route's color and cost
				TrainCardColor color = nextRoute.getColor();
				int cost = nextRoute.getCost();
				// if player has more than the required cost of such color,
				// add to return ArrayList
				if (super.getNumTrainCardsByColor(color) >= cost) {
					// System.out.println("color is "+color+
					// ", cost is "+cost+". Player currently has "+super.getNumTrainCardsByColor(color)+" "+color+" train cards.");
					ret.add(nextRoute);
				}
			}
		}
		return ret;
	}

	/**
	 * Returns true iff this route is already claimed. Includes routes with two
	 * tracks (i.e., if one is claimed then both are claimed)
	 * */
	public boolean isRouteClaimed(Route route) {
		ArrayList<Route> routes = getRoutes(route.getDest1(), route.getDest2());

		for (Route nextRoute : routes) {
			if (nextRoute.getOwner() != null)
				return true;
		}
		return false;
	}

	/**
	 * Returns the route objects between the given cities if it exists (returns
	 * more than one if more than one track exists, but only one if there are
	 * two tracks of the same color)
	 * */
	public ArrayList<Route> getRoutes(Destination destination1,
			Destination destination2) {
		ArrayList<Route> toReturn = new ArrayList<Route>();
		for (Route route : Routes.getInstance().getAllRoutes()) {
			if (route.getDest1() == destination1
					&& route.getDest2() == destination2) {
				toReturn.add(route);
			}
			if (route.getDest2() == destination1
					&& route.getDest1() == destination2) {
				toReturn.add(route);
			}
		}
		return toReturn;
	}

	/**
	 * Returns the route objects between the given cities if it exists (returns
	 * null if not exist)
	 * Return a random one if two routes exist TODO see if this is ok
	 * */
	public Route getOneWayRoute(Destination from, Destination to) {

		for (Route route : Routes.getInstance().getAllRoutes()) {
			if (route.getDest1() == from && route.getDest2() == to) {
				return route;
			}
		}
		return null;
	}

}
