package game;

import exceptions.FieldNotEmptyException;
import exceptions.IllegalMoveException;
import exceptions.InvalidFieldException;

import java.util.*;

/**
 * This class is the implementation of the Board.
 * Specifically the board used for clientside games and calculations.
 */
public class OthelloBoard implements Board {
    /**
     * The interface direction is an interface for classes to adhere to.
     * These directions are used for calculating the possible moves and flippable moves.
     * Possible directions are N, NE, E, SE, S, SW, W and NE.
     * These are specified in the getDirections() method.
     */
    public interface Direction {
        /**
         * Calculates the next field on the board in the direction which the class is made from.
         * @param row the current row
         * @param column the current column
         * @return the index of the field which is the next field
         *         when searching in this direction from field (row, col)
         * @throws InvalidFieldException Whenever it encounters an edge, or other invalid field
         */
        int calculateNext(int row, int column) throws InvalidFieldException;
    }

    public static final int DIM = 8;
    private final Mark[] fields;

    /**
     * The constructor creates a list of length 36 of all the fields on the board.
     * All fields are set to Mark.EMPTY. Then sets some fields to the starting setup.
     */
    public OthelloBoard() {
        this(new DefaultConfiguration());
    }

    /**
     * To create a new Board for Othello. It creates a board for an 8 by 8 game and it fills the board.
     * Then it calls setup to set up the board configuration.
     * @param configuration the configuration which the game starts at
     */
    public OthelloBoard(BoardConfiguration configuration) {
        fields = new Mark[DIM * DIM];
        Arrays.fill(fields, Mark.EMPTY);
        setupConfig(configuration);
    }

    @Override
    public Board deepCopy() {
        OthelloBoard board = new OthelloBoard();
        for (int field = 0; field < DIM * DIM; field++) {
            board.fields[field] = fields[field];
        }
        return board;
    }

    @Override
    public void setupConfig(BoardConfiguration config) {
        for (Integer field: config.getConfig().get(Mark.WHITE)) {
            fields[field] = Mark.WHITE;
        }
        for (Integer field: config.getConfig().get(Mark.BLACK)) {
            fields[field] = Mark.BLACK;
        }
    }

    @Override
    public int index(int row, int col) throws InvalidFieldException {
        if (row >= DIM || row < 0) {
            throw new InvalidFieldException("Row number " + row + " does not exist.");
        }
        if (col >= DIM || col < 0) {
            throw new InvalidFieldException("Column number " + col + " does not exist.");
        }
        return col + DIM * row;
    }

    @Override
    public int row(int index) {
        return (index - column(index)) / 8;
    }

    @Override
    public int column(int index) {
        return index % 8;
    }

    @Override
    public boolean isField(int index) {
        return 0 > index || index >= DIM * DIM;
    }

    @Override
    public Mark getField(int i) throws InvalidFieldException {
        if (isField(i)) {
            throw new InvalidFieldException("The index " + i +
                    " does not appear in the scope of an " + DIM + " * " + DIM + " board.");
        }
        return fields[i];
    }

    @Override
    public void setField(int i, Mark m) throws InvalidFieldException, IllegalMoveException {
        if (isField(i)) {
            throw new InvalidFieldException("The index " + i +
                    " does not appear in the scope of an " + DIM + " * " + DIM + " board.");
        }
        if (!possibleMoves(m).contains(i)) {
            throw new IllegalMoveException("The move you try to make is not possible. " +
                    "You must flip stones.\nPossible moves are: " + possibleMoves(m));
        }
        if (fields[i] == Mark.EMPTY) {
            fields[i] = m;
        } else {
            throw new FieldNotEmptyException("The requested field is already occupied by mark: "
                    + fields[i]);
        }
        HashSet<Integer> flippable = calculateFlips(i, m);
        this.flipFields(flippable);
    }

    @Override
    public HashSet<Integer> calculateFlips(int newMove, Mark mark) {
        HashSet<Integer> result = new HashSet<>();
        ArrayList<Direction> directions = getDirections();

        for (Direction direction : directions) {
            int pointer;
            int rowPointer = row(newMove);
            int columnPointer = column(newMove);
            Mark markPointer;
            HashSet<Integer> possibleFields = new HashSet<>();
            while (true) {
                try {
                    pointer = direction.calculateNext(rowPointer, columnPointer);
                    rowPointer = row(pointer);
                    columnPointer = column(pointer);
                    markPointer = getField(pointer);
                } catch (InvalidFieldException e) {
                    break;
                }
                if (markPointer == Mark.EMPTY) {
                    break;
                }
                if (markPointer == mark.other()) {
                    possibleFields.add(pointer);
                }
                if (markPointer == mark) {
                    result.addAll(possibleFields);
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public Set<Integer> possibleMoves(int index) {
        Mark mark = fields[index];
        Set<Integer> result = new HashSet<>();
        ArrayList<Direction> directions = getDirections();

        for (Direction direction : directions) {
            int pointer;
            int rowPointer = row(index);
            int columnPointer = column(index);
            Mark markPointer;
            HashSet<Integer> possibleFlips = new HashSet<>();
            while (true) {
                try {
                    pointer = direction.calculateNext(rowPointer, columnPointer);
                    rowPointer = row(pointer);
                    columnPointer = column(pointer);
                    markPointer = getField(pointer);
                } catch (InvalidFieldException e) {
                    break;
                }
                if (markPointer == Mark.EMPTY) {
                    if (!possibleFlips.isEmpty()) {
                        result.add(pointer);
                    }
                    break;
                }
                if (markPointer == mark.other()) {
                    possibleFlips.add(pointer);
                }
                if (markPointer == mark) {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * A method to construct the eight different directions to search in.
     * The only possible directions are N, NE, E, SE, S, SW, W and NE.
     * @return a list of all directions
     */
    private ArrayList<Direction> getDirections() {
        ArrayList<Direction> directions = new ArrayList<>();

        // North
        directions.add((row, column) -> index(row - 1, column));
        // NorthEast
        directions.add((row, column) -> index(row - 1, column + 1));
        // East
        directions.add((row, column) -> index(row, column + 1));
        // SouthEast
        directions.add((row, column) -> index(row + 1, column + 1));
        // South
        directions.add((row, column) -> index(row + 1, column));
        // SouthWest
        directions.add((row, column) -> index(row + 1, column - 1));
        // West
        directions.add((row, column) -> index(row, column - 1));
        // NorthWest
        directions.add((row, column) -> index(row - 1, column - 1));

        return directions;
    }

    @Override
    public Set<Integer> possibleMoves(Mark mark) {
        Set<Integer> result = new HashSet<>();
        Set<Integer> markFields = getFields(mark);
        for (Integer field : markFields) {
            result.addAll(possibleMoves(field));
        }
        return result;
    }

    @Override
    public boolean hasMoves(Mark mark) {
        return !possibleMoves(mark).isEmpty();
    }

    @Override
    public void flipField(int index) throws InvalidFieldException {
        if (isField(index)) {
            throw new InvalidFieldException("The index " + index +
                    " does not appear in the scope of an " + DIM + " * " + DIM + " board.");
        }
        if (getField(index) == Mark.EMPTY) {
            throw new InvalidFieldException("The field at index " + index +
                    " cannot flip, it is empty.");
        }
        fields[index] = getField(index).other();
    }


    @Override
    public void flipFields(HashSet<Integer> fieldsFlip) {
        for (Integer field : fieldsFlip) {
            try {
                this.flipField(field);
            } catch (InvalidFieldException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isFull() {
        for (Mark field : fields) {
            if (field == Mark.EMPTY) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasStaled() {
        return !hasMoves(Mark.BLACK) && !hasMoves(Mark.WHITE);
    }

    @Override
    public boolean gameOver() {
        return this.isFull() || this.hasStaled();
    }

    @Override
    public boolean isWinner(Mark m) {
        if (!this.gameOver()) {
            return false;
        }
        if (m == Mark.EMPTY) {
            return false;
        }
        int markCount = 0;
        int otherCount = 0;
        for (Mark field : fields) {
            if (field == m) {
                markCount++;
            }
            if (field == m.other()) {
                otherCount++;
            }
        }
        return markCount > otherCount;
    }

    @Override
    public boolean hasWinner() {
        return isWinner(Mark.WHITE) || isWinner(Mark.BLACK);
    }

    @Override
    public Mark getWinner() {
        if (isWinner(Mark.WHITE)) {
            return Mark.WHITE;
        }
        if (isWinner(Mark.BLACK)) {
            return Mark.BLACK;
        }
        return null;
    }

    @Override
    public boolean isDraw() {
        return this.isFull() && !this.hasWinner();
    }

    @Override
    public Mark[] getFields() {
        return fields;
    }

    @Override
    public Set<Integer> getFields(Mark mark) {
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < DIM * DIM; i++) {
            if (fields[i] == mark) {
                result.add(i);
            }
        }
        return result;
    }

    @Override
    public int getScore(Mark mark) {
        return (int) Arrays.stream(fields).filter(m -> m == mark).count();
    }
}
