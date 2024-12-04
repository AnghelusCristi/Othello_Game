package game;

import exceptions.IllegalMoveException;
import exceptions.InvalidFieldException;

import java.util.*;

/**
 * Represents the Othello (Reversi) Game.
 */
public class OthelloGame implements Game {
    public static final int NUMBER_PLAYERS = 2;
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final Board board;
    private final Player[] players;
    private Player current;
    private ArrayList<Integer> charFields;

    /**
     * Constructs a game of Othello with initial setup:
     * current player is black and the initial board.
     * @param p1 the first player of the game.
     * @param p2 the second player of the game.
     */
    public OthelloGame(Player p1, Player p2) {
        this(p1, p2, new DefaultConfiguration());
    }

    /**
     * Constructs a game of Othello with a configuration supplied to the configuration. This can be with any starting config.
     * @param p1 the first player of the game.
     * @param p2 the second player of the game.
     * @param configuration the board configuration
     */
    public OthelloGame(Player p1, Player p2, BoardConfiguration configuration) {
        board = new OthelloBoard(configuration);
        players = new Player[NUMBER_PLAYERS];
        players[0] = p1;
        players[1] = p2;
        if (p1.getMark() == Mark.BLACK) {
            current = p1;
        } else {
            current = p2;
        }
    }

    @Override
    public void pass() {
        if (current == players[0]) {
            current = players[1];
        } else {
            current = players[0];
        }
    }

    /**
     * Converts the move from the letter representation to the integer index.
     * @param input the letter we want to convert.
     * @return the integer representation of the move.
     */
    public int convertMove(String input) {
        String regex = "[a-zA-Z]";
        String in = input.toUpperCase();
        if (!in.matches(regex)) {
            return -1;
        }
        try {
            return charFields.get(ALPHABET.indexOf(in));
        } catch (IndexOutOfBoundsException e) {
            return -1;
        }
    }

    /**
     * Converts the move from the integer representation to the letter index.
     * @param input the integer move to be converted.
     * @return move represented as a letter.
     */
    public char convertMove(int input) {
        if (charFields.contains(input)) {
            int index = charFields.indexOf(input);
            return ALPHABET.charAt(index);
        }
        return '-';
    }

    /**
     * Returns a representation of the black mark as a black circle
     * and white mark as a white circle.
     * @param m the mark to be represented.
     * @return the representation of the black/white mark as circles.
     */
    /*
     IMPORTANT NOTE: The colors are reversed (the white mark returns black circle and vice versa).
     This is done because most of the IDEs and consoles are dark themed,
     so they reverse the black and white color.
     If the program is run on a light (white) background,
     the return encodings should be interchanged.
     */
    public String getMarkChar(Mark m) {
        switch (m) {
            case WHITE:
                return "\u25CF";
            case BLACK:
                return "\u25EF";
            default:
                return "-";
        }
    }

    /**
     * Returns the current representation of the game board, including: the positions of the marks,
     * the possible moves of the current player encoded as uppercase letters
     * and the scores of both players.
     * @return current representation of the game board.
     */
    @Override
    public String update() {
        this.charFields = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        result.append("\u007C ");
        Mark[] marks = board.getFields();
        Set<Integer> possibleMoves = board.possibleMoves(current.getMark());
        int count = 0;
        for (int i = 0; i < 64; i++) {
            if (marks[i] == Mark.WHITE || marks[i] == Mark.BLACK) {
                result.append(getMarkChar(marks[i]));
            }
            if (marks[i] == Mark.EMPTY) {
                if (possibleMoves.contains(i)) {
                    result.append(ALPHABET.charAt(count));
                    charFields.add(i);
                    count++;
                } else {
                    result.append(" ");
                }
            }
            if ((i + 1) % 8 == 0) {
                result.append(" \u007C");
                if (i == 15) {
                    result.append(" ".repeat(Math.max(0, 15 - players[0].getUsername().length())));
                    result.append(players[0].getUsername()).append(" (").append(getMarkChar(players[0].getMark())).append("): ");
                    result.append(board.getScore(players[0].getMark()));
                }
                if (i == 23) {
                    result.append(" ".repeat(Math.max(0, 15 - players[1].getUsername().length())));
                    result.append(players[1].getUsername()).append(" (").append(getMarkChar(players[1].getMark())).append("): ");
                    result.append(board.getScore(players[1].getMark()));
                }
                result.append("\n");
                if (i != 63) {
                    result.append("\u007C");
                }
            }
            result.append(" ");
        }
        return result.toString();
    }

    @Override
    public void makeMove(int index) throws IllegalMoveException, InvalidFieldException {
        if (index == 64 && getBoard().possibleMoves(current.getMark()).isEmpty()) {
            pass(); // if the index is 64 and don't have any other moves, pass the turn
        } else {
            board.setField(index, current.getMark());
            pass();
        }
    }

    @Override
    public Player getCurrent() {
        return current;
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public Player[] getPlayers() {
        return players;
    }
}
