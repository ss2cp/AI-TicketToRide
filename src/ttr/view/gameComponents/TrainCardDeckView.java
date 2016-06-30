package ttr.view.gameComponents;

import java.awt.Point;
import java.util.ArrayList;

import ttr.model.events.TrainCardClickEvent;
import ttr.model.trainCards.TrainCard;
import ttr.model.trainCards.TrainCardDeck;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.events.general.Event;
import edu.virginia.engine.events.general.IEventListener;
import edu.virginia.engine.events.mouse.MouseEvent;
import edu.virginia.engine.ui.Button;

public class TrainCardDeckView extends Sprite implements IEventListener{
	
	/* The deck of cards */
	private TrainCardDeck deck;
	
	/* The deck card back image along with the five cards */
	private Button cardBack;
	private ArrayList<TrainCardView> cards;

	public TrainCardDeckView(){
		super("TRAIN_CARDS_VIEW");
		
		deck = new TrainCardDeck();
		initialize();
	}
	
	private void initialize(){
		this.cardBack = new Button("TRAIN_CARD_BACK", "cardBack.png", new Point(0,0));
		this.addChild(cardBack);
		
		cards = new ArrayList<TrainCardView>();
		for(int i=0; i<5; i++) pullNewSharedCard(i);
		
		/* Listen for clicks */
		cardBack.addEventListener(this, MouseEvent.OBJECT_CLICKED);
	}
	
	/**
	 * Pulls a new shared card and places it in position index, returns the card that was removed
	 * */
	private TrainCardView pullNewSharedCard(int index){
		
		if(index < 0 || index > 4) return null;
		
		/* Get a new card */
		TrainCardView newCard = new TrainCardView(deck.drawCard());
		newCard.addEventListener(this, MouseEvent.OBJECT_CLICKED);
		
		/* If card already present, then remove it */
		if(cards.size() > index && cards.get(index) != null){
			TrainCardView oldCard = cards.get(index);
			newCard.setLocation(new Point(oldCard.getX(), oldCard.getY()));
			this.removeChild(oldCard);
			this.addChild(newCard, index);
			
			cards.set(index, newCard);

			oldCard.removeEventListener(this, MouseEvent.OBJECT_CLICKED);
			return oldCard;
		}
		else{
			cards.add(newCard);
			int xLoc = cardBack.getScaledWidth() + 20 + (cards.size()-1)*(newCard.getScaledWidth()+10);
			int yLoc = (int)(cardBack.getScaledHeight() / 5);
			
			newCard.setLocation(new Point(xLoc, yLoc));
			
			this.addChild(newCard);
			return null;
		}
	}
	
	/**
	 * Given an index, either pulls returns a random card from the deck (if index is 0) or returns one
	 * of the face-up cards (index is 1-5) and replaces it
	 * */
	public TrainCard getCard(int index){
		if(index < 0 || index > 5) return null;
		
		/* If user wants, pull a random card from top of deck */
		if(index == 0) return getDeck().drawCard();
		
		/* Otherwise, give them the card they asked for and replace with a new face-up card */
		TrainCard toReturn = cards.get(index-1).getCard();
		pullNewSharedCard(index-1);
		return toReturn;
	}
	
	private int getIndexOfCard(TrainCardView card){
		if(card == null) return 0;
		
		for(int i = 0; i < cards.size(); i++){
			if(cards.get(i) == card) return i+1;
		}
		
		return 0;
	}
	
	/**
	 * Let listeners know that card has been clicked on
	 * */
	@Override
	public void handleEvent(Event e) {
		if(!(e instanceof MouseEvent)) return;
		int index = 0;
		if(e.getSource() instanceof TrainCardView)
			index = getIndexOfCard((TrainCardView)e.getSource());
		
		dispatchEvent(new TrainCardClickEvent(TrainCardClickEvent.TRAIN_CARD_CLICKED, e.getSource(), index));
	}
	
	/* Returns the model deck object */
	public TrainCardDeck getDeck(){return deck;}
	public ArrayList<TrainCardView> getFaceUpCards(){return cards;}
	
}
