package ttr.model.trainCards;

/**
 * Train card deck for ticket to ride
 * */
public class TrainCardDeck {
	
	/*  */
	
	public TrainCardDeck(){
		
	}
	
	public TrainCard drawCard(){	
		return new TrainCard(TrainCardColor.getRandomColor());
	}

}
