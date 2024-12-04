package strategies;

import game.Board;
import game.Mark;

import java.util.HashSet;
import java.util.Set;

/**
 * Stackable strategies are strategies that can be used one after the other. This is created by a StackStrategy.
 * The premise of this stacking is that a strategy can have multiple "best fields" For example, a strategy limiting the
 * other players turns can have 2 move which cause the next player to have 0 moves. These 2 moves are then passed on
 * to the next Stackable strategy in the stack, which then filters based on its strategy.
 */
public interface StackableStrategy extends Strategy {

    /**
     * Calculates all the moves that are the best following this strategy given some other moves. Multiple moves can
     * have the same heuristic value.
     * @param board the board of the game
     * @param mark the mark to calculate for
     * @return the stripped hashset
     */
    Set<Integer> determineMoveSet(Board board, Mark mark, Set<Integer> filterSet);
}
