package network.client;

import strategies.*;
import exceptions.FailedConnection;
import exceptions.IllegalMoveException;
import exceptions.InvalidUsername;
import exceptions.InvalidFieldException;
import game.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 * Represents a Client for the Othello game.
 */
public class Client implements GameClient, Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private OthelloGame game;
    private Player clientPlayer;
    private Player opponent;
    private boolean isAI;
    private boolean isLogged;
    private boolean queue;
    private Strategy strategy;
    private List<GameListener> listeners;

    /**
     * Returns whether the client is logged into the server or not.
     * @return true if the client is logged, false otherwise.
     */
    public boolean isLogged() {
        return isLogged;
    }

    @Override
    public void connect(String address, int port) throws FailedConnection {
        try {
            socket = new Socket(address, port);
        } catch (IOException e) {
            throw new FailedConnection();
        }

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("HELLO~Othello Client");
        } catch (IOException e) {
            close();
        }
        new Thread(this).start();
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Failed to close the socket!!!");
        }
        try {
            removeGameListener(listeners.get(0));
        } catch (IndexOutOfBoundsException ignored) {

        }
    }

    @Override
    public void sendUsername(String name) throws InvalidUsername {
        if (name != null && !name.contains("~")) {
            out.println("LOGIN~" + name);
            this.username = name;
        } else {
            throw new InvalidUsername("Username is not valid!!! Contains ~ or is null.");
        }
    }

    @Override
    public void handleMessages(String message) {
        String wrongCommand = "Wrong command parameter.";
        String[] command = message.split(" ");
        switch (command[0]) {
            case "list":
                if (command.length == 1) {
                    out.println("LIST");
                } else {
                    sendToListener(wrongCommand);
                }
                break;
            case "queue":
                if (command.length == 1) {
                    if (this.game == null) {
                        out.println("QUEUE");
                        if (queue) {
                            queue = false;
                            sendToListener("You are removed from the queue.");
                        } else {
                            queue = true;
                            sendToListener("Waiting for a game...");
                        }
                    } else {
                        sendToListener("Command not available when you are already in a game.");
                    }
                } else {
                    sendToListener(wrongCommand);
                }
                break;
            case "move":
                if (command.length == 2) {
                    if (game != null) {
                        if (game.convertMove(command[1]) != -1 && command.length == 2) {
                            int move = game.convertMove(command[1]);
                            if (game.getCurrent() == opponent) {
                                sendToListener("It's not your turn. Wait for your opponent's move.");
                            } else {
                                out.println("MOVE~" + move);
                            }
                        } else {
                            sendToListener("Invalid move. Please choose a valid move.");
                        }
                    } else {
                        sendToListener("Command not available when you are not playing a game.");
                    }
                } else {
                    sendToListener(wrongCommand);
                }
                break;
            case "hint":
                if (command.length == 1) {
                    if (game != null) {
                        if (game.getCurrent() == clientPlayer) {
                            Strategy hintStrategy = new FieldValueStrategy();
                            int move = hintStrategy.determineMove(game.getBoard(), clientPlayer.getMark());
                            sendToListener("Suggested move by AI: " + game.convertMove(move));
                        } else {
                            sendToListener("It's not your turn. Wait for your opponent's move.");
                        }
                    } else {
                        sendToListener("Command not available when you are not playing a game.");
                    }
                } else {
                    sendToListener(wrongCommand);
                }
                break;
            case "ai":
                if (command.length == 2 && "01234".contains(command[1])) {
                    if (game == null && !isAI) {
                        isAI = true;
                        switch (command[1]) {
                            case "0":
                                this.strategy = new NaiveStrategy();
                                break;
                            case "1":
                                this.strategy = new LimitingStrategy();
                                break;
                            case "2":
                                this.strategy = new FieldValueStrategy();
                                break;
                            case "3":
                                StackableStrategy fieldValue = new FieldValueStrategy();
                                StackableStrategy limiting = new LimitingStrategy();
                                this.strategy = new StackStrategy(new ArrayList<>(Arrays.asList(fieldValue, limiting)));
                                break;
                            case "4":
                                this.strategy = new MiniMaxStrategy(5);
                            default:
                                sendToListener(wrongCommand);
                        }
                        sendToListener(strategy.getName() + " is turned on and will play the games for you.");
                    } else if (game != null && isAI) {
                        sendToListener("The AI is already playing a game.");
                    } else if (game != null && !isAI) {
                        sendToListener("Can't turn on the AI while you are playing a game. You can only request a hint from the AI.");
                    } else if (game == null && isAI) {
                        sendToListener(wrongCommand);
                    }
                } else if (command.length == 3 && Objects.equals(command[1], "4")) {
                    if (game == null && !isAI) {
                        isAI = true;
                        this.strategy = new MiniMaxStrategy(Integer.parseInt(command[2]));
                        sendToListener(strategy.getName() + " is turned on and will play the games for you.");
                    } else if (game != null && isAI) {
                        sendToListener("The AI is already playing a game.");
                    } else if (game != null && !isAI) {
                        sendToListener("Can't turn on the AI while you are playing a game. You can only request a hint from the AI.");
                    } else if (game == null && isAI) {
                        sendToListener(wrongCommand);
                    }
                } else if (command.length == 1) {
                    if (game == null && isAI) {
                        sendToListener(strategy.getName() + " is turned off.");
                        isAI = false;
                        strategy = null;
                    } else if (game != null && isAI) {
                        sendToListener("The AI is already playing a game.");
                    } else if (game != null && !isAI) {
                        sendToListener("Can't turn on the AI while you are playing a game. You can only request a hint from the AI.");
                    } else if (game == null && !isAI) {
                        sendToListener(wrongCommand);
                    }
                } else {
                    sendToListener(wrongCommand);
                }
                break;
            case "exit":
                if (command.length == 1) {
                    sendToListener("Exiting the program. Goodbye!!!");
                    close();
                } else {
                    sendToListener(wrongCommand);
                }
                break;
            default:
                sendToListener("Unavailable command. Check help menu.");
        }
    }

    @Override
    public void sendToListener(String message) {
        for (GameListener listener : listeners) {
            listener.messageReceived(message);
        }
    }
    @Override
    public void addGameListener(GameListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    @Override
    public void removeGameListener(GameListener listener) {
        listeners.remove(listener);
    }

    /**
     * Processes what to do when the server sends a move or a new game.
     */
    private void processMoveAI() {
        int move = strategy.determineMove(game.getBoard(), game.getCurrent().getMark());
        // the AI will send move -1 if there's no available moves.
        if (move != -1) {
            // make the move of the AI by sending the move to handleMessages.
            // as if the user sent it.
            handleMessages("move " + game.convertMove(move));
        }
    }

    /**
     * The run method of the Runnable class Client.
     * It receives messages from the server according to the protocol and acts accordingly.
     * Also deals with disconnections from the server.
     */
    @Override
    public void run() {
        String line;
        try {
            while ((line = in.readLine()) != null) {
                String[] command = line.split("~");
                switch (command[0]) {
                    case "LOGIN":
                        sendToListener("Welcome " + this.username + " to the server.\n\n");
                        isLogged = true;
                        break;
                    case "ALREADYLOGGEDIN":
                        sendToListener("The username " + this.username + " is already used. Please try another username.");
                        break;
                    case "LIST" :
                        String list = "Connected users in the server: ";
                        for (int i = 1; i < command.length; i++) {
                            if (i == 1) {
                                list += command[i];
                            } else {
                                list += "~" + command[i];
                            }
                        }
                        sendToListener(list);
                        break;
                    case "NEWGAME":
                        if (command[1].equals(this.username)) {
                            clientPlayer = new Player(command[1], Mark.BLACK);
                            opponent = new Player(command[2], Mark.WHITE);
                            game = new OthelloGame(clientPlayer, opponent);
                        } else {
                            clientPlayer = new Player(command[2], Mark.WHITE);
                            opponent = new Player(command[1], Mark.BLACK);
                            game = new OthelloGame(opponent, clientPlayer);
                        }
                        sendToListener("\nNew Game: " + command[1] + " " + game.getMarkChar(game.getPlayers()[0].getMark()) +
                                " vs " + command[2] + " " + game.getMarkChar(game.getPlayers()[1].getMark()));
                        sendToListener(game.getCurrent().getUsername() + " " + game.getMarkChar(game.getCurrent().getMark()) + " may start the game.");
                        sendToListener(game.update());
                        if (game.getCurrent() == clientPlayer) {
                            if (isAI) {
                                processMoveAI();
                            } else {
                                sendToListener("Make your move in the format: " +
                                        "move A (A - one of the available moves from the board)");
                            }
                        } else {
                            sendToListener("Waiting for your opponent's move...");
                        }
                        break;
                    case "MOVE":
                        if (Integer.parseInt(command[1]) == 64 && game.getCurrent() == opponent) {
                            sendToListener("\nPlayer " + opponent.getUsername() + " doesn't have any legal moves. His turn is passed to you.");
                        } else if (game.getCurrent().getUsername().equals(clientPlayer.getUsername())) {
                            if (isAI) {
                                sendToListener(strategy.getName() + " made a move.");
                            }
                            sendToListener("Current board:");
                        } else if (game.getCurrent().getUsername().equals(opponent.getUsername())) {
                            sendToListener("\nPlayer " + opponent.getUsername() + " made his move.");
                            sendToListener("Current board:");
                        }
                        game.makeMove(Integer.parseInt(command[1]));
                        sendToListener(game.update());
                        if (game.getCurrent() == clientPlayer) {
                            //checks if the player doesn't have possible moves, but the opponent does.
                            if (game.getBoard().possibleMoves(clientPlayer.getMark()).isEmpty() &&
                                    !game.getBoard().possibleMoves(opponent.getMark()).isEmpty()) {
                                sendToListener("You don't have any possible moves. Your turn is passed to the opponent.");
                                out.println("MOVE~64"); //automatically send to the server move 64
                            } else if (isAI) {
                                processMoveAI();
                            } else {
                                sendToListener("Make your move in the format: " +
                                        "move A (A - one of the available moves from the board)");
                            }
                        } else {
                            sendToListener("Waiting for your opponent's move...");
                        }
                        break;
                    case "GAMEOVER":
                        switch (command[1]) {
                            case "DRAW":
                                sendToListener("\nGAMEOVER: It's a draw!");
                                break;
                            case "VICTORY":
                                Mark winner = game.getBoard().getWinner();
                                Mark loser = game.getBoard().getWinner().other();
                                sendToListener("\nGAMEOVER: Player " + command[2] + " " + game.getMarkChar(winner) + " has won the game.\n" +
                                        "SCORE: " + game.getMarkChar(winner) + " " + game.getBoard().getScore(winner)
                                        + " vs " + game.getMarkChar(loser) + " " + game.getBoard().getScore(loser));
                                break;
                            case "DISCONNECT":
                                Mark win;
                                //check which mark won by disconnect
                                if (game.getPlayers()[0].getUsername().equals(command[2])) {
                                    win = game.getPlayers()[0].getMark();
                                } else {
                                    win = game.getPlayers()[1].getMark();
                                }
                                sendToListener("\nGAMEOVER: Player " + command[2] + " " + game.getMarkChar(win) + " has won the game because the opponent disconnected!");
                                break;
                            default:
                                out.println("ERROR~Wrong command received.");
                        }
                        // after game over, reset the queue and the game fields.
                        queue = false;
                        game = null;
                        break;
                    default:
                        out.println("ERROR~Wrong command received.");
                }
            }
            sendToListener("Server crash!!!.");
            close();
            System.exit(-1);
        } catch (IOException e) {
            sendToListener("Server crash!!!");
            close();
            System.exit(-1);
        } catch (IllegalMoveException | InvalidFieldException e) {
            System.out.println(e.getMessage());
        }
    }
}
