package ttr.model.player;

import ttr.model.destinationCards.Destination;
import ttr.model.destinationCards.Route;
import ttr.model.trainCards.TrainCardColor;

/**
 * A very stupid player that simply draws train cards only. Shown as an example of implemented a player.
 * */
public class StupidPlayer extends Player{

	/**
	 * Need to have this constructor so the player has a name, you can use no parameters and pass the name of your player
	 * to the super constructor, or just take in the name as a parameter. Both options are shown here.
	 * */
	public StupidPlayer(String name) {
		super(name);
	}
	public StupidPlayer(){
		super("Stupid Player");
	}
	
	/**
	 * MUST override the makeMove() method and implement it.
	 * */
	@Override
	public void makeMove(){
		
		/* Always draw train cards (0 means we are drawing from the pile, not from the face-up cards) */
		super.drawTrainCard(0);
		
		/* This call would allow player to draw destination tickets*/
		super.drawDestinationTickets();
		
		/* Something like this will allow an AI to attempt to buy a route on the board. The first param is the route they wish */
		/* ...to buy, the second param is the card color they wish to pay for the route with (some routes have options here) */
		super.claimRoute(new Route(Destination.Atlanta,  Destination.Miami, 6, TrainCardColor.blue), TrainCardColor.blue);
		
		/* NOTE: This is just an example, a player cannot actually do all three of these things in one turn. The simulator won't allow it */
	}

}
