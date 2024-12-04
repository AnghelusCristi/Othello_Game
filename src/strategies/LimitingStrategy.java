package strategies;

import exceptions.IllegalMoveException;
import exceptions.InvalidFieldException;
import game.Board;
import game.Mark;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This strategy is based on the premise of giving your opponent the least possible moves,
 * since this may cause you to get more moves.
 * Statistic: 66% win against Naive
 */
public class LimitingStrategy implements StackableStrategy, Strategy {

    @Override
    public String getName() {
        return "Limiting AI";
    }

    /**
     * Calculates the move to make and returns this index. It filters based on the set of all possible moves.
     * Returns -1 if there are no possible moves
     * @param board the board of the game
     * @param mark the mark to calculate for
     * @return the index of the move
     */
    @Override
    public int determineMove(Board board, Mark mark) {
        Set<Integer> possibleMoves =  board.possibleMoves(mark);
        if (possibleMoves.isEmpty()) {
            return -1;
        }
        Set<Integer> moves = determineMoveSet(board, mark, possibleMoves);
        return getRandomElement(moves);

    }

    /**
     * A method for testing all possible moves to create a set of the moves
     * where the opponent has the least possible moves.
     * @param board the board
     * @param mark the mark to check for
     * @param filterSet the set to filter on for the least possible moves
     * @return a set of all best moves
     */
    @Override
    public Set<Integer> determineMoveSet(Board board, Mark mark, Set<Integer> filterSet) {
        Set<Integer> leastMoves = new HashSet<>();
        int amount = 32;
        // Choose the move to make so that the opponent has the least possibilities
        for (Integer move : filterSet) {
            Board copy = board.deepCopy();
            try {
                copy.setField(move, mark);
                if (copy.possibleMoves(mark.other()).size() < amount) {
                    leastMoves = new HashSet<>();
                    leastMoves.add(move);
                    amount = copy.possibleMoves(mark.other()).size();
                }
                if (copy.possibleMoves(mark.other()).size() == amount) {
                    leastMoves.add(move);
                }
            } catch (InvalidFieldException | IllegalMoveException e) {
                throw new RuntimeException(e);
            }

        }
        return leastMoves;
    }

    /**
     * Gets a random element from a collection.
     * @param coll the collection to choose
     * @return a random element
     */
    public static <T> T getRandomElement(Collection<T> coll) {
        int num = (int) (Math.random() * coll.size());
        for (T t: coll) {
            if (--num < 0) {
                return t;
            }
        }
        throw new AssertionError();
    }


}
