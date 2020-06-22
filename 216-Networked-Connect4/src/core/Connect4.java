package core;

/**
 * 
 * @author Ryan Munin
 * @version 1.0
 *
 */
public class Connect4 {
    private char[][] board;
    private int filledSpots;
    private int totalSpots;
    private int lastPosX;
    private int lastPosY;

    /**
     * This is the constructor the the Connect4 object. It creates a blank game
     * board.
     */
    public Connect4() {
	this.board = new char[6][7];
	for (int i = 0; i < 6; i++) {
	    for (int j = 0; j < 7; j++) {
		this.board[i][j] = ' ';

	    }
	}
	this.filledSpots = 0;
	this.totalSpots = 42;

    }

    /**
     * This checks to see if any victory conditions have been met.
     * 
     * @return This returns a boolean value indicating if any victory conditions
     *         have been met.
     */
    public boolean checkVictory() {
	if (board[lastPosY][lastPosX] == ' ') {
	    return false;
	}

	return (checkDown() || checkLeft() || checkRight() || checkUL() || checkUR() || checkDL() || checkDR());

    }

    /**
     * This checks to see if all spaces are full and there is a tie.
     * 
     * @return This returns a boolean value indicating if all spaces are full and
     *         there is a tie.
     */
    public boolean checkTie() {
	return filledSpots == totalSpots;
    }

    /**
     * This checks if there is a victory downward from the most recently placed
     * piece.
     * 
     * @return is a boolean value indicating a victory.
     */
    private boolean checkDown() {
	int currPosY = lastPosY;
	int currPosX = lastPosX;
	char player = board[lastPosY][lastPosX];
	int totalInRow = 0;

	while (currPosY < 6) {
	    // If we don't find the player's piece, they can't have won in this direction.
	    if (board[currPosY][currPosX] != player) {
		return false;

	    } else {
		currPosY++;
		totalInRow++;
	    }

	    if (totalInRow >= 4) {
		return true;
	    }
	}

	// If we get here it is because we ran out of room in this direction.
	return false;
    }

    /**
     * This checks if there is a victory to the left of the most recently placed
     * piece.
     * 
     * @return is a boolean value indicating a victory.
     */
    private boolean checkLeft() {
	int currPosY = lastPosY;
	int currPosX = lastPosX;
	char player = board[lastPosY][lastPosX];
	int totalInRow = 0;

	while (currPosX >= 0) {
	    // If we don't find the player's piece, they can't have won in this direction.
	    if (board[currPosY][currPosX] != player) {
		return false;

	    } else {
		currPosX--;
		totalInRow++;
	    }

	    if (totalInRow >= 4) {
		return true;
	    }
	}

	// If we get here it is because we ran out of room in this direction.
	return false;
    }

    /**
     * This checks if there is a victory to the right of the most recently placed
     * piece.
     * 
     * @return is a boolean value indicating a victory.
     */
    private boolean checkRight() {
	int currPosY = lastPosY;
	int currPosX = lastPosX;
	char player = board[lastPosY][lastPosX];
	int totalInRow = 0;

	while (currPosX < 7) {
	    // If we don't find the player's piece, they can't have won in this direction.
	    if (board[currPosY][currPosX] != player) {
		return false;

	    } else {
		currPosX++;
		totalInRow++;
	    }

	    if (totalInRow >= 4) {
		return true;
	    }
	}

	// If we get here it is because we ran out of room in this direction.
	return false;
    }

    /**
     * This checks if there is a victory diagonal to the up and left of the most
     * recently placed piece.
     * 
     * @return is a boolean value indicating a victory.
     */
    private boolean checkUL() {
	int currPosY = lastPosY;
	int currPosX = lastPosX;
	char player = board[lastPosY][lastPosX];
	int totalInRow = 0;

	while ((currPosX >= 0) && (currPosY >= 0)) {
	    // If we don't find the player's piece, they can't have won in this direction.
	    if (board[currPosY][currPosX] != player) {
		return false;

	    } else {
		currPosX--;
		currPosY--;
		totalInRow++;
	    }

	    if (totalInRow >= 4) {
		return true;
	    }
	}

	// If we get here it is because we ran out of room in this direction.
	return false;
    }

    /**
     * This checks if there is a victory diagonal to the up and right of the most
     * recently placed piece.
     * 
     * @return is a boolean value indicating a victory.
     */
    private boolean checkUR() {
	int currPosY = lastPosY;
	int currPosX = lastPosX;
	char player = board[lastPosY][lastPosX];
	int totalInRow = 0;

	while ((currPosX < 7) && (currPosY >= 0)) {
	    // If we don't find the player's piece, they can't have won in this direction.
	    if (board[currPosY][currPosX] != player) {
		return false;

	    } else {
		currPosX++;
		currPosY--;
		totalInRow++;
	    }

	    if (totalInRow >= 4) {
		return true;
	    }
	}

	// If we get here it is because we ran out of room in this direction.
	return false;
    }

    /**
     * This checks if there is a victory diagonal to the down and left of the most
     * recently placed piece.
     * 
     * @return is a boolean value indicating a victory.
     */
    private boolean checkDL() {
	int currPosY = lastPosY;
	int currPosX = lastPosX;
	char player = board[lastPosY][lastPosX];
	int totalInRow = 0;

	while ((currPosX >= 0) && (currPosY < 6)) {
	    // If we don't find the player's piece, they can't have won in this direction.
	    if (board[currPosY][currPosX] != player) {
		return false;

	    } else {
		currPosX--;
		currPosY++;
		totalInRow++;
	    }

	    if (totalInRow >= 4) {
		return true;
	    }
	}

	// If we get here it is because we ran out of room in this direction.
	return false;
    }

    /**
     * This checks if there is a victory diagonal to the down and right of the most
     * recently placed piece.
     * 
     * @return is a boolean value indicating a victory.
     */
    private boolean checkDR() {
	int currPosY = lastPosY;
	int currPosX = lastPosX;
	char player = board[lastPosY][lastPosX];
	int totalInRow = 0;

	while ((currPosX < 7) && (currPosY < 6)) {
	    // If we don't find the player's piece, they can't have won in this direction.
	    if (board[currPosY][currPosX] != player) {
		return false;

	    } else {
		currPosX++;
		currPosY++;
		totalInRow++;
	    }

	    if (totalInRow >= 4) {
		return true;
	    }
	}

	// If we get here it is because we ran out of room in this direction.
	return false;
    }

    /**
     * This adds a piece to the board, finding the lowest spot the piece can go.
     * 
     * @param col    is the column to which the player wants to add a piece
     * @param player is the character being added (X or O)
     * @return is a boolean indicating if the piece was successfully added.
     */
    public boolean addPiece(int col, char player) {
	int xPos = col;
	int yPos = findLowestRow(col);

	// this is a check to see if the column is full.
	if (yPos == -1) {
	    return false;

	} else {
	    board[yPos][xPos] = player;
	    lastPosX = xPos;
	    lastPosY = yPos;
	    filledSpots++;
	}

	return true;
    }

    /**
     * This finds the lowest row in a given column.
     * 
     * @param col is the column being checked.
     * @return is an int value representing the row to which a piece will be added.
     *         a value of -1 indicates that a column is full.
     */
    private int findLowestRow(int col) {
	int currSpot = -1;

	// if the top position is empty return -1 so we know this column is full.
	if (board[0][col] != ' ') {
	    return currSpot;

	} else {
	    currSpot = 0;

	    while (currSpot < 5) {
		// Check if the next lowest spot is empty. If not, break the loop
		if (board[currSpot + 1][col] != ' ') {
		    break;
		}

		// if the next lowest spot is empty, start the loop again from that spot
		currSpot = currSpot + 1;

	    }
	}

	return currSpot;
    }

    /**
     * This returns the 2d array representation of the board for display elsewhere.
     * 
     * @return is a 2d char array representing the board.
     */
    public char[][] getBoard() {
	return board;
    }

}
