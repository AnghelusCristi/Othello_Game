package exceptions;

/**
 * Thrown to indicate that a player tries to claim
 * a field that has already been claimed by a player.
 */
public class FieldNotEmptyException extends IllegalMoveException {
    /**
     * Constructs a FieldNotEmptyException exception.
     * @param errorMessage the detail message of the exception.
     */
    public FieldNotEmptyException(String errorMessage) {
        super(errorMessage);
    }
}
