package strategies;

import game.Board;
import game.Mark;

import java.util.*;

/**
 * The stack strategy is an implementation of other Stackable strategies. Stackable strategies are strategies
 * that can be used one after the other. This is created by a StackStrategy.
 * The premise of this stacking is that a strategy can have multiple "best fields". For example, a strategy limiting the
 * other players turns can have 2 move which cause the next player to have 0 moves. These 2 moves are then passed on
 * to the next Stackable strategy in the stack, which then filters based on its strategy.
 */
public class StackStrategy implements Strategy {

    public final String NAME = "Strategy stack: ";

    private final Stack<StackableStrategy> strategyStack = new Stack<>();
    private final ArrayList<StackableStrategy> strategyList = new ArrayList<>();

    /**
     * A constructor to add all strategies to the stack. The smaller the index, the bigger the priority of the strategy.
     * @param strategies the strategies to stack.
     */
    public StackStrategy(ArrayList<StackableStrategy> strategies) {
        strategyList.addAll(strategies);
        resetStack();
    }

    @Override
    public String getName() {
        StringBuilder result = new StringBuilder();
        for (StackableStrategy strategy : strategyList) {
            result.append(strategy.getName()).append(" \u2192 ");
        }
        result.append("Random");
        return result.toString();
    }

    @Override
    public int determineMove(Board board, Mark mark) {
        Set<Integer> set = board.possibleMoves(mark);
        StackableStrategy strategy;
        while (!strategyStack.empty()) {
            strategy = strategyStack.pop();
            set = strategy.determineMoveSet(board, mark, set);
            if (set.size() == 1) {
                break;
            }
        }
        resetStack();
        return getRandomElement(set);
    }

    /**
     * Gets a random element from a collection
     * @param coll the collection to choose
     * @return a random element
     */
    public static <T> T getRandomElement(Collection<T> coll) {
        int num = (int) (Math.random() * coll.size());
        for(T t: coll) if (--num < 0) return t;
        throw new AssertionError();
    }

    /**
     * Resets the stack, so it can be used again later.
     */
    public void resetStack() {
        Collections.reverse(strategyList);
        for (StackableStrategy strategy : strategyList) {
            strategyStack.push(strategy);
        }
        Collections.reverse(strategyList);
    }
}
