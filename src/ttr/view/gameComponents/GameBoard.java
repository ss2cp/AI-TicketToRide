package ttr.view.gameComponents;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ttr.model.destinationCards.Destination;
import ttr.model.destinationCards.Route;
import ttr.model.destinationCards.Routes;
import ttr.model.events.PlayerStatChangeEvent;
import ttr.model.player.Player;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.events.general.Event;
import edu.virginia.engine.events.general.IEventListener;

public class GameBoard extends Sprite implements IEventListener{

	private static final String CHECK_MARK_INPUT_FILE = "resources" + File.separator + "txt" + File.separator + "CheckmarkLocations.txt";
	private static final String GREEN_CHECK_FILE_NAME = "CheckMark_Green.png";
	private static final String ORANGE_CHECK_FILE_NAME = "CheckMark_Orange.png";
	
	private String greenPlayerName;
	private String orangePlayerName;
	
	
	public GameBoard(Player player1, Player player2) {
		super("GAME_BOARD", "fullBoard.jpg", new Point(0,0));
		
		initCheckMarkSprites();
		
		greenPlayerName = player1.getName();
		orangePlayerName = player2.getName();
		
		/* Listen to the players */
		player1.addEventListener(this, PlayerStatChangeEvent.PLAYER_POINTS_CHANGED);
		player2.addEventListener(this, PlayerStatChangeEvent.PLAYER_POINTS_CHANGED);
	}
	
	/**
	 * Initializes the checkmarks
	 * */
	private void initCheckMarkSprites(){
		
		/* Read the txt file with the check marks */
		
		/* Open the file and start reading */
		try(BufferedReader br = new BufferedReader(new FileReader(CHECK_MARK_INPUT_FILE))) {
	        String line = br.readLine();
	        while (line != null && !line.equals("%")) {
	            processLine(line);
	            line = br.readLine();
	        }
	    }
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Processes a single line of the input file (when initializing routes)
	 * */
	private void processLine(String line){
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		
		Destination dest1 = Destination.valueOf(tokenizer.nextToken());
		Destination dest2 = Destination.valueOf(tokenizer.nextToken());
		int x = Integer.parseInt(tokenizer.nextToken());
		int y = Integer.parseInt(tokenizer.nextToken());
		
		/* Add the checkmarks */
		Sprite greenCheck = new Sprite(getCheckId(dest1,dest2,1), GREEN_CHECK_FILE_NAME, new Point(x,y));
		greenCheck.setPivotToCenter();
		greenCheck.setScaleX(0.5);
		greenCheck.setScaleY(0.5);
		greenCheck.setVisible(false);
		
		Sprite orangeCheck = new Sprite(getCheckId(dest1,dest2,2), ORANGE_CHECK_FILE_NAME, new Point(x,y));
		orangeCheck.setPivotToCenter();
		orangeCheck.setScaleX(0.5);
		orangeCheck.setScaleY(0.5);
		orangeCheck.setVisible(false);
		
		if(!this.contains(getCheckId(dest1,dest2,1)) && !this.contains(getCheckId(dest2,dest1,1))) this.addChild(greenCheck);
		if(!this.contains(getCheckId(dest1,dest2,2)) && !this.contains(getCheckId(dest2,dest1,2))) this.addChild(orangeCheck);
	}
	
	/**
	 * Returns the ID of the checkmark associated with these dests
	 * */
	private String getCheckId(Destination dest1, Destination dest2, int playerNum){
		String id = dest1 + "_" + dest2 + "_";
		if(playerNum == 1) id += "GREEN";
		else id += "ORANGE";
		
		return id;
	}
	
	private Sprite getCheckmark(Destination dest1, Destination dest2, int playerNum){
		String id = getCheckId(dest1, dest2, playerNum);
		if(this.contains(id)) return (Sprite)this.getChildById(id);
		
		id = getCheckId(dest2, dest1, playerNum);
		if(this.contains(id)) return (Sprite)this.getChildById(id);
		
		return null;
	}
	
	/**
	 * Updates the board so that it is consistent in showing which routes are claimed by which
	 * players
	 * */
	public void updateView(){
		
		for(Destination dest1 : Destination.values()){
			for(Destination dest2 : Destination.values()){
				ArrayList<Route> routes = Routes.getInstance().getRoutes(dest1, dest2);
				for(Route route : routes){
					if(route == null) continue;
					
					/* grab green and orange checkmarks */
					Sprite greenCheck = getCheckmark(dest1, dest2, 1);
					Sprite orangeCheck = getCheckmark(dest1, dest2, 2);
					
					if(Routes.getInstance().isRouteClaimed(route) && route.getOwner() != null){
						if(route.getOwner().getName().equals(greenPlayerName)){
							if(greenCheck != null){
								greenCheck.setVisible(true);
							}
						}
						else{
							if(greenCheck != null){
								greenCheck.setVisible(false);
							}
						}
						
						if(route.getOwner().getName().equals(orangePlayerName)){
							if(orangeCheck != null){
								orangeCheck.setVisible(true);
							}
						}
						else{
							if(orangeCheck != null){
								orangeCheck.setVisible(false);
							}
						}
					}
				}
			}
		}
		
		/* TODO: actually have the board display something */
		
	}

	@Override
	public void handleEvent(Event e) {
		this.updateView();
	}
}
