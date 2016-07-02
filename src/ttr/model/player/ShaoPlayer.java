package ttr.model.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import ttr.model.destinationCards.Destination;
import ttr.model.destinationCards.DestinationTicket;
import ttr.model.destinationCards.Route;
import ttr.model.destinationCards.Routes;
import ttr.model.trainCards.TrainCard;
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
		// store an instance of the Destination tickets arrayList
		ArrayList<DestinationTicket> tickets = super.getDestinationTickets();
		// Store all affordable routes in the ArrayList
		ArrayList<Route> possRoutes = possibleRoutes();
		// sort the list in descending order of cost
		possRoutes = sortDescen(possRoutes);

		// if no more dest ticket, pick from possRoutes
		if (super.getTotalDestTicketCost() == 0) {
			if (!possRoutes.isEmpty()) {
				System.out.println("poss: " + possRoutes);
				if (possRoutes.get(0).getColor().equals(TrainCardColor.rainbow)) {
					super.claimRoute(possRoutes.get(0), maxNumOfColor());
				} else {
					super.claimRoute(possRoutes.get(0), possRoutes.get(0)
							.getColor());
				}
			}// if no more possRoutes, draw cards
			else {

				super.drawTrainCard(drawCard());
			}
		} else {

			/**
			 * test and see if any destination ticket is affordable
			 */
			// iterate through the tickets
			for (int i = 0; i < tickets.size(); i++) {
				// return true if at least one route in ticket is affordable
				if (existAffordableRoute(tickets.get(i))) {
					System.out.println("ticket " + i
							+ " DOES have affordable route");
					nextTicketRouteToBuy(tickets.get(i));
				}

				else {
					System.out.println("ticket " + i
							+ " does NOT have affordable route");
				}
			}

			// if the program comes to here, then, there is no route can be
			// bought
			// within all tickets

			// so we need to draw cards

			// try to find a rainbow card in the facing up ones first
			if (drawCard() == 0) {

			} else {
				Route nextToBuy = nextTicketRouteToBuy(tickets.get(0));
				for (int j = 0; j < super.getFaceUpCards().size(); j++) {
					if (nextToBuy != null
							&& nextToBuy.getColor().equals(
									super.getFaceUpCards().get(j))) {
						System.out
								.println("Found a card that match needed color, drawing it...");
						super.drawTrainCard(j + 1);
					}
				}
				super.drawTrainCard(drawCard());
			}
		}
		super.drawTrainCard(drawCard());

		// // TODO anealing
		// if (super.getNumTrainCardsByColor(maxNumOfColor()) > 5) {
		//
		// for (Route route : Routes.getInstance().getAllRoutes()) {
		// if (!isRouteClaimed(route)) {
		// super.claimRoute(route, route.getColor());
		// }
		// }
		// }

	}

	/*
	 * This method returns the color of the max number.
	 */
	public TrainCardColor maxNumOfColor() {
		HashMap<TrainCardColor, Integer> color = new HashMap<TrainCardColor, Integer>();

		color.put(TrainCardColor.black,
				super.getNumTrainCardsByColor(TrainCardColor.black));
		color.put(TrainCardColor.blue,
				super.getNumTrainCardsByColor(TrainCardColor.blue));
		color.put(TrainCardColor.green,
				super.getNumTrainCardsByColor(TrainCardColor.green));
		color.put(TrainCardColor.orange,
				super.getNumTrainCardsByColor(TrainCardColor.orange));
		color.put(TrainCardColor.purple,
				super.getNumTrainCardsByColor(TrainCardColor.purple));
		color.put(TrainCardColor.red,
				super.getNumTrainCardsByColor(TrainCardColor.red));
		color.put(TrainCardColor.white,
				super.getNumTrainCardsByColor(TrainCardColor.white));
		color.put(TrainCardColor.yellow,
				super.getNumTrainCardsByColor(TrainCardColor.yellow));
		ArrayList<Integer> values = new ArrayList<Integer>(color.values());
		Collections.sort(values);
		System.out.println(values);
		for (TrainCardColor key : color.keySet()) {
			if (color.get(key).equals(values.get(values.size() - 1))) {
				return key;
			}
		}
		return null;

	}

	/*
	 * This method returns the number of rainbow card, if facing up. Default
	 * return 0, from the deck
	 */
	public int drawCard() {
		ArrayList<TrainCard> cards = super.getFaceUpCards();
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).getColor().equals(TrainCardColor.rainbow)) {
				return i + 1;
			}
		}
		return 0;
	}

	/*-
	 * Returns the next best route to buy. 
	 * 1) Eliminate those has been claimed.
	 * 2) Eliminate those non-gray routes that are not affordable.
	 * 3) Include those gray route , which cost less than the max number Card of a color
	 * 4) If the final result is a gray card, use the max number card, instead of rainbow
	 */
	public Route nextTicketRouteToBuy(DestinationTicket ticket) {
		// return a stack of the shortest path from Dest to Dest
		Stack<Route> route = shortestPath(ticket.getFrom(), ticket.getTo());
		// System.out.println("The shortest path is " + route + "\nIn total of "
		// + route.size());
		ArrayList<Route> grayRoutes = new ArrayList<Route>();

		for (int i = 0; i < route.size(); i++) {
			if (!route.isEmpty() && isRouteClaimed(route.get(i))) {
				route.remove(i);
				i--;
			}
		}
		System.out.println("After claim filter, route has " + route.size());

		for (int i = 0; i < route.size(); i++) {
			if (!route.isEmpty()) {
				if (!route.get(i).getColor().equals(TrainCardColor.rainbow)) {
					if (super.getNumTrainCardsByColor(route.get(i).getColor()) < route
							.get(i).getCost()) {
						route.remove(i);
						i--;
					}
				}
				// such route is gray
				else if (super.getNumTrainCardsByColor(maxNumOfColor()) >= route
						.get(i).getCost()) {
					grayRoutes.add(route.get(i));
				}
			}
		}
		System.out.println("After color filter, route has " + route.size());

		if (!route.isEmpty()) {
			Route temp = route.peek();
			for (int i = 0; i < route.size(); i++) {
				if (route.get(i).getCost() > temp.getCost()) {
					if (route.get(i).getOwner() == null) {
						temp = route.get(i);

					}
				}
			}
			System.out.println("Finally, we are going to buy " + temp
					+ ", which worth " + temp.getCost() + " and we have "
					+ super.getNumTrainCardsByColor(temp.getColor()));

			if (grayRoutes.contains(temp)) {
				System.out.println("It's a gray route of " + temp.getCost()
						+ ", using " + maxNumOfColor() + ", which we have "
						+ super.getNumTrainCardsByColor(maxNumOfColor()));
				super.claimRoute(temp, maxNumOfColor());
			} else {
				super.claimRoute(temp, temp.getColor());
			}

			return temp;
		}

		return null;
	}

	/*
	 * This method checks if among the optimal path of a ticket, at least one
	 * route is affordable
	 */
	public boolean existAffordableRoute(DestinationTicket ticket) {
		Stack<Route> route = shortestPath(ticket.getFrom(), ticket.getTo());
		for (int i = 0; i < route.size(); i++) {
			if (route.get(i).getCost() <= super.getNumTrainCardsByColor(route
					.get(i).getColor())) {

				return true;
			}
		}
		return false;
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
	 * Sort the given ArrayList of Route to descending order of cost
	 */
	public ArrayList<DestinationTicket> sortDescenDestinTickets(
			ArrayList<DestinationTicket> tickets) {
		// sort the list in ascending order of cost
		((List<DestinationTicket>) tickets).sort((o1, o2) -> Integer.compare(
				o1.getValue(), o2.getValue()));
		// reverse the list so that the larger cost will be bought first
		Collections.reverse(tickets);
		return tickets;
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

				if (color.equals(TrainCardColor.rainbow)
						&& super.getNumTrainCardsByColor(maxNumOfColor()) >= cost) {
					System.out.println("has "
							+ super.getNumTrainCardsByColor(maxNumOfColor())
							+ ", require " + cost);
					ret.add(nextRoute);
				} else

				// if player has more than the required cost of such color,
				// add to return ArrayList
				if (super.getNumTrainCardsByColor(color) >= cost) {

					ret.add(nextRoute);
				}
			}
		}
		return ret;
	}

	public boolean anyClaimedRouteBetween(Destination dest1, Destination dest2) {
		for (int j = 0; j < getRoutes(dest1, dest2).size(); j++) {
			if (isRouteClaimed(getRoutes(dest1, dest2).get(j)) == true)
				return true;
		}
		return false;
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
		System.out.println("RETURNING NULL");
		return null;
	}

	/*
	 * return false if all routes has been claimed by opponent or this Player
	 * has claimed one
	 */

}
