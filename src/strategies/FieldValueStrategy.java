package strategies;

import game.Board;
import game.Mark;
import java.util.*;


/**
 * This strategy is based on the positional values
 * from the publication <a href="https://repub.eur.nl/pub/7142">...</a>.
 * It chooses the field based on the highest value possible,
 * if there are multiple of the same value, it chooses a random one.
 * Statistic: 80% win against Naive
 */
public class FieldValueStrategy implements StackableStrategy, Strategy {

    private final ArrayList<Integer> values;

    /**
     * Constructs a Field Value strategy.
     */
    public FieldValueStrategy() {
        this.values = new ArrayList<>(Arrays.asList(
                100, -20, 10,  5,  5, 10, -20, 100,
                -20, -50, -2, -2, -2, -2, -50, -20,
                 10, -2,  -1, -1, -1, -1, -2,  10,
                  5, -2,  -1, -1, -1, -1, -2,   5,
                  5, -2,  -1, -1, -1, -1, -2,   5,
                 10, -2,  -1, -1, -1, -1, -2,  10,
                -20, -50, -2, -2, -2, -2, -50, -20,
                100, -20, 10, 5, 5, 10, -20, 100
                ));
    }

    @Override
    public String getName() {
        return "Field Value AI";
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
     * A method for testing all possible moves to create a set of the moves with the fields all equal in best value.
     * @param board the board
     * @param mark the mark to check for
     * @param filterSet the set to filter on for the best moves to make
     * @return a set of all best moves
     */
    @Override
    public Set<Integer> determineMoveSet(Board board, Mark mark, Set<Integer> filterSet) {
        HashSet<Integer> bestMoves = new HashSet<>();
        int bestValue = Integer.MIN_VALUE;
        for (int i : board.possibleMoves(mark)) {
            if (values.get(i) > bestValue) {
                bestMoves = new HashSet<>();
                bestMoves.add(i);
                bestValue = values.get(i);
                continue;
            }
            if (values.get(i) == bestValue) {
                bestMoves.add(i);
            }
        }
        return bestMoves;
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
