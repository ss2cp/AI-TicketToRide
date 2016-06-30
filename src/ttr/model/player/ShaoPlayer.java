package ttr.model.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		ArrayList<Route> possRoutes = possibleRoutes();

		// sort the list in ascending order of cost
		((List<Route>) possRoutes).sort((o1, o2) -> Integer.compare(
				o1.getCost(), o2.getCost()));
		// reverse the list so that the larger cost will be bought first
		Collections.reverse(possRoutes);

		/**
		 * test and see if any destination ticket is affordable
		 */
		// iterate through the tickets
		for (DestinationTicket ticket : super.getDestinationTickets()) {
			// iterate through the affordable routes
			for (Route route : possRoutes) {
				// if destination ticket is an affordable route
				if ((route.getDest1().equals(ticket.getFrom())
						&& route.getDest2().equals(ticket.getTo()) || (route
						.getDest2().equals(ticket.getFrom()) && route
						.getDest1().equals(ticket.getTo())))) {
					// TODO need to write a method to get the passing route
					// between two cities on the ticket

				}
			}
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
		 * board. The first param is the route they wish
		 */
		/*
		 * ...to buy, the second param is the card color they wish to pay for
		 * the route with (some routes have options here)
		 */
		super.claimRoute(new Route(Destination.Atlanta, Destination.Miami, 6,
				TrainCardColor.blue), TrainCardColor.blue);

		/*
		 * NOTE: This is just an example, a player cannot actually do all three
		 * of these things in one turn. The simulator won't allow it
		 */

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
				// add to return arraylist
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

}
