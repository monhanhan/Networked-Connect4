package ui;

import java.util.Optional;

import core.Connect4;
import core.Connect4ComputerPlayer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

//TODO: there is a bug in this code that prevents the player from ever winning. 
//Find it and squash it. It lives in the code to update player moves. 

/**
 * 
 * @author Ryan Munin
 * @version 1.0
 * 
 *          This class provides a GUI for the Connect4 game. It allows the
 *          player to select if they would prefer to play against a computer or
 *          another human. It then allows the player to play the game by
 *          clicking on the column in which they want their piece to go.
 *
 */
public class Connect4GUI extends Application {
    Stage primaryStage;

    private Connect4 myBoard;
    private boolean computerPlayer;
    private boolean redTurn;

    // These exist here so that I don't have any magic numbers and to make changing
    // the scale easier if I need to do that later.
    private final int PANESIZE = 338;
    private final int INSET = 8;
    private final int TILESIZE = 46;

    /**
     * This starts the application.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
	this.myBoard = new Connect4();

	// This generates an alert to let the player pick their opponent.
	Alert computerChoice = new Alert(Alert.AlertType.CONFIRMATION);
	computerChoice.setTitle("Play against computer");
	computerChoice.setHeaderText("Press ok to play against a computer or cancel to play against a human");
	computerChoice.setContentText(
		"If you close this window without making a choice, you will play against a human by default.");

	Optional<ButtonType> result = computerChoice.showAndWait();
	ButtonType button = result.orElse(ButtonType.CANCEL);

	if (button == ButtonType.OK) {
	    this.computerPlayer = true;

	} else if (button == ButtonType.CANCEL) {
	    this.computerPlayer = false;
	}

	this.redTurn = true;

	playGame(primaryStage);

    }

    /**
     * This handles the logic to draw and redraw the game as well as setting up the
     * buttons the player will need to click on.
     * 
     * @param primaryStage
     */
    public void playGame(Stage primaryStage) {
	BorderPane myBorders = makeBorders();

	Scene myScene = new Scene(myBorders);

	makeMoves(myScene);

	primaryStage.setTitle("Connect 4");
	primaryStage.setScene(myScene);
	this.primaryStage = primaryStage;
	primaryStage.show();
	checkGameOver(myScene);

    }

    /**
     * This sets up a BorderPane to hold the smaller panes that will make up our
     * grid.
     * 
     * @return BorderPane
     */
    private BorderPane makeBorders() {
	TilePane myTiles = makePanes();
	myTiles.setMaxSize(PANESIZE, PANESIZE);
	myTiles.setMinSize(PANESIZE, PANESIZE);

	BorderPane myBorders = new BorderPane();

	myBorders.setCenter(myTiles);

	return myBorders;

    }

    /**
     * This makes TilePanes that are colored correctly depending on which player has
     * a piece in a given spot.
     * 
     * @return is a TilePane with circles of the appropriate colors.
     */
    private TilePane makePanes() {
	char[][] currBoard = myBoard.getBoard();

	TilePane myTiles = new TilePane();

	myTiles.setAlignment(Pos.CENTER);

	myTiles.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));

	for (int i = 0; i < 6; i++) {
	    for (int j = 0; j < 7; j++) {
		StackPane tempPane = new StackPane();

		tempPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));

		Circle myCirc = new Circle();

		if (currBoard[i][j] == 'X') {
		    myCirc.setFill(Color.RED);

		} else if (currBoard[i][j] == 'O') {
		    myCirc.setFill(Color.BLUE);

		} else {
		    myCirc.setFill(Color.WHITE);

		}

		myCirc.setRadius(20);

		tempPane.getChildren().add(myCirc);

		StackPane.setMargin(myCirc, new Insets(2, 2, 2, 2));

		myTiles.getChildren().add(tempPane);

	    }
	}
	return myTiles;

    }

    /**
     * This makes the computer move and then returns control to the player.
     * 
     * @param myScene is the current scene we are using.
     */
    private void computerTurn(Scene myScene) {
	Connect4ComputerPlayer.takeTurn(myBoard, 'O');
	redTurn = true;

    }

    /**
     * This checks if a game is over and displays an alert accordingly.
     * 
     * @param myScene is the scene in use.
     */
    private void checkGameOver(Scene myScene) {
	boolean win = myBoard.checkVictory();
	boolean tie = myBoard.checkTie();

	if (win || tie) {
	    Alert gameOver = new Alert(AlertType.INFORMATION);
	    gameOver.setTitle("Game Over");

	    if (tie) {
		gameOver.setHeaderText("You Tie");

	    } else if (!redTurn) {
		gameOver.setHeaderText("Red Wins!");

	    } else {
		gameOver.setHeaderText("Blue Wins!");

	    }

	    gameOver.showAndWait();
	    System.exit(0);

	}

    }

    /**
     * This handles the logic for players making moves. If the player is playing
     * against the computer, the computer moves immediately after the player does.
     * 
     * @param myScene is the Scene object currently in use.
     */
    private void makeMoves(Scene myScene) {
	char player;
	if (redTurn) {
	    player = 'X';

	} else {
	    player = 'O';
	}

	// This makes it so that when the player clicks a column they get to
	// make a move.
	myScene.setOnMouseClicked((event) -> {
	    int x = (int) Math.round(event.getSceneX());
	    int y = (int) Math.round(event.getSceneY());

	    // This insures we have a valid position on which to click.
	    if ((x > INSET) && (x < PANESIZE - INSET) && (y > INSET) && (y < (PANESIZE - INSET))) {

		// This converts the click from pixels to grid position.
		int mathX = (x - INSET) / TILESIZE;

		if (myBoard.addPiece(mathX, player)) {

		    redTurn = !redTurn;
		    if (computerPlayer) { // TODO: the devil lives here.
			computerTurn(myScene);
		    }

		    playGame(primaryStage);

		}

	    }

	});

    }

}