package ttr.model.trainCards;


public enum TrainCardColor{
	
	rainbow,
	black,
	blue,
	green,
	orange,
	purple,
	red,
	white,
	yellow;
	
	public static TrainCardColor getRandomColor(){
		return TrainCardColor.values()[(int)(Math.random()*TrainCardColor.values().length)];
		
	}
}