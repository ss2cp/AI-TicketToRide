package ttr.view.scenes;

import java.awt.Point;
import java.util.ArrayList;

import ttr.model.events.GameOverEvent;
import ttr.model.events.PlayerMakeMoveEvent;
import ttr.model.player.HumanPlayer;
import ttr.model.player.Player;
import ttr.model.trainCards.TrainCard;
import ttr.view.gameComponents.DestinationTicketDeckView;
import ttr.view.gameComponents.GameBoard;
import ttr.view.gameComponents.GameInfoPanel;
import ttr.view.gameComponents.GameLogView;
import ttr.view.gameComponents.RouteSelectionPanel;
import ttr.view.gameComponents.TrainCardDeckView;
import ttr.view.gameComponents.TrainCardView;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.GameScene;
import edu.virginia.engine.events.general.Event;
import edu.virginia.engine.events.general.IEventListener;

public class TTRGamePlayScene extends GameScene implements IEventListener{	
	
	/* The two players involved */
	private Player player1;
	private Player player2;
	
	/* The main board */
	private GameBoard board;
	
	/* Destination Ticket Deck */
	private DestinationTicketDeckView destTicketDeck;
	
	/* Area for the train cards deck and such */
	private TrainCardDeckView trainCardDeck;
	
	/* Info panel that shows stats for game */
	private GameInfoPanel infoPanel;
	
	/* Panel for selecting a route to claim */
	private RouteSelectionPanel routeSelector;
	
	/* The player whose turn it currently is */
	private Player currentPlayer;
	
	/* Maximum number of moves allowed in a game. Currently set to 200 (so 100 for each player) */
	private final int MAX_TURNS = 200;
	private int curTurnNumber = 0; //number of moves taken so far
	
	/* Game Logger (lower right hand of UI) */
	private GameLogView gameLog;
	
	private boolean changeTurns = false;

	public TTRGamePlayScene(String sceneId, String backgroundFileName, Game gameRef, Player player1, Player player2) {
		super(sceneId, backgroundFileName, gameRef);
		
		/* Save player objects */
		this.player1 = player1;
		this.player2 = player2;
		
		/* Init and place the main board */
		board = new GameBoard(player1, player2);
		board.setLocation(new Point(15, 20));
		board.setScaleX(0.95);
		board.setScaleY(0.95);
		this.addChild(board);
		
		/* Init and place the deck of destination tickets */
		destTicketDeck = new DestinationTicketDeckView();
		destTicketDeck.setLocation(new Point(20,this.getUnscaledHeight()-destTicketDeck.getUnscaledHeight()-50));
		this.addChild(destTicketDeck);
		
		/* Init and place and deal the train cards */
		trainCardDeck = new TrainCardDeckView();
		trainCardDeck.setScaleX(0.7);
		trainCardDeck.setScaleY(0.7);
		trainCardDeck.setLocation(new Point(destTicketDeck.getX(), destTicketDeck.getY() - 160));
		this.addChild(trainCardDeck);
		
		/* Initialize the stats panel */
		infoPanel = new GameInfoPanel(this.player1, this.player2);
		infoPanel.setLocation(new Point(board.getScaledWidth() + 20 + 20, 20));
		this.addChild(infoPanel);
		
		/* Initialize and place the route selector */
		routeSelector = new RouteSelectionPanel();
		routeSelector.setX(destTicketDeck.getX() + destTicketDeck.getScaledWidth() + 100);
		routeSelector.setY(destTicketDeck.getY() + 20);
		this.addChild(routeSelector);
		//routeSelector.init();
		
		/* Init game log */
		gameLog = new GameLogView();
		gameLog.setX(480);
		gameLog.setY(390);
		this.addChild(gameLog);
		
		initializeGame();
	}
	
	private void initializeGame(){
		
		/* Deal 5 train cards to each player */
		for(int i=0; i<5; i++){
			player1.addCard(trainCardDeck.getDeck().drawCard());
			player2.addCard(trainCardDeck.getDeck().drawCard());
		}
		
		/* Deal two destination tickets */
		for(int i=0; i<2; i++){
			player1.addDestinationTicket(destTicketDeck.getDeck().drawTicket());
			player2.addDestinationTicket(destTicketDeck.getDeck().drawTicket());
		}
		
		/* Listen for the two players to make moves */
		player1.addEventListener(this, PlayerMakeMoveEvent.CLAIM_ROUTE);
		player1.addEventListener(this, PlayerMakeMoveEvent.DRAW_DESTINATION_TICKETS);
		player1.addEventListener(this, PlayerMakeMoveEvent.DRAW_TRAIN_CARD);
		
		/* if human player, initialize the player */
		if(player1 instanceof HumanPlayer) ((HumanPlayer) player1).init(trainCardDeck, destTicketDeck, routeSelector);
		if(player2 instanceof HumanPlayer) ((HumanPlayer) player2).init(trainCardDeck, destTicketDeck, routeSelector);
	}
	
	/**
	 * Plays the game by calling makeMove() over and over on various players
	 * */
	public void playGame(){
		currentPlayer = player1;
		nextTurn();
	}
	
	/**
	 * Performs the next turn
	 * */
	/* Lock for taking turns */
	private boolean turnLocked = false;
	private void nextTurn(){
		/* If the game is over, just stop it now */
		if(gameOver()){
			endGame();
			return;
		}
		
		this.getGameRef().repaint();
		//sleep(500);
		turnLocked = true;
		try{
			currentPlayer.makeMove();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		if(!(currentPlayer instanceof HumanPlayer)) changeTurns = true;
		turnLocked = false;
		
	}
	
	/**																																																																																																
	 * Ends the game
	 * */
	private void endGame(){
		/* Set current player to null and remove all listeners */
		currentPlayer = null;
		player1.removeEventListener(this, PlayerMakeMoveEvent.CLAIM_ROUTE);
		player1.removeEventListener(this, PlayerMakeMoveEvent.DRAW_DESTINATION_TICKETS);
		player1.removeEventListener(this, PlayerMakeMoveEvent.DRAW_TRAIN_CARD);
		player2.removeEventListener(this, PlayerMakeMoveEvent.CLAIM_ROUTE);
		player2.removeEventListener(this, PlayerMakeMoveEvent.DRAW_DESTINATION_TICKETS);
		player2.removeEventListener(this, PlayerMakeMoveEvent.DRAW_TRAIN_CARD);
		
		player1.processDestinationTicketPenalty();
		player2.processDestinationTicketPenalty();
		
		this.dispatchEvent(new GameOverEvent(GameOverEvent.GAME_COMPLETED, this));
	}
	
	/**
	 * causes the game to sleep for the given number of milliseconds. 
	 * Useful when you want to watch an AI play at a slightly slower pace (sometimes is WAY too fast)
	 * */
	private void sleep(int milliseconds){
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is called when a player asks to make a move by throwing an event with the details of the move to make
	 * This function will update the game appropriately, change which player's turn it is, check the end game conditions, etc.
	 * */
	@Override
	public void handleEvent(Event e) {
		/* If (somehow) this is not a make move event, then just return and move on */
		if(!(e instanceof PlayerMakeMoveEvent)) return;
		if(changeTurns) return;
		
		/* cast into the right type of object */
		PlayerMakeMoveEvent event = (PlayerMakeMoveEvent)e;
		
		/* Make sure the player trying to make a move is the player whose turn is currently active */
		if((Player)e.getSource() != currentPlayer){
			gameLog.logMessage("[WARNING in TTRGamePlayScene.java: handleEvent()] a player whose turn is not active is trying to make a move. Please debug this. Ignoring requested move");
			return;
		}
		
		/* If the player asked to draw destination tickets, then give them the tickets */
		if(event.getEventType().equals(PlayerMakeMoveEvent.DRAW_DESTINATION_TICKETS)){
			gameLog.logMessage("[GameLog Turn #" + curTurnNumber + "]: " + currentPlayer.getName() + " is drawing two destination tickets.");
			currentPlayer.addDestinationTicket(destTicketDeck.getDeck().drawTicket());
			currentPlayer.addDestinationTicket(destTicketDeck.getDeck().drawTicket());
			
			changeTurns = true;
		}
		/* If the player wants to draw a train card, then give them the card they want */
		else if(event.getEventType().equals(PlayerMakeMoveEvent.DRAW_TRAIN_CARD)){
			/* Get the index of the card they want and draw it */
			gameLog.logMessage("[GameLog Turn #" + curTurnNumber + "]: " + currentPlayer.getName() + " is pulling train card from index " + event.getTrainCardIndex());
			currentPlayer.addCard(trainCardDeck.getCard(event.getTrainCardIndex()));
			
			changeTurns = true;
		}
		else if(event.getEventType().equals(PlayerMakeMoveEvent.CLAIM_ROUTE)){
			if(!(event.getRequestedRoute() == null)) gameLog.logMessage("[GameLog Turn #" + curTurnNumber + "]: " + currentPlayer.getName() + " is claiming route " + event.getRequestedRoute().toString());
			
			if(currentPlayer.addRoute(event.getRequestedRoute(), event.getColorToUse())){	
				System.out.println("Inside Claim if (claim worked). Setting changeTurns to true");
				changeTurns = true;
			}
			else{
				gameLog.logMessage("[GameLog Turn #" + curTurnNumber + "]: Sorry, you can't claim that route for some reason");
			}
		}
	}
	
	/**
	 * Checks the end game conditions, returns true iff the game should end on this turn
	 * */
	private boolean gameOver(){
		if (player1.getNumTrainPieces() < 3 || player2.getNumTrainPieces() < 3) return true;
		if (curTurnNumber > MAX_TURNS) return true;
		return false;
	}
	
	/**
	 * Changes the player turns to the other player
	 * */
	private void changeTurns(){
		currentPlayer.removeEventListener(this, PlayerMakeMoveEvent.CLAIM_ROUTE);
		currentPlayer.removeEventListener(this, PlayerMakeMoveEvent.DRAW_DESTINATION_TICKETS);
		currentPlayer.removeEventListener(this, PlayerMakeMoveEvent.DRAW_TRAIN_CARD);
		
		if(currentPlayer == null) currentPlayer = player1;
		if(currentPlayer == player1) currentPlayer = player2; else currentPlayer = player1;
		
		currentPlayer.addEventListener(this, PlayerMakeMoveEvent.CLAIM_ROUTE);
		currentPlayer.addEventListener(this, PlayerMakeMoveEvent.DRAW_DESTINATION_TICKETS);
		currentPlayer.addEventListener(this, PlayerMakeMoveEvent.DRAW_TRAIN_CARD);
		
		curTurnNumber++;
	}
	
	/**
	 * Get the face up cards on the board
	 * */
	public ArrayList<TrainCard> getFaceUpCards(){
		ArrayList<TrainCard> toReturn = new ArrayList<TrainCard>();
		for(TrainCardView card : trainCardDeck.getFaceUpCards())
			toReturn.add(card.getCard());
		return toReturn;
	}
	
	@Override
	public void update(){
		super.update();
		
		if(changeTurns && !turnLocked){
			changeTurns = false;
			changeTurns();
			nextTurn();
		}
	}
	
	public int getPlayer1Score(){return player1.getPoints();}
	public int getPlayer2Score(){return player2.getPoints();}
}
