package network.server;

import exceptions.IllegalMoveException;
import exceptions.InvalidFieldException;
import game.Game;
import game.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Represents a Client Handler for the server of the Othello game.
 * Handles the connection for a client from the server side.
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final GameServer server;
    private final BufferedReader in;
    private final PrintWriter out;
    private String username;
    private Game game;
    private boolean isLogged;
    private boolean isInGame;
    private boolean hello;
    private Player player;
    private String clientDescription;

    /**
     * Constructs a ClientHandler with the given socket and server.
     * @param socket the socket of the connection.
     * @param server the server to which it's connected.
     * @throws IOException if an I/O error occurs when creating the input/output streams.
     */
    public ClientHandler(Socket socket, GameServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Sets the game of the client.
     * @param game the game to be set.
     */
    public void setGame(Game game) {
        synchronized (server) {
            this.game = game;
            this.isInGame = true;
        }
    }

    /**
     * Closes the ClientHandler connection.
     * Removes the client from the server list and closes the socket.
     */
    public void close() {
        synchronized (server) {
            try {
                server.removeClient(this);
                socket.close();
                //print in the server which client disconnected.
                server.print("Client with username " + getUsername() + " is disconnected.");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Sets the isInGame field.
     * @param inGame the value to be set.
     */
    public void setInGame(boolean inGame) {
        synchronized (server) {
            isInGame = inGame;
        }
    }

    /**
     * Returns the game of the ClientHandler.
     * @return the game of the ClientHandler.
     */
    public Game getGame() {
        synchronized (server) {
            return game;
        }
    }

    /**
     * Returns the username of the client.
     * @return the username of the client.
     */
    public String getUsername() {
        synchronized (server) {
            return username;
        }
    }

    /**
     * Sets the player of the ClientHandler.
     * @param player the player to be set.
     */
    public void setPlayer(Player player) {
        synchronized (server) {
            this.player = player;
        }
    }

    /**
     * Returns the player of the ClientHandler.
     * @return the player of the ClientHandler.
     */
    public Player getPlayer() {
        synchronized (server) {
            return player;
        }
    }

    /**
     * Sends a message to the client using the output stream of the socket.
     * @param message the message to be sent.
     */
    public void sendMessage(String message) {
        synchronized (server) {
            out.println(message);
        }
    }

    /**
     * Handles the login command sent by the client.
     * @param name the username provided by the client.
     */
    private void login(String name) {
        synchronized (server) {
            if (server.checkFreeUsername(name)) {
                this.username = name;
                isLogged = true;
                server.addLogged(this);
                sendMessage("LOGIN");
                server.print("Client with username " + name + " logged in.");
            } else {
                sendMessage("ALREADYLOGGEDIN");
            }
        }
    }

    /**
     * Checks whether the provided string is an integer.
     * @param move the string to be checked.
     * @return true if the string is an int and false otherwise.
     */
    private boolean isValid(String move) {
        synchronized (server) {
            try {
                int i = Integer.parseInt(move);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
    }

    /**
     * Handles the disconnection of the client while being in a game.
     * Sends a GAMEOVER message to the other player.
     */
    public void disconnect() {
        List<ClientHandler> clientsPlayers = server.getPlayers().get(game);
        //check if there is a game and which player disconnected
        if (clientsPlayers != null && (!server.getClients().contains(clientsPlayers.get(0)) ||
                !server.getClients().contains(clientsPlayers.get(1)))) {
            if (!server.getClients().contains(clientsPlayers.get(0))) {
                clientsPlayers.get(1).sendMessage("GAMEOVER~DISCONNECT~"
                        + clientsPlayers.get(1).getUsername());
                clientsPlayers.get(1).setInGame(false);
                server.getPlayers().remove(game);
            } else {
                clientsPlayers.get(0).sendMessage("GAMEOVER~DISCONNECT~"
                        + clientsPlayers.get(0).getUsername());
                clientsPlayers.get(0).setInGame(false);
                server.getPlayers().remove(game);
            }
        }
    }

    /**
     * The run method of the Runnable class ClientHandler.
     * It receives messages from the client according to the protocol and handles them accordingly.
     * Also deals with disconnections from the client.
     */
    @Override
    public void run() {
        String line;
        try {
            while ((line = in.readLine()) != null) {
                synchronized (server) {
                    String[] command = line.split("~");
                    switch (command[0]) {
                        case "HELLO":
                            if (hello) {
                                sendMessage("ERROR~Hello handshake was already done.");
                            } else if (command.length != 2) {
                                sendMessage("ERROR~Wrong arguments for HELLO command.");
                            } else {
                                //print to the server which client has connected.
                                server.print("New Client: " + command[1] + " connected.");
                                this.clientDescription = command[1];
                                sendMessage("HELLO~Server is ready.");
                                this.hello = true;
                            }
                            break;
                        case "LOGIN":
                            if (command.length != 2) {
                                sendMessage("ERROR~Wrong arguments for LOGIN command.");
                            } else if (isLogged) {
                                sendMessage("ERROR~Client already logged in.");
                            } else if (!hello) {
                                sendMessage("ERROR~Hello handshake not completed.");
                            } else {
                                login(command[1]);
                            }
                            break;
                        case "LIST":
                            if (!isLogged) {
                                sendMessage("ERROR~Client not logged in yet.");
                            } else if (command.length != 1) {
                                sendMessage("ERROR~Wrong arguments for LIST command.");
                            } else {
                                sendMessage(server.getList());
                            }
                            break;
                        case "QUEUE":
                            if (command.length != 1) {
                                sendMessage("ERROR~Wrong arguments for QUEUE command.");
                            } else if (!isLogged) {
                                sendMessage("ERROR~Client not logged in yet.");
                            } else {
                                server.handleQueue(this);
                            }
                            break;
                        case "MOVE":
                            if (command.length != 2) {
                                sendMessage("ERROR~Wrong arguments for MOVE command.");
                            } else if (!isInGame) {
                                sendMessage("ERROR~Client not in a game.");
                            } else if (!isValid(command[1])) {
                                sendMessage("ERROR~Wrong argument for MOVE: Not integer.");
                            } else {
                                server.makeMove(this, Integer.parseInt(command[1]));
                            }
                            break;
                        default:
                            sendMessage("ERROR~Wrong command.");
                    }
                }
            }
            close();
            disconnect();
        } catch (IOException e) {
            close();
            disconnect();
        } catch (IllegalMoveException | InvalidFieldException e) {
            System.out.println(e.getMessage());
        }
    }
}
