package strategies;

import game.Board;
import game.Mark;
import java.util.Random;
import java.util.Set;

/**
 * Implementation of a naive strategy, which chooses a random legal move.
 */
public class NaiveStrategy implements Strategy {

    @Override
    public String getName() {
        return "Naive AI";
    }

    @Override
    public int determineMove(Board board, Mark mark) {
        Set<Integer> choices = board.possibleMoves(mark);
        Integer[] arrayNumbers = choices.toArray(new Integer[0]); //generate a random number
        if (!choices.isEmpty()) {
            int randomNumber = new Random().nextInt(choices.size());
            return arrayNumbers[randomNumber];
        } else {
            return -1;
        }
    }
}
