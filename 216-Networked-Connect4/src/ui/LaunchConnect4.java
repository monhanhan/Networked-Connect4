package ui;

import java.util.Scanner;

import javafx.application.Application;

/**
 * This class launches the ui elements for Connect4
 * 
 * @author Ryan Munin
 * @version 1.0
 * 
 */
public class LaunchConnect4 {
	/**
	 * This is main. It calls all other methods in order.
	 * 
	 * @param args is unused.
	 */
	public static void main(String[] args) {
		// This launches the Connect4GUI application.
		Application.launch(Connect4GUI.class);

		/**
		 * The code block below is commented out because only the GUI is in use
		 * for now. Scanner myScanner = new Scanner(System.in);
		 * 
		 * if (chooseGUI(myScanner)) { Application.launch(Connect4GUI.class);
		 * 
		 * } else
		 * 
		 * { Connect4TextConsole newPlayer = new Connect4TextConsole();
		 * newPlayer.start(); }
		 */

	}

	/**
	 * This method asks the player if they would like to play with a GUI or
	 * text-based interface
	 * 
	 * @param myScanner is a scanner object
	 * @return is a boolean representation of whether the player wants to play
	 *         with a GUI
	 */
	private static boolean chooseGUI(Scanner myScanner) {
		boolean badInput = true;
		boolean chooseGUI = false;

		while (badInput) {
			System.out.print(
					"Would you like to play using a GUI? Press 'G' for GUI or 'T' for text only");
			System.out.println();
			System.out.println();
			System.out.print(">>");

			String myInput = myScanner.next();

			System.out.println();
			System.out.println();

			// Cleaning string.
			myInput = myInput.toUpperCase().trim();

			if ((myInput.equals("G")) || (myInput.equals("T"))) {
				badInput = false;
				chooseGUI = (myInput.equals("G"));
			}
		}

		return chooseGUI;
	}

}
