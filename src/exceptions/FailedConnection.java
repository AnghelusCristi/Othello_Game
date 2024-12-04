package exceptions;

/**
 * Thrown to indicate that the client failed to connect to the server.
 */
public class FailedConnection extends Exception {
    /**
     * Constructs a FailedConnection exception.
     */
    public FailedConnection() {
        super("No connection could be established: Invalid port or address. Please try again.");
    }
}
