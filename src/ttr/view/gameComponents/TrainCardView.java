package ttr.view.gameComponents;

import java.awt.Point;

import ttr.model.trainCards.TrainCard;
import ttr.model.trainCards.TrainCardColor;
import edu.virginia.engine.ui.Button;

public class TrainCardView extends Button{
	
	/* The train card associated with this view */
	private TrainCard card;
	
	public TrainCardView(TrainCardColor color) {
		super("TRAIN_CARD", "trainCard_"+color+".png", new Point(0,0));
		
		card = new TrainCard(color);
	}
	
	public TrainCardView(TrainCard card){
		super("TRAIN_CARD", "trainCard_"+card.getColor()+".png", new Point(0,0));
		this.card = card;
	}

	public TrainCard getCard(){return card;}
}
