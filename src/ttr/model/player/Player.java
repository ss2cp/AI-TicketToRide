package ttr.model.player;

import java.util.ArrayList;

import edu.virginia.engine.events.general.EventDispatcher;
import ttr.model.destinationCards.DestinationTicket;
import ttr.model.destinationCards.Route;
import ttr.model.destinationCards.Routes;
import ttr.model.events.PlayerMakeMoveEvent;
import ttr.model.events.PlayerStatChangeEvent;
import ttr.model.trainCards.TrainCard;
import ttr.model.trainCards.TrainCardColor;
import ttr.view.scenes.TTRGamePlayScene;

/**
 * A single player, either AI or human
 * */
public abstract class Player extends EventDispatcher{
	
	/* The hand of cards the player has */
	private ArrayList<TrainCard> hand;
	
	/* The destination tickets this player currently holds, and another list for the completed tickets */
	private ArrayList<DestinationTicket> tickets;
	private ArrayList<DestinationTicket> completed;
	
	/* Number of points the player has */
	private int points = 0;
	
	/* Number of total train pieces the player has left, starting with 45 */
	private int numTrainPieces = 45;
	
	/* All of the routes this player has claimed */
	private ArrayList<Route> claimedRoutes;
	
	/* Player name */
	private String name = "player";
	
	/* Set to true iff you want to hide the player's stats */
	private boolean hideStats = false;
	
	/* Reference to the game scene the players are playing in */
	private TTRGamePlayScene gameSceneRef;
	
	public Player(String name){
		if(name != null) this.name = name;
		hand = new ArrayList<TrainCard>();
		tickets = new ArrayList<DestinationTicket>();
		completed = new ArrayList<DestinationTicket>();
		claimedRoutes = new ArrayList<Route>();
	}
	
	
	/**
	 * Abstract method makeMove(). When implemented, defines how the player chooses to play. This method should invoke the three methods
	 * below this one within the body of the method: drawTrainCard(), drawDestinationTickets(), claimRoute().
	 * */
	public abstract void makeMove();
	
	/**
	 * Call this method if you've decided to draw a train card.
	 * Provide the index (0 for from pile, 1-5 for face-up cards
	 * */
	protected void drawTrainCard(int index){
		PlayerMakeMoveEvent e = new PlayerMakeMoveEvent(PlayerMakeMoveEvent.DRAW_TRAIN_CARD, this);
		e.setTrainCardIndex(index);
		dispatchEvent(e);
	}
	
	/**
	 * Call this method if you've decided to draw destination tickets
	 * */
	protected void drawDestinationTickets(){
		PlayerMakeMoveEvent e = new PlayerMakeMoveEvent(PlayerMakeMoveEvent.DRAW_DESTINATION_TICKETS, this);
		dispatchEvent(e);
	}
	
	/**
	 * Call this method if you've decided to claim a route on the board
	 * */
	protected void claimRoute(Route route, TrainCardColor colorToUse){
		PlayerMakeMoveEvent e = new PlayerMakeMoveEvent(PlayerMakeMoveEvent.CLAIM_ROUTE, this);
		e.setRequestedRoute(route, colorToUse);
		dispatchEvent(e);
	}
	
	/**
	 * Adding and removing routes this player has claimed, updates points as well.
	 * */
	public boolean addRoute(Route route, TrainCardColor colorToUse){
		if(!Routes.getInstance().isValidRoute(route)) return false;
		if(Routes.getInstance().isRouteClaimed(route)) return false;
		if(claimedRoutes.contains(route)) return false;
		if(numTrainPieces < route.getCost()) return false;
		if(route.getColor() != colorToUse && route.getColor() != TrainCardColor.rainbow && colorToUse != TrainCardColor.rainbow) return false;
		if(colorToUse != TrainCardColor.rainbow){
			if(this.getNumTrainCardsByColor(colorToUse) + this.getNumTrainCardsByColor(TrainCardColor.rainbow) < route.getCost()) return false;
		}
		else
			if(this.getNumTrainCardsByColor(TrainCardColor.rainbow) < route.getCost()) return false;
		
		/* Add the route and points */
		claimedRoutes.add(route);
		Routes.getInstance().claimRoute(route, this);
		addPoints(route.getPoints());
		
		/* Subtract the cards and train cars */
		numTrainPieces -= route.getCost();
		discardCards(colorToUse, route.getCost());
		
		updateDestinationTickets();
		
		return true;
	}
	
	/**
	 * Returns a list of the face up cards on the board
	 * */
	protected ArrayList<TrainCard> getFaceUpCards(){
		return this.gameSceneRef.getFaceUpCards();
	}
	
	/**
	 * Sees if any open destination tickets are complete now, adds points
	 * if necessary
	 * */
	private void updateDestinationTickets(){
		for(DestinationTicket ticket : this.tickets){
			if(Routes.getInstance().hasCompletedRoute(this, ticket.getFrom(), ticket.getTo())){
				this.addPoints(ticket.getValue());
				if(!completed.contains(ticket)) completed.add(ticket);
			}
		}
		
		for(DestinationTicket ticket : this.completed)
			if(tickets.contains(ticket)) tickets.remove(ticket);
		
		dispatchEvent(new PlayerStatChangeEvent(PlayerStatChangeEvent.DESTINATION_TICKETS_CHANGED, this));
	}
	
	
	/**
	 * Adding and removing cards from the players hand
	 * */
	public void addCard(TrainCard card){
		if(hand == null || card == null) return;
		hand.add(card);
		dispatchEvent(new PlayerStatChangeEvent(PlayerStatChangeEvent.TRAIN_CARDS_CHANGED, this));
	}
	
	public void removeCard(TrainCard card){
		if(hand == null || card == null) return;
		if(!hand.contains(card)) return;
		hand.remove(card);
		dispatchEvent(new PlayerStatChangeEvent(PlayerStatChangeEvent.TRAIN_CARDS_CHANGED, this));
	}
	
	/**
	 * Discards an exact number of cards of the same color. Uses rainbow cards if necessary.
	 * Meant to be used when claiming routes to easily pay for the route
	 * returns true iff the discard was successful
	 * */
	private boolean discardCards(TrainCardColor color, int amount){
		
		/* loop through deck and find cards of that color and rainbows */
		ArrayList<TrainCard> colorCards = new ArrayList<TrainCard>();
		ArrayList<TrainCard> rainbowCards = new ArrayList<TrainCard>();
		for(TrainCard card : hand){
			if(card.getColor() == color) colorCards.add(card);
			else if(card.getColor() == TrainCardColor.rainbow) rainbowCards.add(card);
		}
		
		/* If there are enough cards, then use them, otherwise return false */
		if(colorCards.size() + rainbowCards.size() < amount) return false;
		
		/* Remove one card at a time. This will def. work because above check ensured enough cards are present */
		int n = amount;
		while(n>0){
			if(colorCards.size()>0)
				hand.remove(colorCards.remove(0));
			else
				hand.remove(rainbowCards.remove(0));
			n--;
		}
		dispatchEvent(new PlayerStatChangeEvent(PlayerStatChangeEvent.TRAIN_CARDS_CHANGED, this));
		return true;
	}
	
	
	/**
	 * Add/remove destination tickets
	 * */
	public void addDestinationTicket(DestinationTicket ticket){
		if(!alreadyContainsDestinationTickets(ticket)){
			tickets.add(ticket);
			this.updateDestinationTickets();
			dispatchEvent(new PlayerStatChangeEvent(PlayerStatChangeEvent.DESTINATION_TICKETS_CHANGED, this));
		}
	}
	public void removeDestinationTicket(DestinationTicket ticket){
		if(alreadyContainsDestinationTickets(ticket)){
			tickets.remove(getDestinationTicket(ticket));
			dispatchEvent(new PlayerStatChangeEvent(PlayerStatChangeEvent.DESTINATION_TICKETS_CHANGED, this));
		}
	}
	
	/**
	 * Invoked at the end of the game. Subtracts away the destination ticket values from 
	 * the player's score if not completed
	 * */
	public void processDestinationTicketPenalty(){
		for(DestinationTicket ticket : tickets){
			this.addPoints(-1 * ticket.getValue());
		}
	}
	
	
	private boolean alreadyContainsDestinationTickets(DestinationTicket ticket){
		return getDestinationTicket(ticket) != null;
	}
	/**
	 * Gets the instance of the given ticket actually in our hand
	 * */
	private DestinationTicket getDestinationTicket(DestinationTicket ticket){
		for(DestinationTicket myTicket : tickets){
			if(myTicket.equals(ticket)) return myTicket;
		}
		return null;
	}
	
	/**
	 * Returns the number of train cards this player has of the given color
	 * */
	public int getNumTrainCardsByColor(TrainCardColor color){
		int count = 0;
		for(TrainCard card : this.hand){
			if(card.getColor() == color) count++;
		}
		return count;
	}
	
	/**
	 * Getter for num train pieces
	 * */
	public int getNumTrainPieces(){return this.numTrainPieces;}

	/**
	 * Getter / Setter for points
	 * */
	public int getPoints(){return this.points;}
	
	private void addPoints(int amount){
		this.points += amount;
		
		/* Let any UI elements that care know that this player's number of points has changed */
		dispatchEvent(new PlayerStatChangeEvent(PlayerStatChangeEvent.PLAYER_POINTS_CHANGED, this));
	}

	/**
	 * returns the string version of this player's hand / tickets / points for the purpose of a UI.
	 * 
	 * */
	public String getPlayerStatsString(){
		
		String toReturn = this.name + ": " + this.getPoints() + " points";
		toReturn += "\n\n";
		
		if(this.hideStats) return toReturn + "??????????";
		
		/* Number of train pieces left */
		toReturn += "Train Pieces Left: " + this.numTrainPieces;
		toReturn += "\n\n";
		
		/* List out the train cards by color in player's hand */
		toReturn += "Train Cards:\n";
		for(TrainCardColor color : TrainCardColor.values()){
			int numCards = this.getNumTrainCardsByColor(color);
			if(numCards > 0)
				toReturn += "    " + color + ": " + numCards + " cards\n";
		}
		
		/* List out the player's destination tickets */
		toReturn += "\n";
		toReturn += "Destination Tickets:\n";
		for(DestinationTicket ticket : this.tickets){
			toReturn += "    " + ticket.toString() + "\n";
		}
		
		return toReturn;
	}
	
	/**
	 * Returns total cost of dest tickets currently in hand
	 * */
	public int getTotalDestTicketCost(){
		int total = 0;
		for(DestinationTicket ticket : this.tickets){
			total += ticket.getValue();
		}
		return total;
	}
	
	public void setScene(TTRGamePlayScene sceneRef){
		this.gameSceneRef = sceneRef;
	}
	
	public String getName(){return this.name;}
	public ArrayList<TrainCard> getHand(){return this.hand;}
	public ArrayList<DestinationTicket> getDestinationTickets(){return this.tickets;}
	public ArrayList<Route> getPlayerClaimedRoutes(){return this.claimedRoutes;}
	
	
	public void setHideStats(boolean hide){this.hideStats = hide;}
}
