package ttr.view.gameComponents;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import edu.virginia.engine.widgets.TextBlock;

/**
 * A text block showing a log of what has happened in the game.
 * */
public class GameLogView extends TextBlock {

	
	/* The number of log messages shown at a single time. The most recent logs are shown only */
	private static int NUM_MESSAGES_TO_DISPLAY = 5;
	
	/* The list of the log messages */
	private ArrayList<String> log = new ArrayList<String>();
	
	public GameLogView(){
		super();
		
		this.setLineWidth(500);
		this.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
		this.setColor(Color.WHITE);
		this.setText("-------------------------- GAME LOG --------------------------\n\n");
	}
	
	/**
	 * Logs the given message and updates the text to be drawn
	 * */
	public void logMessage(String message){
		if(message == null || message.equals("")) return;
		
		log.add(message);
		
		/* Print out to the console as a secondary record */
		System.out.println(message);
		
		String text = "-------------------------- GAME LOG --------------------------\n\n";
		
		int count = 1;
		while(count <= NUM_MESSAGES_TO_DISPLAY){
			if(log.size() - count < 0) break;
			String nextMessage = log.get(log.size() - count);
			text += nextMessage + "\n\n";
			count++;
		}
		
		this.setText(text);	
	}
}
