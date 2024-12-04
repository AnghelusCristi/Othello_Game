package game;

/**
 * Represents a mark that can be on the board.
 */
public enum Mark {
    WHITE, BLACK, EMPTY;

    /**
     * Returns the other mark.
     * @return the other mark is this mark is not EMPTY or EMPTY
     */
    //@ ensures this == WHITE ==> \result == BLACK && this == BLACK ==> \result == WHITE;
    public Mark other() {
        if (this == WHITE) {
            return BLACK;
        } else if (this == BLACK) {
            return WHITE;
        } else {
            return EMPTY;
        }
    }

    /**
     * Returns a random mark between BLACK and WHITE, with equal chance.
     * @return either Mark.White or Mark.Black, with a 50% chance each.
     */
    public static Mark random() {
        if (Math.random() > 0.5) {
            return Mark.WHITE;
        }
        return Mark.BLACK;
    }
}
