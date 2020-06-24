package ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

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

	private char otherPlayer;

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
					this.otherPlayer = 'O';
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
					this.otherPlayer = 'X';
					System.out.println(
							"Welcome to the game. You are player 2 and your token is O");

				}

				// This is set up so we can check our end game state later.
				int gameEnd = CONTINUE;

				while (!isGameOver) {
					if (myTurn) {
						printBoard();
						takeTurn(myScanner);
						int newY = fromServer.readInt();
						int newX = fromServer.readInt();
						board[newY][newX] = player;
						myTurn = false;

						// Check for a game ending condition. If found, break
						// the loop.
						gameEnd = fromServer.readInt();
						if (gameEnd != CONTINUE) {
							isGameOver = true;
							break;
						}

					} else {
						printBoard();
						int newY = fromServer.readInt();
						int newX = fromServer.readInt();
						// TODO: test print
						System.out.println("poopiedooks");
						board[newY][newX] = otherPlayer;
						myTurn = true;

						// Check for a game ending condition. If found, break
						// the loop.
						gameEnd = fromServer.readInt();
						if (gameEnd != CONTINUE) {
							isGameOver = true;
							break;
						}
					}
				}

				// If we got here it means we have reached an end game
				// condition.
				printGameEnd(gameEnd);

			} catch (IOException e) {
				System.out.println(
						"Sorry, something went wrong with the server. Please try again later.");
			}

		}).start();
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
	 * @param gameEnd is an int representation of how the game ended received
	 *                from the server.
	 */
	private void printGameEnd(int gameEnd) {
		if (gameEnd == DRAW) {
			System.out.print("The Game was a Tie!");

		} else {
			if (gameEnd == PLAYER1_WON) {
				System.out.print("Player X won the Game!");

			} else {
				System.out.print("Player Y won the Game!");
			}

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

		int serverReturn = INVALID;

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