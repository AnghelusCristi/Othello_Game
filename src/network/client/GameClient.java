package network.client;

import exceptions.FailedConnection;
import exceptions.InvalidUsername;

/**
 * A representation of the client of the Othello game.
 */
public interface GameClient {
    /**
     * Connects to the server.
     * @param address the address of the server.
     * @param port the port of the server.
     * @throws FailedConnection when it fails to connect.
     */
    void connect(String address, int port) throws FailedConnection;

    /**
     * Closes the connection.
     */
    void close();

    /**
     * Sends the username to the server according to the protocol.
     * @param username the username of the user.
     * @throws InvalidUsername when the provided username is not valid.
     */
    void sendUsername(String username) throws InvalidUsername;

    /**
     * Handles the messages that are sent by the user from the listener.
     * @param message the message to be handled.
     */
    void handleMessages(String message);

    /**
     * Adds a listener to the listener list.
     * @param listener the listener to be added.
     */
    void addGameListener(GameListener listener);

    /**
     * Removes a listener from the listener list.
     * @param listener the listener to be removed.
     */
    void removeGameListener(GameListener listener);

    /**
     * Sends a message to the listeners.
     * @param message the message to be sent.
     */
    void sendToListener(String message);

}
