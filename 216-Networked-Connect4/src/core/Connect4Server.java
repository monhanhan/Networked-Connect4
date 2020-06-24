package core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * This class runs a server application that will communicate with a client
 * application in order to play a game of Connect4
 * 
 * @author Ryan Munin
 * @version 1.0
 *
 */
public class Connect4Server extends Application implements Connect4Constants {
	private int sessionNo = 1;

	@Override
	/**
	 * Entry point for the program starts the server and begins a new thread to
	 * handle connections.
	 */
	public void start(Stage primaryStage) {
		TextArea serverLog = new TextArea();
		// Create a scene and place it in the stage
		Scene scene = new Scene(new ScrollPane(serverLog), 450, 200);
		primaryStage.setTitle("Connect4Server"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage

		/**
		 * This lambda handles the logic for taking connections and starting a
		 * game.
		 */
		new Thread(() -> {
			try {
				// Create a server socket
				ServerSocket serverSocket = new ServerSocket(8000);
				Platform.runLater(() -> serverLog.appendText(
						new Date() + ": Server started at socket 8000\n"));

				// Ready to create a session for every two players
				while (true) {
					Platform.runLater(() -> serverLog.appendText(
							new Date() + ": Wait for players to join session "
									+ sessionNo + '\n'));

					// Connect to player 1
					Socket player1 = serverSocket.accept();

					Platform.runLater(() -> {
						serverLog.appendText(
								new Date() + ": Player 1 joined session "
										+ sessionNo + '\n');
						serverLog.appendText("Player 1's IP address"
								+ player1.getInetAddress().getHostAddress()
								+ '\n');
					});

					// Notify that the player is Player 1
					new DataOutputStream(player1.getOutputStream())
							.writeInt(PLAYER1);

					// Connect to player 2
					Socket player2 = serverSocket.accept();

					Platform.runLater(() -> {
						serverLog.appendText(
								new Date() + ": Player 2 joined session "
										+ sessionNo + '\n');
						serverLog.appendText("Player 2's IP address"
								+ player2.getInetAddress().getHostAddress()
								+ '\n');
					});

					// Notify that the player is Player 2
					new DataOutputStream(player2.getOutputStream())
							.writeInt(PLAYER2);

					// Display this session and increment session number
					Platform.runLater(() -> serverLog.appendText(
							new Date() + ": Start a thread for session "
									+ sessionNo++ + '\n'));

					// Launch a new thread for this session of two players
					new Thread(new HandleASession(player1, player2)).start();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				System.exit(0);
			}
		}).start();
	}

	/**
	 * This class handles the moves and logic for a networked game of Connect4
	 * 
	 * @author Ryan Munin
	 * @version 1.0
	 *
	 */
	class HandleASession implements Runnable, Connect4Constants {
		private Socket player1;
		private Socket player2;

		private Connect4 board;

		private DataInputStream fromPlayer1;
		private DataOutputStream toPlayer1;
		private DataInputStream fromPlayer2;
		private DataOutputStream toPlayer2;

		/** Construct a thread */
		public HandleASession(Socket player1, Socket player2) {
			this.player1 = player1;
			this.player2 = player2;
		}

		/** Implement the run() method for the thread */
		public void run() {
			try {
				// Create data input and output streams
				this.fromPlayer1 = new DataInputStream(
						player1.getInputStream());
				this.toPlayer1 = new DataOutputStream(
						player1.getOutputStream());
				this.fromPlayer2 = new DataInputStream(
						player2.getInputStream());
				this.toPlayer2 = new DataOutputStream(
						player2.getOutputStream());

				this.board = new Connect4();

				// Write anything to notify player 1 to start
				// This is just to let player 1 know to start
				toPlayer1.writeInt(1);

				while (true) {

					// Player 1 takes a turn
					p1Move();

					// Check for a tie, update appropriately
					if (board.checkTie()) {
						toPlayer1.writeInt(DRAW);
						toPlayer2.writeInt(DRAW);
						break;

						// Check for player 2 victory.
					} else if (board.checkVictory()) {
						toPlayer1.writeInt(PLAYER1_WON);
						toPlayer2.writeInt(PLAYER1_WON);
						break;

						// If no tie or victory, continue.
					} else {
						// toPlayer1.writeInt(CONTINUE);
						toPlayer2.writeInt(CONTINUE);
					}

					// Send the move to player 2
					toPlayer2.writeInt(board.getLastY());
					toPlayer2.writeInt(board.getLastX());

					// player 2 takes a move
					p2Move();

					// Check for a tie, update appropriately
					if (board.checkTie()) {
						toPlayer1.writeInt(DRAW);
						toPlayer2.writeInt(DRAW);
						break;

						// Check for player 2 victory.
					} else if (board.checkVictory()) {
						toPlayer1.writeInt(PLAYER2_WON);
						toPlayer2.writeInt(PLAYER2_WON);
						break;

						// If no tie or victory, continue.
					} else {
						toPlayer1.writeInt(CONTINUE);
						// toPlayer2.writeInt(CONTINUE);
					}

					// Move sent to player 1
					toPlayer1.writeInt(board.getLastY());
					toPlayer1.writeInt(board.getLastX());

				}

			} catch (IOException ex) {
				ex.printStackTrace();
				System.exit(0);
			}
		}

		/**
		 * This takes the move from player 1 and returns the information of
		 * where a piece wound up to the ui from which it was sent.
		 * 
		 * @throws IOException
		 */
		private void p1Move() throws IOException {
			boolean valid = false;
			while (!valid) {
				int col = fromPlayer1.readInt();
				boolean moveAttempt = board.addPiece(col, 'X');

				// If the piece is added to a valid column the client will be
				// updated and the loop will exit.
				if (moveAttempt) {
					valid = true;
					toPlayer1.writeInt(CONTINUE);
					toPlayer1.writeInt(board.getLastY());
					toPlayer1.writeInt(board.getLastX());
				}
			}
		}

		/**
		 * This takes the move from player 2 and returns the information of
		 * where a piece wound up to the ui from which it was sent.
		 * 
		 * @throws IOException
		 */
		private void p2Move() throws IOException {
			boolean valid = false;
			while (!valid) {
				int col = fromPlayer2.readInt();
				boolean moveAttempt = board.addPiece(col, 'O');

				// If the piece is added to a valid column the client will be
				// updated and the loop will exit.
				if (moveAttempt) {
					valid = true;
					toPlayer2.writeInt(CONTINUE);
					toPlayer2.writeInt(board.getLastY());
					toPlayer2.writeInt(board.getLastX());
				}
			}
		}

	}

	/**
	 * The main method is only needed for the IDE with limited JavaFX support.
	 * Not needed for running from the command line.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
