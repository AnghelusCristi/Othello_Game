package network.client;

/**
 * A representation of the listener of Othello game.
 */
public interface GameListener {
    /**
     * Prints the message that is received from the client.
     * @param message the received message.
     */
    void messageReceived(String message);
}
