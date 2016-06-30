package ttr.model.events;

import edu.virginia.engine.events.general.Event;
import edu.virginia.engine.events.general.IEventDispatcher;
import ttr.model.destinationCards.Route;
import ttr.model.trainCards.TrainCardColor;


/**
 * This class is an event thrown by the player (see Player.java and subclasses) when the player
 * wants to make a move on his/her turn. The event lets the main game module know what the player
 * wants to do so the game can update everything accordingly (i.e., give the player cards or routes, etc).
 * */
public class PlayerMakeMoveEvent extends Event{

	/* The different type of moves a player can make */
	public static final String DRAW_TRAIN_CARD = "PlayerMakeMoveEvent_DRAW_TRAIN_CARD";
	public static final String DRAW_DESTINATION_TICKETS = "PlayerMakeMoveEvent_DRAW_DESTINATION_TICKETS";
	public static final String CLAIM_ROUTE = "PlayerMakeMoveEvent_CLAIM_ROUTE";
	
	/* if drawing cards, which one you want (0 for deck, or 1-5 for one of the face-up cards) */
	private int trainCardIndex = 0;
	
	/* if claiming route, the route requested and the card color to use */
	private Route requestedRoute;
	private TrainCardColor colorToUse;
	
	/**
	 * Constructor
	 * */
	public PlayerMakeMoveEvent(String eventType, IEventDispatcher source){
		super(eventType, source);
	}
	
	/**
	 * Setters
	 * */
	public void setRequestedRoute(Route requestedRoute, TrainCardColor colorToUse){
		this.requestedRoute = requestedRoute;
		this.colorToUse = colorToUse;
	}
	public void setTrainCardIndex(int index){this.trainCardIndex = index;}
	
	/**
	 * Getters
	 * */
	public Route getRequestedRoute(){return this.requestedRoute;}
	public int getTrainCardIndex(){return this.trainCardIndex;}
	public TrainCardColor getColorToUse(){return this.colorToUse;}
}
