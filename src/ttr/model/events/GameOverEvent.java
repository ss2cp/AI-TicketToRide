package ttr.model.events;

import ttr.view.scenes.TTRGamePlayScene;
import edu.virginia.engine.events.general.Event;

public class GameOverEvent extends Event{
	
	/* Event types */
	public static final String GAME_COMPLETED = "GameOverEvent:GAME_COMPLETED";
	
	
	public GameOverEvent(String eventType, TTRGamePlayScene gameScene){
		super(eventType, gameScene);
		
	}
	
	/* The source is the scene. Redundant method but makes life easier */
	public TTRGamePlayScene getScene(){return (TTRGamePlayScene)super.getSource();}

}
