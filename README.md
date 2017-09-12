# connect-4
Java Based Connect-4 game

## Running
Once you have a working executable .jar file, run the command
```
java -jar ./connect4.jar
```

**Setup**
Select how many rows you want for your connect "4" board.
Select how many columns you want for your board.

Select how many pieces you must connect to win (standard would be 4, but you can input any number other than that)

Select how deep the program should search in its minimax tree.
Lastly, select how long the AI gets to make its move.

**Selecting a game type**

There are 5 game types available.
- Human Vs AI (human goes first)
- AI Vs Human (AI goes first)
- Ai Vs Ai (same AI for  both players)
- Random Player vs AI (Randomg goes first)
- Human vs Human
- There is a sixth option for diagnostics/stress testing

**Playing the game**
Instructions for playing the game will be displayed on the screen before starting gameplay.

## Example output
```
Player 1, is thinking of a move.
Generated Tree in: 24 ms
Found move in: 4 ms
Player 1, Half of your time has expired.
Player 1: column 2

 1   2   3   4   5   6
-------------------------
|   |   |   |   |   |   |
| Q |   |   | Q |   |   |
| R |   |   | R |   |   |
| Q |   |   | Q |   |   |
| R |   |   | R |   |   |
| Q | R |   | Q |   |   |
| R | R | Q | R |   |   |
-------------------------
 1   2   3   4   5   6

The board is worth: 0.040000000000000036 to Player R
Rated in 2 ms
```

It prints out info about the AI's move making process, and thenit says where it went (in this case column 2)
the score of the board according to the hueristic is printed as well
