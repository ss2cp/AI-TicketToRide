package ttr.model.destinationCards;

import java.util.ArrayList;
import java.util.Collections;

public class DestinationTicketDeck {

	/* The tickets left in the deck */
	private ArrayList<DestinationTicket> tickets;
	
	/* issued tickets (i.e., in the hands of the players) */
	private ArrayList<DestinationTicket> issued;
	
	public DestinationTicketDeck(){
		tickets = new ArrayList<DestinationTicket>();
		issued = new ArrayList<DestinationTicket>();
		
		/* Get the cities and generate all of the possible dest cards*/
		generateDestinationTickets();
	}
	
	/**
	 * Generates all of the possible destination ticket cards
	 * */
	private void generateDestinationTickets(){
		ArrayList<Destination> dests = new ArrayList<Destination>();
		for(Destination dest : Destination.values())
			dests.add(dest);
		
		Collections.shuffle(dests);
		
		for(Destination from : dests){
			for(Destination to : dests){
				if(from == to) continue;
				
				DestinationTicket nextTicket = new DestinationTicket(from, to);
				if(!alreadyIssued(nextTicket)) tickets.add(nextTicket);
			}
		}
	}
	
	private boolean alreadyIssued(DestinationTicket ticket){
		for(DestinationTicket issued : tickets){
			if(issued.equals(ticket)) return true;
		}
		
		return false;
	}
	
	public DestinationTicket drawTicket(){
		if(tickets.size() == 0) return null;
		int index = (int) Math.floor(Math.random()*tickets.size());
		DestinationTicket popped = tickets.remove(index);
		issued.add(popped);
		return popped;
	}
	
	public boolean returnTicket(DestinationTicket ticket){
		if(ticket == null) return false;
		if(!issued.contains(ticket)) return false;
		
		issued.remove(ticket);
		tickets.add(ticket);
		return true;
	}
}
