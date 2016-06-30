package ttr.model.destinationCards;

import ttr.model.player.Player;
import ttr.model.trainCards.TrainCardColor;

/**
 * Information associated with a single route between two
 * destinations. Includes price (in number of train cars), color,
 * cities involved, etc.
 * */
public class Route {

	/* The two destinations */
	private Destination dest1;
	private Destination dest2;
	
	/* cost of this route (in train cars) */
	private int cost = 0;
	
	/* color of card required for this route, for gray this will be assigned to Color.rainbow */
	private TrainCardColor color;
	
	/* Some info about whether this route is already claimed and who claimed it. Null if not claimed yet */
	private Player claimer = null;
	
	/**
	 * Constructor, pretty simple
	 * */
	public Route(Destination dest1, Destination dest2, int cost, TrainCardColor color){
		
		/* Make sure all values are valid, if not print an error */
		if(dest1 == null || dest2 == null) System.out.println("[Fatal Error: Route.java] Constructor: Cannote create route with a null destination");
		if(cost < 1 || cost > 6) System.out.println("[Fatal Error: Route.java] Constructor: Cannote create route with a cost value of " + cost);
		if(color == null) System.out.println("[Fatal Error: Route.java] Constructor: Cannote create route with a null color");
		
		/* Init everything */
		this.dest1 = dest1;
		this.dest2 = dest2;
		
		this.cost = cost;
		
		this.color = color;
		
	}
	
	@Override
	public boolean equals(Object other){
		if(!(other instanceof Route)) return false;
		
		Route otherRoute = (Route)other;
		
		if(this.color != otherRoute.color) return false;
		
		return (this.dest1 == otherRoute.dest1 && this.dest2 == otherRoute.dest2) || 
				(this.dest2 == otherRoute.dest1 && this.dest1 == otherRoute.dest2);
	}
	
	@Override
	public Route clone(){
		Route toReturn = new Route(this.getDest1(), this.getDest2(), this.getCost(), this.getColor());
		toReturn.claimer = this.claimer;
		return toReturn;
	}
	
	/**
	 * Returns how many points this route is worth if claimed
	 * */
	public int getPoints(){
		if(cost == 1 || cost == 2) return cost;
		if(cost == 3) return 4;
		if(cost == 4) return 7;
		if(cost == 5) return 10;
		if(cost == 6) return 15;
		return 0;
	}
	
	/**
	 * Claims this route for this given player
	 * */
	protected void claimThisRoute(Player playerClaiming){
		this.claimer = playerClaiming;
	}
	
	public String toString(){
		return this.dest1 + " to " + this.dest2;
	}
	
	
	/**
	 * Getters
	 * */
	public Destination getDest1(){return this.dest1;}
	public Destination getDest2(){return this.dest2;}
	public int getCost(){return this.cost;}
	public TrainCardColor getColor(){return this.color;}
	public Player getOwner(){return this.claimer;}
	protected boolean isClaimed(){return this.claimer != null;}
	
	
}
