package ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import core.Connect4;
import core.Connect4ComputerPlayer;
import core.Connect4Constants;

/**
 * 
 * @author Ryan Munin
 * @version 4.0
 *
 */
public class Connect4TextConsole implements Connect4Constants {
	private DataInputStream fromServer;
	private DataOutputStream toServer;

	private char player;

	private boolean myTurn;

	private boolean isGameOver;

	private String host = "localhost";
	private int port = 8000;

	private Socket mySocket;

	private char[][] board;

	public Connect4TextConsole() {
		this.myTurn = false;
		this.isGameOver = false;

		this.board = new char[6][7];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				this.board[i][j] = ' ';

			}
		}

		try {
			this.mySocket = new Socket(host, port);

			fromServer = new DataInputStream(mySocket.getInputStream());
			toServer = new DataOutputStream(mySocket.getOutputStream());

		} catch (Exception e) {
			System.out.println("No server available.");
			System.exit(0);
		}
	}

	public void start() {
		new Thread(() -> {
			try {
				// Get the server to tell me if I joined first.
				int playerInt = fromServer.readInt();

				Scanner myScanner = new Scanner(System.in);

				// If I joined first, I am player X
				if (playerInt == PLAYER1) {
					this.player = 'X';
					System.out
							.println("You are first player. Your marker is X");
					System.out.println("Waiting for player 2");
					// TODO: If I want to include code to play against a
					// computer I would put it here.

					// This line gets an int from the server indicating that a
					// player has joined and that I am ready to start the game.
					// This int itself is useless to me.
					fromServer.readInt();

					myTurn = true;

					// If I joined 2nd I am player O.
				} else {
					this.player = 'O';
					System.out.println(
							"Welcome to the game. You are player 2 and your token is O");

				}

				// TODO: Implement a loop here to take turns until the game is
				// over. Figure out if a board object can be passed from the
				// server.

				while (!isGameOver) {
					if (myTurn) {
						printBoard();
						// TODO: logic to take player move lives here. Parse
						// input, give to server, if item is returned true,
						// print the board after it gets passed from the server.
					} else {
						// TODO: Set up code to wait for player 2 to move.
					}
				}

			} catch (IOException e) {
				System.out.println(
						"Sorry, something went wrong with the server. Please try again later.");
			}

		}).start();
	}

	/**
	 * This handles the logic for playing a text based game of connect4
	 * 
	 */
	private void playTextGame() {
		// Create a new instance of the game.
		Connect4 myGame = new Connect4();

		Scanner myScanner = new Scanner(System.in);

		printBoard(myGame);
		printGameStart();

		char player = 'Q';

		// TODO: This only exists here to bypass the logic of playing with a
		// computer. Correct this later.
		// boolean playComputer = playComputer(myScanner);
		boolean playComputer = false;

		if (playComputer) {
			System.out.println("Start game against computer.");
		}

		boolean isGameOver = false;
		boolean victory = false;

		// Until an end game condition is met, swap turns and allow the players
		// to play
		// the game.
		while (!isGameOver) {
			if (player != 'X') {
				player = 'X';
			} else {
				player = 'O';
			}

			// This is if statement enables a computer turn if the player has
			// opted to play
			// against a computer and if it is the computer's turn.
			if ((player == 'O') && playComputer) {
				System.out.println();
				System.out.println("Computer turn");
				System.out.println();
				Connect4ComputerPlayer.takeTurn(myGame, player);
				System.out.println();

			} else {
				takeTurn(player, myScanner, myGame);

			}

			// Check end game conditions.
			victory = myGame.checkVictory();
			boolean tie = myGame.checkTie();
			isGameOver = (victory || tie);

			printBoard(myGame);

		}

		// Print results
		printGameEnd(victory, player);

		// Close down the game.
		myScanner.close();
		System.exit(0);

	}

	/**
	 * This method takes player input for whether or not a player wants to play
	 * against a computer. The method will continue prompting until it receives
	 * either a 'C' or a 'P'. Input can be upper or lower case with any number
	 * of spaces.
	 * 
	 * @return is a boolean representing whether the player wants to play
	 *         against a computer.
	 */
	private static boolean playComputer(Scanner myScanner) {
		boolean badInput = true;
		boolean playComputer = false;

		while (badInput) {
			System.out.print(
					"Enter 'P' if you want to play against another player; enter 'C' to play against computer.");
			System.out.println();
			System.out.println();
			System.out.print(">>");

			String myInput = myScanner.next();

			System.out.println();
			System.out.println();

			// Cleaning string.
			myInput = myInput.toUpperCase().trim();

			if ((myInput.equals("C")) || (myInput.equals("P"))) {
				badInput = false;
				playComputer = (myInput.equals("C"));
			}
		}

		return playComputer;

	}

	/**
	 * This prints the opening message of the game.
	 */
	private static void printGameStart() {
		System.out.println("Begin Game.");

	}

	/**
	 * This prints the game board that has been passed from myGame
	 * 
	 * @param myGame is a Connect4 object.
	 */
	private void printBoard() {
		for (char[] subArray : board) {
			System.out.print("|");

			for (char currChar : subArray) {
				System.out.print(currChar);
				System.out.print("|");
			}
			System.out.println();
		}

	}

	/**
	 * This prints the end of game messages indicating victory/winner or a tie.
	 * 
	 * @param victory is a boolean indicating if there has been a victory. If
	 *                false, that indicates a tie.
	 * @param player  is the player (X or O) that won the match.
	 */
	private static void printGameEnd(boolean victory, char player) {
		if (victory) {
			System.out.print("Player ");
			System.out.print(player);
			System.out.print(" Won the Game");

		} else {
			System.out.print("The Game was a Tie!");

		}
	}

	/**
	 * This allows players to play their turn. It takes input and does error
	 * checking for invalid input before adding pieces to the board. If a column
	 * if full or the move is invalid the player will be prompted again for new
	 * input.
	 * 
	 * @param myScanner is a scanner object taking input from the console.
	 */
	private void takeTurn(Scanner myScanner) {
		System.out.print("Player");
		System.out.print(player);
		System.out.print(" - your turn. ");

		int serverReturn = 0;

		while (serverReturn != CONTINUE) {
			int col = parseInput(myScanner);
			try {
				toServer.writeInt(col);
				serverReturn = fromServer.readInt();

			} catch (IOException e) {
				// TODO: consider writing a proper error message here.
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	/**
	 * This is a helper function for takeTurn that specifically handles input
	 * and error checking.
	 * 
	 * @param myScanner is a scanner object taking input from the console
	 * @return is the int representation of player input.
	 */
	private static int parseInput(Scanner myScanner) {
		boolean badInput = true;
		int inInt = -1;

		while (badInput) {
			System.out.println("Choose a column number from 1-7.");

			String input = myScanner.next();
			try {
				inInt = Integer.parseInt(input);

			} catch (Exception notValidInput) {
				continue;

			}
			if ((inInt >= 1) && (inInt <= 7)) {
				inInt = inInt - 1;
				badInput = false;
			}

		}

		return inInt;

	}

}