package strategies;

import game.Board;
import game.Mark;

/**
 * Interface that represents a strategy that can be used to determine a move in the Othello game.
 */
public interface Strategy {

    /**
     * Returns the name of the strategy.
     * @return the name
     */
    String getName();

    /**
     * Calculates the move to make and returns this index.
     * If it doesn't have available moves returns -1.
     * @param board the board of the game
     * @param mark the mark to calculate for
     * @return the index of the move
     */
    int determineMove(Board board, Mark mark);
}
