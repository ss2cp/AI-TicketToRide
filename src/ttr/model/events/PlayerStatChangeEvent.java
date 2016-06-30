package ttr.model.events;

import ttr.model.player.Player;
import edu.virginia.engine.events.general.Event;

/**
 * This is an event that the player throws any time the player's stats change
 * UI should listen for this event and update everytime it is heard
 * */
public class PlayerStatChangeEvent extends Event{

	/* The types of events that can be thrown (e.g., player's points amount changed) */
	public static final String PLAYER_POINTS_CHANGED = "PlayerStatChangeEvent:PLAYER_POINTS_CHANGED";
	public static final String TRAIN_CARDS_CHANGED = "PlayerStatChangeEvent:TRAIN_CARDS_CHANGED";
	public static final String DESTINATION_TICKETS_CHANGED = "PlayerStatChangeEvent:DESTINATION_TICKETS_CHANGED";
	
	public PlayerStatChangeEvent(String eventType, Player source) {
		super(eventType, source);
	}
	
	/**
	 * Returns the player that threw this event
	 * */
	public Player getPlayer(){
		return (Player)super.getSource();
	}
	
}
