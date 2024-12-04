package game;

import exceptions.IllegalMoveException;
import exceptions.InvalidFieldException;

/**
 * A representation of a game that can be played on the board.
 */
public interface Game {
    /**
     * Makes a move on the board of this game for the current player.
     * @param index the index on the board where to put the mark.
     * @throws IllegalMoveException when the move is not possible.
     * @throws InvalidFieldException when the index is not a valid field on the board.
     */
    void makeMove(int index) throws IllegalMoveException, InvalidFieldException;

    /**
     * Returns the current situation of the game on the board.
     * @return representation of the current board.
     */
    String update();

    /**
     *
     * Returns the current player of the game.
     * @return the current player of the game.
     */
    Player getCurrent();

    /**
     * Returns the board of the game.
     * @return the board of the game.
     */
    Board getBoard();

    /**
     * Returns an array with the players of the game.
     * @return the players of the game as an array.
     */
    Player[] getPlayers();

    /**
     * Changes the current player's turn to the other one.
     */
    void pass();

}
