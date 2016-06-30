package ttr.view.gameComponents;

import java.awt.Point;

import ttr.model.destinationCards.DestinationTicketDeck;
import edu.virginia.engine.ui.Button;

public class DestinationTicketDeckView extends Button{
	
	/* The actual deck (model) */
	private DestinationTicketDeck deck;

	public DestinationTicketDeckView(){
		super("DESTINATION_TICKET_DECK", "ticketDeck.png", new Point(0,0));
		
		this.deck = new DestinationTicketDeck();
	}
	
	public DestinationTicketDeckView(Point position){
		super("DESTINATION_TICKET_DECK", "ticketDeck.png", position);
		
		this.deck = new DestinationTicketDeck();
	}
	
	/* return the deck */
	public DestinationTicketDeck getDeck(){return deck;}
}
