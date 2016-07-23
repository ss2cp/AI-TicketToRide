# AI-TicketToRide
An AI agent to play board game Ticket to Ride

![GameBoard](https://raw.githubusercontent.com/ss2cp/AI_HW3/master/ScreenShot.png)

## Background
This program uses BFS Search to find the shortest path between two cities, and Simulated Annealing to determine some of the actions to make. The simulator code was provided.

**Rules:** 

- There are 2 types of cards: *destination cards* and *train cards*. 

- There are 3 possible actions to take in each turn: *draw 2 desination cards*, *draw 1 train card*, or *buy 1 route*. 

- There are multiple colors for train cards, including wild cards. 

- Each route within the map has a color attribute and a cost attribute. 

- Finishing destination cards will get a reward of the cost of that route, not finishing them will result in negative punishment of the reward. 

**Goal:** Within 200 turns, or before train cards run out, who has the higher points wins.

**Language:** Java

## Usage
Under `src/ttr.model/player` are the players created to play the game.

Run `src/ttr.main/TTRMain` to start the GUI and the game.

## Game Strategy
After a brief analysis of how human play this game, we found the following strategies:

1. **Finish game fast.** This gives us the advantage of having more routes purchased while our opponent is still building out his path or hoarding cards.
2. **Finish 2 destination tickets.** This gives a baseline for planning out routes, as well as takes away the negative penalty at the end of game due to incomplete destination tickets.
3. **Prioritize longer routes first.** Since the Value Per Train increases as the route becomes longer, the longer routes give a much better return on our investment and thus should be prioritized.
4. **If impossible to finish destination ticket, buy most expensive ones.** Normally, an average game with our algorithm will take up to 130 turns in total. So we implemented an annealing factor to deal with later in the game where the algorithm may not be able to finish the destination ticket.

##Pseudo Code for Annealing Factor
```java
annealingFactor(){
  if turn < 40
	  annealingFactor = (1 - (turn / 100)) * 80 + 20
  Else
	  annealingFactor = (1 - (turn / 100)) * 100
}
```
##Pseudo Code for Decision Making
```java
dicisionMake(){
  turn++ // increment turns counter
  TICKETS // set of all destination tickets in hand
  possibleRoutes // set of all AFFORDABLE routes sorted in descending cost order
  
  if TICKETS is empty
  	if turn <= 20
  		draw destination tickets // if finish all destination tickets before 20th turn, draw another 2
  	else
  		if currently have more than 5 train cards of same color
        claim affordable route
      else
        randomDecision // a random number < 100
        // more towards later the game, more likely to buy routes than drawing cards
        if randomDecision < annealingFactor
  		    drawTrainCard	// prefer rainbow cards
  		  else
  		    if possibleRoutes is not empty
  			    claim route
  		    else
  			    drawTrainCard // prefer rainbow cards
  else // still have destination tickets in hand
  	if there is an affordable route in shortest path of any of the destination tickets
  		claim route
  	else
  		drawTrainTicket // prefer rainbow cards
}
```
##Simulated Annealing
Two sepertate annealing factor functions for early game strategy and later game strategy. In later games, the probability of drawing cards drops faster. 

![Annealing Factor Graph](https://raw.githubusercontent.com/ss2cp/AI_HW3/master/imgs/annealing.png)

## Testing
We created 4 players for testing. We let MainPlayer play 50 games against the other 3 players. (25 where opponent went first)
####MainPlayer
MainPlayer implements the pseudo code given above.
####ShaoPlayer
ShaoPlayer implements a very similar algorithm as our main algorithm does, with the exception that ShaoPlayer does not use annealing.
####BasicPlayer
BasicPlayer’s strategy is to buy routes as soon as it can afford one. This strategy leads to a hoarding of short routes, which can potentially lead to blocking a path the opponent has planned.
####StupidPlayer
The baseline case is StupidPlayer. It only draws card and does nothing else. 

## Results
The results are as followed:

| Player 1  | Player 2 |P1 Average Score|P2 Average Score|Point Difference (P1-P2)|
| ------------- | ------------- | ------------- | ------------- |------------- |
| Main algorithm  | ShaoPlayer  |59.68|70.94|-11.26|
| Main algorithm  | BasicPlayer |82.85|76.29|6.56|
| Main algorithm  | StupidPlayer|90.94|0.00|90.94|

##Annalysis

The key observation we made in regards to winning games is the importance of finishing your destination tickets. Many games in which our main algorithm was able to beat ShaoPlayer are games where ShaoPlayer was not able to complete his destination tickets while own main algorithm did. This not only provides a penalty differential more favorable to our main algorithm, but also shows a more efficient use of card drawing.

We were surprised that ShaoPlayer was able to defeat our main algorithm more often. Our main algorithm’s annealing factor does not kick in until it has completed its destination tickets, before which point, ShaoPlayer and the main algorithm actually utilize the same algorithm in drawing cards and claiming routes. Thus, the fact that our main algorithm loses more often and most losses are happen when it fails to complete its destination tickets shows that its annealing factor did not have an impact on the game since it did not get to utilize that part of the algorithm, and that many games are won or lost on pure luck of the draw when the initial destination tickets are given.




