package exceptions;

/**
 * Thrown to indicate that a called field does not exist on the board
 * or is in some way else invalid.
 */
public class InvalidFieldException extends Exception {
    /**
     * Constructs a InvalidFieldException exception.
     * @param errorMessage the detail message of the exception.
     */
    public InvalidFieldException(String errorMessage) {
        super(errorMessage);
    }
}
