# AI_3_TicketToRide
An AI agent to play board game Ticket to Ride

![alt tag](https://raw.githubusercontent.com/ss2cp/AI_HW3/master/ScreenShot.png)

## Background
This program uses BFS Search to find the shortest path between two cities, and Simulated Annealing to determine some of the actions to make. The simulator code was provided.

**Rules:** There are 2 types of cards: destination cards and train cards. There are 3 possible actions to take in each turn: draw desination cards, draw train cards, or buy route. There are multiple colors for train cards, including wild cards. Each route within the map has a color attribute and a cost attribute. Finishing destination cards will get a reward of the cost of that route, not finishing them will result in negative punishment of the reward. 

**Goal:** Within 200 turns, or before train cards run out, who has the higher points wins.

**Language:** Java




Inside src/ttr/model/player are the players created to play the game.


