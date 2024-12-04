# Othello Game

A multi-player Othello game that allows clients to play against each other on a server.<br>
Othello is a two-player board game where the two players take turns placing discs of their color.<br>
Each move must capture at least one disc of the opponent. If this is not possible, the player must pass, unless the other player also cannot make such a move. <br>
The game ends when neither player can capture from their opponent. The winner is the player with the most discs. The game ends in a draw when both players have the same number of discs.<br>
For more info about the rules of Othello, see https://www.worldothello.org/about/about-othello/othello-rules/official-rules/english

## Notice

The colors of the discs (black and white) are inverted to fit the dark theme. If a light theme is used, the colors will be inverted.<br>
Can be changed in the `OthelloGame` class, by interchanging the return encodings in the `getMarkChar` method.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine.

### Prerequisites

- JDK 11 or higher.

### Installing

Clone the repository to your local machine:

```shell
$ git clone https://github.com/AnghelusCristi/Othello_Game.git
```
### Running the Server

- To start the server, run the StartServer class in the server package, nested in the network package.<br><br>
- The user will be prompted to enter the port number for the server (can be left blank to start at port 44444 by default). The server will then start and wait for clients to connect.<br><br>
- The server can be stopped by entering "quit" in the console.

### Running the Client

- The client is started via a TUI, namely the OthelloTUI class in the client package, nested in the network package.<br><br>
- The user will be prompted to enter the IP address and port number of the server (default is localhost and port 44444).<br><br>
- The user will then be prompted to enter a username. The username should be unique on the server.<br><br>
- After a successful connection, the user will be prompted with the main menu explaining the available commands and how to use them (See Commands).<br><br>

### Playing a Game
- After a successful connection, the user can play a game by joining the queue (see Commands). <br>
- Once there are 2 players on the server in a queue, the server will match them and a new game will begin.<br>
- Make the moves with the appropriate command. The game status will be updated after each move. <br>
- If you want to enable the AI to play the games for you, use the command `ai strategy` before joining a game (See Commands).

### Commands
After a successful connection, the users can use the following commands via the TUI:
<pre>
- list                           - List of connected users in the server separated by ~.
- queue                          - Adds you to the queue to play a game. If used again, removes you from the queue. 
- move A/a                       - Make your move in the game: A/a - letter where you want to place your mark.
                                   You will see all available moves represented as letters on the board. Make a move with one of the letters.
                                   You can use both lowercase and uppercase letters to make your move.
-ai strategy                     - Enter before joining a game to enable an AI that will automatically play games for you. Enter simply 'ai' to disable it.
                                   The strategy parameter can be 0, 1, 2, 3, 4:
                                   0: NaiveStrategy - This strategy chooses a random legal move.
                                   1: LimitingStrategy - This strategy is based on the premise of giving your opponent the least possible moves, since this may cause you to get more moves.
                                      Statistic: 66% win against Naive
                                   2: FieldValueStrategy - This strategy is based on the positional values from the publication https://repub.eur.nl/pub/7142.
                                      It chooses the field based on the highest value possible, if there are multiple of the same value, it chooses a random one.
                                      Statistic: 80% win against Naive
                                   3: StackStrategy - A combination of strategies 2 and 1.
                                      Stackable strategies are strategies that can be used one after the other. This is created by a StackStrategy.
                                      The premise of this stacking is that a strategy can have multiple "best fields" For example, a strategy limiting the
                                      other players' turns can have 2 moves which causes the next player to have 0 moves. These 2 moves are then passed on
                                      to the next Stackable strategy in the stack, which then filters based on its strategy.
                                   4: MiniMaxStrategy - The minimax strategy looks at n turns in the future to determine what is the best move, assuming the opponent has perfect play.
                                      A second parameter n is optional to determine the depth, else it will use depth 5. This affects the calculation time.
-hint                             - Gives you a legal move suggested by the FieldValueStrategy AI.
                                    Can be used only when it's your turn in a game.
-help                             - Print the help menu.
-exit                             - Exit the program.
</pre>

## Documentation

The project's Javadoc can be found in the `documentation` folder and read in a web browser.

