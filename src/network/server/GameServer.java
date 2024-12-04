package network.server;

import exceptions.IllegalMoveException;
import exceptions.InvalidFieldException;
import game.Game;
import game.OthelloGame;
import game.Mark;
import game.Player;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * A representation of the server for the Othello game.
 */
public class GameServer implements Server, Runnable {
    private final int port;
    private ServerSocket ss;
    private List<ClientHandler> clients; // List of clients connected to the server.
    private List<ClientHandler> queue; // List of clients that are in the queue.

    private List<ClientHandler> logged; // List of clients that are logged in.
    private Thread s1;
    private Map<Game, List<ClientHandler>> players; //Map of games and the players for each game.


    /**
     * Constructor for the GameServer class.
     * Takes as argument the port number at which the server will start.
     * @param port the port where the server will start.
     */
    public GameServer(int port) {
        this.port = port;
    }

    @Override
    public void start() {
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Could not start the server at port: " + port +
                    "\n The port is probably already in use.");
            System.exit(-1);
        }

        s1 = new Thread(this);
        s1.start();
    }

    @Override
    public void stop() {
        try {
            ss.close();
        } catch (IOException e) {
            System.out.println("Couldn't close the ServerSocket!!!");
        }

        try {
            s1.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        }

    }

    /**
     * Returns the map with the games and players.
     * @return map with the games and players.
     */
    public synchronized Map<Game, List<ClientHandler>> getPlayers() {
        return players;
    }

    /**
     * Add a client to the list of total clients connected to the server.
     * @param ch the ClientHandler to be added to the list.
     */
    public synchronized void addClient(ClientHandler ch) {
        clients.add(ch);
    }

    /**
     * Add a client to the list of the clients that are in the queue.
     * @param ch the ClientHandler to be added to the list.
     */
    public synchronized void addQueue(ClientHandler ch) {
        queue.add(ch);
        System.out.println(ch.getUsername() + " is currently queueing. Queue-size:" + queue.size());
    }

    /**
     * Add a client to the list of the clients that are logged in.
     * @param ch the ClientHandler to be added to the list.
     */
    public synchronized void addLogged(ClientHandler ch) {
        logged.add(ch);
    }

    /**
     * Removes a client from the list of total clients connected to the server.
     * Also removes the client from the logged list.
     * @param ch the ClientHandler to be removed from the list.
     */
    public synchronized void removeClient(ClientHandler ch) {
        removeLogin(ch);
        clients.remove(ch);
    }

    /**
     * Removes a client from the list of the clients that are in the queue.
     * @param ch the ClientHandler to be removed from the list.
     */
    public synchronized void removeQueue(ClientHandler ch) {
        queue.remove(ch);
    }

    /**
     * Removes a client from the list of the clients that are logged in.
     * Also removes the client from the queue list.
     * @param ch the ClientHandler to be removed from the list.
     */
    public synchronized void removeLogin(ClientHandler ch) {
        removeQueue(ch);
        logged.remove(ch);
    }

    /**
     * Returns the list of the clients that are connected to server.
     * @return the list of the clients that are connected to server.
     */
    public List<ClientHandler> getClients() {
        return clients;
    }

    /**
     * Returns the list of the clients that are in the queue.
     * @return the list of the clients that are in the queue.
     */
    public synchronized List<ClientHandler> getQueue() {
        return queue;
    }

    /**
     * Returns the list of the clients that are logged in.
     * @return the list of the clients that are logged in.
     */
    public synchronized List<ClientHandler> getLogin() {
        return logged;
    }

    /**
     * Handles the queue of the server for a ClientHandler(removes or adds it to the queue list).
     * If there are 2 or more players in the queue, it starts a new game for 2 clients in the queue.
     * @param ch the ClientHandler for which to handle the queue.
     */
    public synchronized void handleQueue(ClientHandler ch) {
        if (getQueue().contains(ch)) {
            removeQueue(ch);
        } else {
            addQueue(ch);
        }

        if (getQueue().size() >= 2) {
            List<ClientHandler> gamePlayers = new ArrayList<>();
            ClientHandler p1 = getQueue().get(0);
            ClientHandler p2 = getQueue().get(1);
            removeQueue(p1);
            removeQueue(p2);
            Player player1 = new Player(p1.getUsername(), Mark.BLACK);
            Player player2 = new Player(p2.getUsername(), Mark.WHITE);
            p1.setPlayer(player1);
            p2.setPlayer(player2);

            OthelloGame game = new OthelloGame(player1, player2);
            p1.setGame(game);
            p2.setGame(game);

            gamePlayers.add(p1);
            gamePlayers.add(p2);
            getPlayers().put(game, gamePlayers);

            System.out.println("Creating a game for users " + p1.getUsername() + " and " + p2.getUsername());

            p1.sendMessage("NEWGAME~" + p1.getUsername() + "~" + p2.getUsername());
            p2.sendMessage("NEWGAME~" + p1.getUsername() + "~" + p2.getUsername());
        }
    }

    /**
     * Returns the list with all the logged in clients in the protocol format.
     * @return the list with all the logged in clients.
     */
    public synchronized String getList() {
        StringBuilder list = new StringBuilder("LIST");
        for (ClientHandler ch : getLogin()) {
            list.append("~").append(ch.getUsername());
        }
        return list.toString();
    }

    /**
     * Prints to the standard output of this class.
     * @param message the message to be printed.
     */
    public synchronized void print(String message) {
        System.out.println(message);
    }

    /**
     * checks if the provided username is not already used in the server.
     * @param username the username to be checked.
     * @return true if the provided username is free and false otherwise.
     */
    public synchronized boolean checkFreeUsername(String username) {
        for (ClientHandler ch : getLogin()) {
            if (ch.getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Handles the move that is sent by the ClientHandler.
     * @param ch the ClientHandler which sent the move.
     * @param move the move to be handled.
     * @throws IllegalMoveException if the move is not legal
     * @throws InvalidFieldException if the field is not valid
     */
    public synchronized void makeMove(ClientHandler ch, int move)
            throws IllegalMoveException, InvalidFieldException {
        Game game = ch.getGame();
        //get the list of ClientHandlers who play this game, basically the players of the game
        List<ClientHandler> clientsPlayers = getPlayers().get(game);
        //checks whether the client is the current player
        if (game.getCurrent().equals(ch.getPlayer())) {
            //Check whether the move is possible
            if (game.getBoard().possibleMoves(ch.getPlayer().getMark()).contains(move)) {
                game.makeMove(move);
                for (ClientHandler pl : clientsPlayers) {
                    pl.sendMessage("MOVE~" + move);
                }
                //If the move is 64, then check if the player doesn't have any possible moves
            } else if (move == 64 &&
                    game.getBoard().possibleMoves(ch.getPlayer().getMark()).isEmpty()) {
                game.pass(); //pass the turn to the other player
                for (ClientHandler pl : clientsPlayers) {
                    pl.sendMessage("MOVE~" + move);
                }
            } else {
                ch.sendMessage("ERROR~Invalid move");
            }
        } else {
            ch.sendMessage("ERROR~Not your turn");
        }
        //check if the game is over
        if (game.getBoard().gameOver()) {
            //check who is the winner
            if (game.getBoard().isWinner(game.getPlayers()[0].getMark())) {
                for (ClientHandler pl : clientsPlayers) {
                    pl.setInGame(false);
                    pl.sendMessage("GAMEOVER~" + "VICTORY~" + game.getPlayers()[0].getUsername());
                }
                players.remove(game);
            } else if (game.getBoard().isWinner(game.getPlayers()[1].getMark())) {
                for (ClientHandler pl : clientsPlayers) {
                    pl.setInGame(false);
                    pl.sendMessage("GAMEOVER~" + "VICTORY~" + game.getPlayers()[1].getUsername());
                }
                players.remove(game);
            } else if (game.getBoard().isDraw()) {
                for (ClientHandler pl : clientsPlayers) {
                    pl.setInGame(false);
                    pl.sendMessage("GAMEOVER~DRAW");
                }
                players.remove(game);
            }
        }
    }

    /**
     * The run method of the Runnable class GameServer.
     * Initializes the fields and starts a loop to accepts new connections.
     * Creates a new ClientHandler for each connection and starts a new thread with it.
     */
    @Override
    public void run() {
        this.clients = new ArrayList<>();
        this.queue = new ArrayList<>();
        this.logged = new ArrayList<>();
        this.players = new HashMap<>();

        boolean run = true;

        while (run) {
            try {
                Socket socket = ss.accept();
                ClientHandler ch = new ClientHandler(socket, this);
                addClient(ch);
                new Thread(ch).start();
            } catch (IOException e) {
                run = false;
            }
        }
    }
}
