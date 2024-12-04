package network.server;

/**
 * A server that runs in a separate thread.
 * The start method is only called once and the stop method is only called once after start.
 */
public interface Server {
    /**
     * Starts the server. The server should run in a separate thread,
     * so this method should return after starting this thread.
     */
    void start();

    /**
     * Stops the server by closing the ServerSocket.
     * This method returns after the server thread has actually stopped.
     */
    void stop();

}
