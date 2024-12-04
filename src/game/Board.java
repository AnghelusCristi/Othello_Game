package game;

import exceptions.IllegalMoveException;
import exceptions.InvalidFieldException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * This is the board for each game of othello. The board is 8X8 cells. Numbering is as follows:
 * 0  1  2  3  4  5  6  7
 * 8  9  10 11 12 13 14 15
 * 16 17 18 19 20 21 22 23
 * 24 25 26 27 28 29 30 31
 * 32 33 34 35 36 37 38 39
 * 40 41 42 43 44 45 46 47
 * 48 49 50 51 52 53 54 55
 * 56 57 58 59 60 61 62 63
 */
public interface Board {
    int DIM = 8;
    Mark[] FIELDS = null;

    /**
     * Creates a deep copy of this board.
     *
     * @return An exact copy of the board.
     */
    /*@ ensures \result != this;
     ensures (\forall int i; (i >= 0 && i < DIM*DIM); \result.FIELDS[i] == this.FIELDS[i]);
     @*/
    Board deepCopy();

    /**
     * Sets all starting fields to the configuration supplied.
     * @param config the config to set up
     */
    void setupConfig(BoardConfiguration config);

    /**
     * Calculates the index belonging to the field from a (row, col)-pair
     *
     * @return the index belonging to the (row,col)-field
     */
    /*@ requires row >= 0 && row < DIM;
    requires col >= 0 && row < DIM;
     @*/
    int index(int row, int col) throws InvalidFieldException;

    /**
     * Calculates the row associated with the index.
     * @param index the index for the row
     * @return the row of the index
     */
    //@ requires isField(index);
    int row(int index);

    /**
     * Calculates the column associated with the index.
     * @param index the index for the row
     * @return the column of the index
     */
    //@ requires isField(index);
    int column(int index);

    /**
     * Returns true if index is a valid index of a field on the board.
     *
     * @return true if 0 <= index < DIM*DIM, otherwise false.
     */
    //@ ensures index >= 0 && index < DIM*DIM ==> \result == true;
    //@ pure;
    boolean isField(int index);

    /**
     * Returns the content of the field i.
     *
     * @param i the number of the field (see NUMBERING)
     * @return the mark on the field
     */
    /*@ requires isField(i);
    ensures \result == Mark.EMPTY || \result == Mark.BLACK || \result == Mark.WHITE;
    @ pure;
     @*/
    Mark getField(int i) throws InvalidFieldException;

    /**
     * Sets the content of field i to the mark m.
     *
     * @param i the field number
     * @param m the mark to be placed
     */
    /*@ requires isField(i);
    ensures getField(i) == m;
     @*/
    void setField(int i, Mark m) throws InvalidFieldException, IllegalMoveException;

    /**
     * Calculates the flips needed for the board to update.
     * It searches from the new move in all eight directions.
     * When it hits an empty field or the edge, it discards this direction.
     * If it hits a field with the other mark for x times in a row,
     * and then hits its own mark, it puts those other marked fields in the list.
     * @param newMove the index of the move that was made
     * @param mark the mark of the move just made
     * @return A list of all indexes to flip.
     */
    HashSet<Integer> calculateFlips(int newMove, Mark mark);


    /**
     * Calculate all fields which are possible moves with the index as a starting point.
     * @param index the index to search from in all 8 directions
     * @return a set of all indexes of possible moves
     */
    Set<Integer> possibleMoves(int index);

    /**
     * Calculate all fields which are possible moves for the mark and all fields containing mark.
     * @param mark the mark to search for
     * @return a hashset of all possible moves for the mark.
     */
    Set<Integer> possibleMoves(Mark mark);

    /**
     * Checks if the mark has moves to make.
     * @param mark the mark to search
     * @return true if there are possible moves
     */
    boolean hasMoves(Mark mark);

    /**
     * Flips one field.
     * @param index the field to flip
     */
    //@ requires isField(index);
    //@ requires getField(index) != Mark.EMPTY;
    //@ ensures getField(index) == \old(getField(index).other());
    void flipField(int index) throws InvalidFieldException;

    /**
     * This flips all fields in the hashset.
     * @param fields the hashset of the fields to flip
     */
    void flipFields(HashSet<Integer> fields);

    /**
     * Tests if the whole board is full.
     *
     * @return true if all fields are occupied
     */
    /*@
      ensures (\forall int i; (i >= 0 && i < DIM*DIM);
                               FIELDS[i] == Mark.BLACK || FIELDS[i] == Mark.WHITE);
      pure;
     */
    boolean isFull();

    /**
     * Tests if both marks cannot make more moves.
     * @return true if both marks are not able to make a move
     */
    boolean hasStaled();

    /**
     * Returns true if the game is over. The game is over when the whole board is full
     *
     * @return true if the game is over
     */
    //@ ensures isFull() ==> \result == true;
    //@ pure
    boolean gameOver();

    /**
     * Checks if the mark m has won. A mark wins if it controls more fields than its opponent
     *
     * @param m the mark of interest
     * @return true if the mark has won, but false if the other mark won or if it's a draw
     */
    /*@ requires m == Mark.BLACK || m == Mark.WHITE;
    requires (\num_of int i; 0 <= i && i < this.FIELDS.length;
    this.FIELDS[i] == m) > (\num_of int i; 0 <= i && i < this.FIELDS.length; this.FIELDS[i] == m);
    pure;
     @*/
    boolean isWinner(Mark m);

    /**
     * Returns true if the game has a winner.
     * @return true if the game has a winner.
     */
    //@ ensures isWinner(Mark.BLACK) || isWinner(Mark.WHITE) ==> \result == true;
    boolean hasWinner();

    /**
     * Returns the mark of the player who won.
     *
     * @return the mark of winner, null if not applicable
     */
    //@ requires gameOver();
    //@ ensures isWinner(\result);
    Mark getWinner();

    /**
     * Returns true if the board is resulted in a draw.
     * The board is full, but both players have 32 stones on the board.
     * @return true if the board is resulted in a draw
     */
    //@ requires this.gameOver() && !(isWinner(Mark.WHITE) || isWinner(Mark.BLACK));
    boolean isDraw();

    /**
     * The getter for getting the private fields in board.
     * @return the fields list in board
     */
    Mark[] getFields();

    /**
     * Gets all the fields in the fields that contain the mark.
     * @param mark the mark to filter on
     * @return A set of indexes
     */
    Set<Integer> getFields(Mark mark);

    /*
     * Gets the total amount of stones for the mark.
     * @param mark the mark to count
     * @return the amount of marks in fields
     */
    int getScore(Mark mark);
}
