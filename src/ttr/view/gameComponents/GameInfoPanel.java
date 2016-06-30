package ttr.view.gameComponents;

import java.awt.Color;
import java.awt.Font;

import ttr.model.events.PlayerStatChangeEvent;
import ttr.model.player.Player;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.events.general.Event;
import edu.virginia.engine.events.general.IEventListener;
import edu.virginia.engine.widgets.TextBlock;

/**
 * Panel on the right side of the screen that provides info about the game
 * This includes the player scores, etc.
 * */
public class GameInfoPanel extends Sprite implements IEventListener{

	/* The players that are playing the game */
	private Player player1;
	private Player player2;
	
	/* Contains a few text fields */
	private TextBlock player1Score;
	private TextBlock player2Score;
	
	public GameInfoPanel(Player player1, Player player2){
		super("GAME_INFO_PANEL");
		
		/* Cannot be given null players, so check for this */
		if(player1 == null || player2 == null){
			System.out.println("[GameInfoPanel.java] in Constructor: Parameter player1 or player2 given was null. This is not allowed. Returning without setting up the game info panel");
			return;
		}
		
		/* Remember the players */
		this.player1 = player1;
		this.player2 = player2;
		
		/* Initialize the text boxes to show the player scores */
		player1Score = new TextBlock();
		player1Score.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
		player1Score.setColor(Color.WHITE);
		player1Score.setLineWidth(230);
		
		player2Score = new TextBlock();
		player2Score.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
		player2Score.setColor(Color.WHITE);
		player2Score.setLineWidth(player1Score.getLineWidth());
		
		/* Set text blocks to display player scores */
		updatePanels();
		
		/* Add score boxes to the UI */
		player2Score.setX(140);
		this.addChild(player1Score);
		this.addChild(player2Score);
		
		/* Have this panel listen for point changes so that UI is always reflecting exact player score and other stats */
		this.player1.addEventListener(this, PlayerStatChangeEvent.PLAYER_POINTS_CHANGED);
		this.player1.addEventListener(this, PlayerStatChangeEvent.TRAIN_CARDS_CHANGED);
		this.player1.addEventListener(this, PlayerStatChangeEvent.DESTINATION_TICKETS_CHANGED);
		this.player2.addEventListener(this, PlayerStatChangeEvent.PLAYER_POINTS_CHANGED);
		this.player2.addEventListener(this, PlayerStatChangeEvent.TRAIN_CARDS_CHANGED);
		this.player2.addEventListener(this, PlayerStatChangeEvent.DESTINATION_TICKETS_CHANGED);
		
	}
	
	/**
	 * updates player's scores text fields
	 * */
	private void updatePanels(){
		if(player1 == null || player2 == null) return;
		
		player1Score.setText(player1.getPlayerStatsString());
		player2Score.setText(player2.getPlayerStatsString());
	}

	@Override
	public void handleEvent(Event e) {
		
		/* If the event is a points changed event, just update both player's scores */
		if(e instanceof PlayerStatChangeEvent){
			updatePanels();
		}	
	}
}
