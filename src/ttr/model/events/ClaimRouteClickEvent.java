package ttr.model.events;

import ttr.model.destinationCards.Route;
import ttr.model.trainCards.TrainCardColor;
import edu.virginia.engine.events.general.Event;
import edu.virginia.engine.events.general.IEventDispatcher;

public class ClaimRouteClickEvent extends Event{

	/* Event Types */
	public static final String CLAIM_ROUTE_CLICKED = "ClaimRouteClickEvent:CLAIM_ROUTE_CLICKED";
	
	/* Necessary info. The route requested and the color card being used to pay for it */
	private Route requestedRoute;
	private TrainCardColor colorToUse;
	
	public ClaimRouteClickEvent(String eventType, IEventDispatcher source, Route requestedRoute, TrainCardColor colorToUse) {
		super(eventType, source);
		
		this.requestedRoute = requestedRoute;
		this.colorToUse = colorToUse;
	}
	
	/**
	 * Getters
	 * */
	public Route getRequestedRoute(){return this.requestedRoute;}
	public TrainCardColor getColorToUse(){return this.colorToUse;}

}
