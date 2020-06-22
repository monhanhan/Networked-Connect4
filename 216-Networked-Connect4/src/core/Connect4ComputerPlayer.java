package core;

/**
 * 
 * @author Ryan Munin
 * @version 1.0 This class controls the logic for a computer controlled player
 *          to play connect 4.
 *
 */
public class Connect4ComputerPlayer {
    /**
     * This is the logic for a computer player to place their pieces. It places in
     * the leftmost column until that column is full, then moves on to the next
     * column.
     * 
     * @param myGame is a connect4 game object.
     * @param player is a character representing which player the computer is
     *               playing as. X or O.
     */
    public static void takeTurn(Connect4 myGame, char player) {
	boolean validMove = false;
	int currCol = 0;

	while (!validMove) {
	    validMove = myGame.addPiece(currCol, player);
	    currCol++;
	}

    }

}
