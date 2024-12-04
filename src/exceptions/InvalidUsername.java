package exceptions;

/**
 * Thrown to indicate that the username of the user is not valid.
 */
public class InvalidUsername extends Exception {
    /**
     * Constructs a InvalidUsername exception.
     * @param errorMessage the detail message of the exception.
     */
    public InvalidUsername(String errorMessage) {
        super(errorMessage);
    }
}
