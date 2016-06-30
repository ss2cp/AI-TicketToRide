package ttr.model.events;

import edu.virginia.engine.events.general.Event;
import edu.virginia.engine.events.general.IEventDispatcher;

/**
 * Event thrown when the UI train deck is clicked on (either the deck or the face-up cards)
 * */
public class TrainCardClickEvent extends Event{

	/* Type of events possible */
	public static final String TRAIN_CARD_CLICKED = "TrainCardClickEvent:TRAIN_CARD_CLICKED";
	
	/* The index of the card that was clicked (0 for deck or 1-5 for face-up cards */
	private int cardIndex;
	
	public TrainCardClickEvent(String eventType, IEventDispatcher source, int cardIndex) {
		super(eventType, source);
		
		this.cardIndex = cardIndex;
	}
	
	public int getCardIndex(){return cardIndex;}

}
