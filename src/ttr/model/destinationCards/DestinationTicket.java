package ttr.model.destinationCards;

/**
 * One destination ticket. Contains the from and to locations (order is irrelevant) and other useful information
 * */
public class DestinationTicket {

	/* To and from, order not important */
	private Destination from;
	private Destination to;
	
	public DestinationTicket(Destination from, Destination to){
		this.from = from;
		this.to = to;
	}
	
	public boolean equals(DestinationTicket other){
		return (this.from == other.from && this.to == other.to) || (this.from == other.to && this.to == other.from);
	}
	
	public boolean equals(Destination from, Destination to){
		return equals(new DestinationTicket(from,to));
	}
	
	public int getValue(){
		return Routes.getInstance().shortestPathcost(getFrom(), getTo());
	}
	
	@Override
	public String toString(){
		return this.getFrom() + " and " + this.getTo() + " (" + getValue() + ")";
	}
	
	public Destination getFrom(){return from;}
	public Destination getTo(){return to;}
}
