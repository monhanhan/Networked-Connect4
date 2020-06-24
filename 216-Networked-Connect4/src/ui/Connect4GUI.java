package ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import core.Connect4Constants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/**
 * This class provides a GUI for the Connect4 game. It allows the player to play
 * the game by clicking on the column in which they want their piece to go.
 * Networked games are now supported and must be initiated via the server.
 * 
 * @author Ryan Munin
 * @version 2.0
 * 
 */
public class Connect4GUI extends Application implements Connect4Constants {
	Stage primaryStage;

	private char player;
	private char otherPlayer;
	private boolean myTurn;
	private boolean isGameOver;
	private boolean waiting = true;

	private Label titleLabel = new Label();
	private Label statusLabel = new Label();

	private Cell[][] myCell = new Cell[6][7];

	private String host = "localhost";
	private int port = 8000;

	private Socket mySocket;

	private DataInputStream fromServer;
	private DataOutputStream toServer;

	private int columnSelected;

	/**
	 * This starts the application.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.myTurn = false;
		this.isGameOver = false;

		GridPane myGrid = new GridPane();
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				myGrid.add(myCell[i][j] = new Cell(i, j), j, i);
			}
		}

		BorderPane myBorderPane = new BorderPane();
		myBorderPane.setTop(titleLabel);
		myBorderPane.setCenter(myGrid);
		myBorderPane.setBottom(statusLabel);

		Scene myScene = new Scene(myBorderPane, 320, 350);
		primaryStage.setTitle("Connect4Client");
		primaryStage.setScene(myScene);
		primaryStage.show();

		connectToServer();

	}

	/**
	 * This handles connecting to the server and starts a new thread that
	 * handles the logic of playing the game.
	 */
	private void connectToServer() {
		try {
			Socket socket = new Socket(host, port);

			fromServer = new DataInputStream(socket.getInputStream());
			toServer = new DataOutputStream(socket.getOutputStream());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/**
		 * This thread handles the logic of playing the game.
		 */
		new Thread(() -> {
			try {
				int playerInt = fromServer.readInt();
				if (playerInt == PLAYER1) {
					this.player = 'X';
					this.otherPlayer = 'O';
					Platform.runLater(() -> {
						titleLabel.setText("Player 1 with token 'X'");
						statusLabel.setText("Waiting for player 2 to join");
					});

					// Start notification. Int is discarded.
					fromServer.readInt();

					Platform.runLater(() -> statusLabel
							.setText("Player 2 has joined. I start first"));

					myTurn = true;

				} else if (playerInt == PLAYER2) {
					this.player = 'O';
					this.otherPlayer = 'X';
					Platform.runLater(() -> {
						titleLabel.setText("Player 2 with token 'O'");
						statusLabel.setText("Waiting for player 1 to move");
					});
				}

				// This loop dictates that the players continue playing until
				// the game concludes.
				while (!isGameOver) {
					if (playerInt == PLAYER1) {
						waitForPlayerAction();
						sendMove();
						receiveInfoFromServer();

					} else if (playerInt == PLAYER2) {
						receiveInfoFromServer();
						waitForPlayerAction();
						sendMove();
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}).start();

	}

	/**
	 * This makes the program wait until a valid move has been submitted.
	 * 
	 * @throws InterruptedException
	 */
	private void waitForPlayerAction() throws InterruptedException {
		while (waiting) {
			Thread.sleep(100);
		}

		waiting = true;
	}

	/**
	 * This takes information from the server and handles program execution
	 * based on what the server sends.
	 * 
	 * @throws IOException
	 */
	private void receiveInfoFromServer() throws IOException {
		int status = fromServer.readInt();

		if (status == PLAYER1_WON) {
			// Player 1 won, stop playing
			isGameOver = true;
			if (player == 'X') {
				Platform.runLater(() -> statusLabel.setText("I won! (X)"));
			} else if (player == 'O') {
				Platform.runLater(
						() -> statusLabel.setText("Player 1 (X) has won!"));
				receiveMove();
			}

		} else if (status == PLAYER2_WON) {
			// Player 2 won, stop playing
			isGameOver = true;
			if (player == 'O') {
				Platform.runLater(() -> statusLabel.setText("I won! (O)"));
			} else if (player == 'X') {
				Platform.runLater(
						() -> statusLabel.setText("Player 2 (O) has won!"));
				receiveMove();
			}

		} else if (status == DRAW) {
			// No winner, game is over
			isGameOver = true;
			Platform.runLater(
					() -> statusLabel.setText("Game is over, no winner!"));

			if (player == 'O') {
				receiveMove();
			}

		} else {
			receiveMove();
			Platform.runLater(() -> statusLabel.setText("My turn"));
			myTurn = true; // It is my turn
		}
	}

	/**
	 * This sends a move to the server and calls update to update the GUI.
	 * 
	 * @throws IOException
	 */
	private void sendMove() throws IOException {
		toServer.writeInt(columnSelected);
		fromServer.readInt();
		update();
	}

	/**
	 * This receives a move from the other player and updates the GUI
	 * 
	 * @throws IOException
	 */
	private void receiveMove() throws IOException {
		int row = fromServer.readInt();
		int column = fromServer.readInt();
		Platform.runLater(() -> myCell[row][column].setToken(otherPlayer));
	}

	/**
	 * This updates the GUI with the move the player just made after it has been
	 * handled by the server.
	 * 
	 * @throws IOException
	 */
	private void update() throws IOException {
		int row = fromServer.readInt();
		int column = fromServer.readInt();
		Platform.runLater(() -> myCell[row][column].setToken(player));
	}

	/**
	 * This creates clickable cells that can return their coordinates.
	 * 
	 * @author Ryan
	 *
	 */
	public class Cell extends Pane {
		// Indicate the row and column of this cell in the board
		private int column;

		// Token used for this cell
		private char token = ' ';

		public Cell(int row, int column) {
			this.column = column;
			this.setPrefSize(2000, 2000); // What happens without this?
			setStyle("-fx-border-color: black"); // Set cell's border
			this.setOnMouseClicked(e -> handleMouseClick());
		}

		/** Return token */
		public char getToken() {
			return token;
		}

		/** Set a new token */
		public void setToken(char c) {
			token = c;
			repaint();
		}

		protected void repaint() {
			if (token == 'X') {
				Line line1 = new Line(10, 10, this.getWidth() - 10,
						this.getHeight() - 10);
				line1.endXProperty().bind(this.widthProperty().subtract(10));
				line1.endYProperty().bind(this.heightProperty().subtract(10));
				Line line2 = new Line(10, this.getHeight() - 10,
						this.getWidth() - 10, 10);
				line2.startYProperty().bind(this.heightProperty().subtract(10));
				line2.endXProperty().bind(this.widthProperty().subtract(10));

				// Add the lines to the pane
				this.getChildren().addAll(line1, line2);
			} else if (token == 'O') {
				Ellipse ellipse = new Ellipse(this.getWidth() / 2,
						this.getHeight() / 2, this.getWidth() / 2 - 10,
						this.getHeight() / 2 - 10);
				ellipse.centerXProperty().bind(this.widthProperty().divide(2));
				ellipse.centerYProperty().bind(this.heightProperty().divide(2));
				ellipse.radiusXProperty()
						.bind(this.widthProperty().divide(2).subtract(10));
				ellipse.radiusYProperty()
						.bind(this.heightProperty().divide(2).subtract(10));
				ellipse.setStroke(Color.BLACK);
				ellipse.setFill(Color.WHITE);

				getChildren().add(ellipse); // Add the ellipse to the pane
			}
		}

		/**
		 * This sets up a handler that updates to the last column that has been
		 * clicked so that the number can be sent to the server.
		 */
		private void handleMouseClick() {
			// If cell is not occupied and the player has the turn
			if (token == ' ' && myTurn) {
				myTurn = false;
				columnSelected = column;
				statusLabel.setText("Waiting for the other player to move");
				waiting = false; // Just completed a successful move
			}
		}
	}

}
