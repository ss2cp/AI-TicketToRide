package ttr.test;

import java.lang.reflect.Constructor;

import edu.virginia.engine.events.general.Event;
import edu.virginia.engine.events.general.IEventListener;
import ttr.main.TicketToRide;
import ttr.model.destinationCards.Routes;
import ttr.model.events.GameOverEvent;
import ttr.model.player.Player;
import ttr.view.scenes.TTRGamePlayScene;

/**
 * This class tests out how the students in a course did on the assignment by playing
 * all pairs of students in games against one another. The names of the possible players should be
 * stored in the enum PlayerClassNames which can be found in this same package (ttr.test)
 * */
public class FullCourseTester implements IEventListener{
	
	private TicketToRide game;
	
	/**
	 * Main method. Loops through all combos and plays games
	 * */
	public static void main(String[] args){
		new FullCourseTester();
	}
	
	/**
	 * Constructor
	 * */
	public FullCourseTester(){
		
		/* This is the game object required by the engine (essentially just the game window) */
		game = new TicketToRide();
		
		playNextGame();
	}
	
	/**
	 * data for looping through all games
	 * */
	int numPlayers = PlayerClassName.values().length;
	int[][] p1Scores = new int[PlayerClassName.values().length][PlayerClassName.values().length];
	int[][] p2Scores = new int[PlayerClassName.values().length][PlayerClassName.values().length];
	int p1Index = 0;
	int p2Index = 0;
	int gameNum = 0;
	int MAX_GAMES = 3;
	
	/**
	 * Play the next game in the loop
	 * */
	private void playNextGame(){
		
		/* Get the names of the two players */
		PlayerClassName p1Name = PlayerClassName.values()[p1Index];
		PlayerClassName p2Name = PlayerClassName.values()[p2Index];
		
		/* play the game */
		playGame(p1Name, p2Name);
	}
	
	/**
	 * Runs a single game and returns an array containing the score (player 1 first, then player 2
	 * */
	private void playGame(PlayerClassName player1Name, PlayerClassName player2Name){
		
		Routes.reset();
		
		/* get the player objects */
		Player player1 = getStudentPlayerObject(player1Name);
		Player player2 = getStudentPlayerObject(player2Name);
		
		TTRGamePlayScene scene = new TTRGamePlayScene("Ticket To Ride", "woodBacking.jpg", game, player1, player2);
		game.setCurrentScene(scene);
		player1.setScene(scene);
		player2.setScene(scene);
		scene.addEventListener(this, GameOverEvent.GAME_COMPLETED);
		game.start();
		scene.playGame();
	}
	
	/**
	 * given a student's class name, instantiates and returns an instance of that
	 * player
	 * */
	private Player getStudentPlayerObject(PlayerClassName className){
		
		try{
			Class<?> playerClass = Class.forName("ttr.model.player." + className);
			Constructor<?> constructor = playerClass.getConstructor(String.class);
			Object instance = constructor.newInstance(className.toString());
			return (Player)instance;
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Cannot instantiate: " + className);
			return null;
		}
	}

	@Override
	public void handleEvent(Event e) {
		
		if(e.getEventType().equals(GameOverEvent.GAME_COMPLETED)){
			/* Get the scene and the scores and print them */
			GameOverEvent evt = (GameOverEvent)e;
			evt.getScene().removeEventListener(this, GameOverEvent.GAME_COMPLETED);
			int[] scores = {evt.getScene().getPlayer1Score(), evt.getScene().getPlayer2Score()};
			System.out.println(scores[0] + ", " + scores[1]);
			evt.getScene().getGameRef().stop();
			
			
			/* Enter score into hashmap */
			p1Scores[p1Index][p2Index] += scores[0];
			p2Scores[p1Index][p2Index] += scores[1];
			
			/* Check end condition */
			if(p1Index == numPlayers-1 && p2Index == numPlayers-1){
				printResults();
			}
			else{
				//increment and recall
				if(gameNum < MAX_GAMES-1) gameNum++;
				else{
					gameNum=0;
					p2Index++;
					if(p2Index == numPlayers){
						p1Index++;
						p2Index = 0;
					}
				}
				playNextGame();
			}
		}
	}
	
	/**
	 * Prints out final results
	 * */
	private void printResults(){
		System.out.print("P2Name,");
		for(int i=0; i<numPlayers; i++) System.out.print(PlayerClassName.values()[i] + ", ,");
		System.out.println();
		
		/* For each player 1, print out how they did against each player 2 */
		for(int i=0; i<numPlayers; i++){
			System.out.print(PlayerClassName.values()[i] + ",");
			for(int j=0; j<numPlayers; j++){
				System.out.print(p1Scores[i][j] + ",");
				System.out.print(p2Scores[i][j] + ",");
			}
			System.out.println();
		}
		
		System.exit(0);
	}
}
