package strategies;

import exceptions.IllegalMoveException;
import exceptions.InvalidFieldException;
import game.Board;
import game.Mark;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The minimax strategy looks at n turns in the future to determine what is the best move, assuming the opponent has perfect play.
 * More on <a href="https://en.wikipedia.org/wiki/Minimax">...</a>.
 */
public class MiniMaxStrategy implements Strategy {
    /**
     * A node in the minimax tree. This is used for calculating the best value based on the MiniMax algorithm.
     */
    public static class Node {
        int index;
        int score;

        public Node(int score) {
            this.score = score;
        }
    }

    private Mark myMark;
    private final int DEPTH;

    private final ArrayList<Integer> values;

    /**
     * The constructor of a miniMax strategy. This adds all the values to the fields on which the
     * heuristic evaluation is calculated. It also set the depth.
     * @param depth the depth to calculate to
     */
    public MiniMaxStrategy(int depth) {
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
        this.DEPTH = depth;
    }

    @Override
    public String getName() {
        return "MiniMax AI (depth " + DEPTH + ")";
    }

    @Override
    public int determineMove(Board board, Mark mark) {
        myMark = mark;
        Node node = minimax(board, DEPTH, mark, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return node.index;
    }

    /**
     * A recursive function which does all the estimation based on the MiniMax strategy.
     * For more information read <a href="https://en.wikipedia.org/wiki/Minimax">...</a>
     * @param board the board to calculate
     * @param depth the depth in which it is currently
     * @param mark the mark to calculate for now (MIN/MAX)
     * @param alpha the alpha value for alpha beta pruning
     * @param beta the beta value for alpha beta pruning
     * @return a node which holds the score and index of that node or leaf.
     */
    public Node minimax(Board board, int depth, Mark mark, int alpha, int beta) {
        if (depth == 0 || board.gameOver()) {
            return new Node(eval(board));
        }
        if (!board.hasMoves(mark)) {
            mark = mark.other();
        }

        if (mark == myMark) {
            int maxEval = Integer.MIN_VALUE;
            Node bestMove = null;

            for (Integer move: board.possibleMoves(mark)) {
                Board copy = board.deepCopy();
                try {
                    copy.setField(move, mark);
                } catch (InvalidFieldException | IllegalMoveException e) {
                    e.printStackTrace();
                }
                Node node = minimax(copy, depth - 1, mark.other(), alpha, beta);
                node.index = move;
                if (maxEval <= node.score) {
                    maxEval = node.score;
                    bestMove = node;
                }
                alpha = Math.max(alpha, node.score);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestMove;
        } else {
            int minEval = Integer.MAX_VALUE;
            Node bestMove = null;
            for (Integer move: board.possibleMoves(mark)) {
                Board copy = board.deepCopy();
                try {
                    copy.setField(move, mark);
                } catch (InvalidFieldException | IllegalMoveException e) {
                    e.printStackTrace();
                }
                Node node = minimax(copy, depth - 1, mark.other(), alpha, beta);
                node.index = move;
                if (minEval >= node.score) {
                    minEval = node.score;
                    bestMove = node;
                }
                beta = Math.min(beta, node.score);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestMove;
        }
    }

    /**
     * The evaluation function to retrieve a value based on the configuration of the board.
     * Negative values favor the opponent, positive values favor the AI.
     * The function is a summation of all field values of the players.
     * @param board the board to calculate
     * @return a value based on the board.
     */
    private int eval(Board board) {
        if (board.isWinner(myMark)) {
            return Integer.MAX_VALUE;
        }
        if (board.isWinner(myMark.other())) {
            return Integer.MIN_VALUE;
        }
        if (board.isDraw()) {
            return 0;
        }
        int val = 0;
        for (int i = 0; i < 64; i++) {
            try {
                val += (board.getField(i) == myMark) ? values.get(i) : 0;
                val -= (board.getField(i) == myMark.other()) ? values.get(i) : 0;
            } catch (InvalidFieldException e) {
                e.printStackTrace();
            }
        }
        return val;
    }
}
