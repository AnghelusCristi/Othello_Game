package exceptions;

/**
 * Indicates that a move cannot be made.
 * Because it is either already full, or not in the scope of available moves.
 */
public class IllegalMoveException extends Exception {
    /**
     * Constructs a IllegalMoveException exception.
     * @param errorMessage the detail message of the exception.
     */
    public IllegalMoveException(String errorMessage) {
        super(errorMessage);
    }
}
