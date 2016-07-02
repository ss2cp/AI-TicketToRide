package ttr.model.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import sun.security.krb5.internal.Ticket;
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

		// Store all affordable routes in the ArrayList
		ArrayList<Route> possRoutes = possibleRoutes();

		// sort the list in descending order of cost
		possRoutes = sortDescen(possRoutes);

		/**
		 * test and see if any destination ticket is affordable
		 */
		// iterate through the tickets
		for (int i = 0; i < super.getDestinationTickets().size(); i++) {
			Route nextToBuy = nextTicketRouteToBuy(super
					.getDestinationTickets().get(i));

			if (checkClaimable(nextToBuy.getDest1(), nextToBuy.getDest2())) {
				if (super.getNumTrainCardsByColor(nextToBuy.getColor()) >= nextToBuy
						.getCost()) {
					System.out.println("Shao is going to buy "
							+ nextToBuy
							+ "\nHe has "
							+ super.getNumTrainCardsByColor(nextToBuy
									.getColor())
							+ " cards, and purchase requires "
							+ nextToBuy.getCost());
					super.claimRoute(nextToBuy, nextToBuy.getColor());

				} else {
					System.out.println("But Not Enough Money");
				}
			} else if (!possRoutes.isEmpty()) {
				// System.out.println("Player has "
				// + super.getNumTrainCardsByColor(possRoutes.get(0)
				// .getColor()) + " " + possRoutes.get(0).getColor()
				// + " cards.\nThe cost of desired path is "
				// + possRoutes.get(0).getCost());
				System.out.println("Shao is going to buy "
						+ nextToBuy
						+ "\nHe has "
						+ super.getNumTrainCardsByColor(possRoutes.get(0)
								.getColor()) + " cards, and purchase requires "
						+ possRoutes.get(0).getCost());
				super.claimRoute(possRoutes.get(0), possRoutes.get(0)
						.getColor());
			}
		}
		super.drawTrainCard(0);

		/*
		 * Always draw train cards (0 means we are drawing from the pile, not
		 * from the face-up cards)
		 */

		/* This call would allow player to draw destination tickets */
		super.drawDestinationTickets();

	}

	/*
	 * Returns a boolean whether you can afford the parameter ticket
	 */
	public Route nextTicketRouteToBuy(DestinationTicket ticket) {
		Stack<Route> route = shortestPath(ticket.getFrom(), ticket.getTo());
		// System.out.println("shortest path from " + ticket.getFrom() + " to "
		// + ticket.getTo() + " is\n" + route);
		Route temp = route.pop();
		// return a stack of the shortest path from Dest to Dest

		// for each route, if i have claimed it, then move to the next route
		for (int i = 0; i < route.size(); i++) {

			if (checkIfIClaimed(route.get(i))) {
				System.out.println(route.get(i) + " is claimed by me already");
				continue;
			}
			// else return the max cost of the route and the color i can afford
			else {
				if (route.get(i).getCost() >= temp.getCost()
						&& super.getNumTrainCardsByColor(route.get(i)
								.getColor()) > route.get(i).getCost()) {
					System.out.println(route.get(i) + " has greater cost then "
							+ temp + ", replacing temp with ~");
					System.out.println("You have "
							+ super.getNumTrainCardsByColor(route.get(i)
									.getColor()) + " cards of "
							+ route.get(i).getColor());
					temp = route.get(i);
					// route.push(temp);
				}
			}
		}
		// System.out.println("The longest route is " + temp);
		return temp;
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
	 * Returns a stack of the shortest path between two cities, including those
	 * that I have claimed.
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
			 * closed lists already OR no route is claimable between next and
			 * neighbor
			 */
			for (Destination neighbor : getNeighbors(next)) {
				// boolean claimable = checkClaimable(next, neighbor);
				if (closedList.containsKey(neighbor)
				// || !claimable
				)
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
					// a boolean to see if such route is claimed by someone
					boolean byMe = true;
					// if claimed, then see if it is claimed by me
					// also return true if no one claimed
					if (routeToNeighbor.getOwner() != null)
						byMe = routeToNeighbor.getOwner().getName()
								.equals(this.getName());

					if (openList.containsKey(neighbor)) {
						if (newCost < openList.get(neighbor) && byMe) {
							parent.put(neighbor, next);
							openList.put(neighbor, newCost);
						}
					} else if (!openList.containsKey(neighbor) && byMe) {
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
					// System.out.println("color is " + color + ", cost is "
					// + cost + ". Player currently has "
					// + super.getNumTrainCardsByColor(color) + " "
					// + color + " train cards.");
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
	 * Returns the route objects between the given cities if that this player
	 * has claimed, IF NOT, return those that no one has claimed (returns null
	 * if not exist) Return a random one if two routes exist
	 * */
	public Route getOneWayRoute(Destination from, Destination to) {

		for (Route route : Routes.getInstance().getAllRoutes()) {
			if ((route.getDest1() == from && route.getDest2() == to)
					|| (route.getDest1() == to && route.getDest2() == from)) {
				return route;
			}

		}
		for (Route route : Routes.getInstance().getAllRoutes()) {
			if (route.getDest1() == from && route.getDest2() == to) {
				System.out.println("TEST3");
				if (route.getOwner() == null) {
					System.out.println("TEST4");
					return route;
				}
			}
		}
		System.out.println("RETURNING NULL");
		return null;
	}

	/*
	 * return false if all routes has been claimed by opponent or this Player
	 * has claimed one
	 */
	public boolean checkClaimable(Destination from, Destination to) {
		System.out.println("Checking claimable... ");

		for (Route route : Routes.getInstance().getAllRoutes()) {
			if (route.getDest1() == from && route.getDest2() == to) {
				System.out.println("Found a route between " + from + " and "
						+ to + " , checking for owner...");
				if (route.getOwner() == null) {
					System.out.println("Such route has no owner");
					return true;
				} else if (route.getOwner().getName().equals(this.getName())) {
					System.out.println("Such route's owner is "
							+ this.getName());
					return false;
				}
			}
		}
		System.out
				.println("No condition in checkClaimable met, returning NO claimable");
		return false;
	}

	public boolean checkIfIClaimed(Route route) {
		if (route.getOwner() == null)
			return false;
		else {
			return route.getOwner().getName().equals(this.getName());
		}
	}
}
