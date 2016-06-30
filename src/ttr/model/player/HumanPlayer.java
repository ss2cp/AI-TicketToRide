package ttr.model.player;

import edu.virginia.engine.events.general.Event;
import edu.virginia.engine.events.general.IEventListener;
import edu.virginia.engine.events.mouse.MouseEvent;
import ttr.model.events.ClaimRouteClickEvent;
import ttr.model.events.TrainCardClickEvent;
import ttr.view.gameComponents.DestinationTicketDeckView;
import ttr.view.gameComponents.RouteSelectionPanel;
import ttr.view.gameComponents.TrainCardDeckView;

public class HumanPlayer extends Player implements IEventListener{

	public HumanPlayer(String name) {
		super(name);
	}
	public HumanPlayer(){
		super("Human Player");
	}
	
	/**
	 * initializes human player. Listens to the UI elements so this class knows when something was clicked
	 * */
	public void init(TrainCardDeckView trainCardDeck, DestinationTicketDeckView destTicketDeck, RouteSelectionPanel routeSelector){
		
		/* listen for clicks */
		trainCardDeck.addEventListener(this, TrainCardClickEvent.TRAIN_CARD_CLICKED);
		destTicketDeck.addEventListener(this, MouseEvent.OBJECT_CLICKED);
		routeSelector.addEventListener(this, ClaimRouteClickEvent.CLAIM_ROUTE_CLICKED);
	}
	
	@Override
	public void handleEvent(Event e) {
		
		if(e.getEventType().equals(TrainCardClickEvent.TRAIN_CARD_CLICKED)){
			super.drawTrainCard(((TrainCardClickEvent) e).getCardIndex());
		}
		else if(e.getEventType().equals(MouseEvent.OBJECT_CLICKED)){
			super.drawDestinationTickets();
		}
		else if(e.getEventType().equals(ClaimRouteClickEvent.CLAIM_ROUTE_CLICKED)){
			ClaimRouteClickEvent evt = (ClaimRouteClickEvent)e;
			super.claimRoute(evt.getRequestedRoute(), evt.getColorToUse());
		}
	}
	
	/**
	 * makeMove(). Doesn't need to do anything because moves are handled by the interface.
	 * */
	@Override
	public void makeMove(){}
	
}
