package go_game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;

import go_game.protocol.Constants3;
import go_game.server.ClientHandler;

/**
 * Class for maintaining the Go game. 
 * 
 * @author Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public class Game extends Observable implements Constants3 {

	// -- Instance variables -----------------------------------------

	public static final int NUMBER_PLAYERS = 2;

	// Scanner for input line, to be used accross all classes
//	public static final Scanner line = new Scanner(System.in);
//	private ClientHandler clientHandlerP1;
//	private ClientHandler clientHandlerP2;

	/*@
       private invariant board != null;
	 */
	/**
	 * The board.
	 */
	private Board board;

	/*@
       private invariant players.length == NUMBER_PLAYERS;
       private invariant (\forall int i; 0 <= i && i < NUMBER_PLAYERS; players[i] != null); 
	 */
	/**
	 * The 2 players of the game.
	 */
	private Player[] players;

	/*@
       private invariant 0 <= current  && current < NUMBER_PLAYERS;
	 */
	/**
	 * Index of the current player.
	 */
	private int current;
	boolean previousTurnPassed;
	boolean isGameOver;	

	// -- Constructors -----------------------------------------------

	/*@
      requires s0 != null;
      requires s1 != null;
	 */
	/**
	 * Creates a new Game object.
	 * 
	 * @param s0
	 *            the first player
	 * @param s1
	 *            the second player
	 */
	public Game(Player s0, Player s1, int dim) {//, ClientHandler clientHandlerP1, ClientHandler clientHandlerP2) {
		// Create a board
		board = new Board(dim);
		
		// Set the players in an array, to make switching easy
		players = new Player[NUMBER_PLAYERS];
		players[0] = s0;
		players[1] = s1;
		
		// Game states and stuff
		current = 0;
		previousTurnPassed = false;
		isGameOver = false;
		
		// Set the clienthandlers
		

		// <-------------------------------------------------------------------------------
		// <---- HIER GEBLEVEN ------------------------------------------------------------
		// <-------------------------------------------------------------------------------
		// System.out.communication is send to the server side, not neccesarry
		System.out.println("New board (or 'Goban') of dimensions " + dim + " X " +  dim + " created...");
	}

	// -- Commands ---------------------------------------------------

	/**
	 * Starts the Go <br>
	 * Asks after each ended game if the user want to continue. Continues until
	 * the user does not want to play anymore.
	 */
	public void start() {
		boolean doorgaan = true;
		while (doorgaan) {
			reset();
			play();
			doorgaan = readBoolean("\n> Play another time? (y/n)?", "y", "n");
		}
	}

	/**
	 * Prints a question which can be answered by yes (true) or no (false).
	 * After prompting the question on standard out, this method reads a String
	 * from standard in and compares it to the parameters for yes and no. If the
	 * user inputs a different value, the prompt is repeated and te method reads
	 * input again.
	 * 
	 * @parom prompt the question to print
	 * @param yes
	 *            the String corresponding to a yes answer
	 * @param no
	 *            the String corresponding to a no answer
	 * @return true is the yes answer is typed, false if the no answer is typed
	 */
	private boolean readBoolean(String prompt, String yes, String no) {
		String answer;
//		do {
			System.out.print(prompt);
			sendMessageToBoth(prompt);
			//            try (line) {
//			answer = line.hasNextLine() ? line.nextLine() : null;
			//            }
//		} while (answer == null || (!answer.equals(yes) && !answer.equals(no)));
//		return answer.equals(yes);
			return false;
	}

	/**
	 * Resets the game. <br>
	 * The board is emptied and player[0] becomes the current player.
	 */
	private void reset() {
		current = 0;
		isGameOver = false;
		previousTurnPassed = false;
		board.reset();
	}

	/**
	 * Plays the Go game. <br>
	 * First the (still empty) board is shown. Then the game is played until it
	 * is over. Players can make a move one after the other. After each move,
	 * the changed game situation is printed.
	 */
	private void play() {
		// Show the first empty board
		this.gameUpdate();

		// Loop through the moves, the actual game
		//    	while (!this.board.gameOver()) {
		while (!isGameOver) {
			// Step 0:  Before playing, add a string representation of the board to the hashset
			this.board.addPreviousBoard(board.deepCopy());

			// Step 1: Send the messages to the current players
			players[current].sentMessage("\nYour turn\n");
			players[(current + 1) % 2].sentMessage(WAITFOROPPONENT);
			players[(current + 1) % 2].sentMessage("\nWait until opponent made his choice.\n");
			
			// Step 2: Keep trying to make moves until a valid move is made
			boolean isValidMove = false;
			int choice = -999;
			
			while(!isValidMove) {
				choice = this.players[this.current].makeMove(board);
				isValidMove = checkLegalMove(choice);
			}

			// Step 3: Keep track of two passes in a row
			if (previousTurnPassed && choice == -1) {
				// QUIT GAME
				String quitMessage = "\n\nTwo passes in a row, game is quit!\n\n";
				System.out.println(quitMessage);
				sendMessageToBoth(quitMessage);
				isGameOver = true;
				break;
			} else if (!previousTurnPassed && choice == -1) {
				previousTurnPassed = true;
			} else {
				previousTurnPassed = false;
			}

			// Step 4: Update the screen
			this.gameUpdate();	

			// Step 5: Switch players for next turn
			this.current = (current + 1) % 2;
		}

		// Print the final result, who has won!
		this.printResult();
	}

	/**
	 * Prints the game situation.
	 */
	private void gameUpdate() {
		String strBoardMessage = "\n Current game situation: \n\n" + board.toString()
		+ "\n";
//		System.out.println(strBoardMessage);
		sendMessageToBoth(strBoardMessage);
		
		// Sent to possible observers
		setChanged();
		// TODO: GET THE NOTIFY OBSERVERS RIGHT
	    notifyObservers(strBoardMessage);
//	    System.out.println("Kom ik hier?");
//		sendMessageToObservers(strBoardMessage);
	}
	
	/*@
       requires this.board.gameOver();
	 */

	/**
	 * Prints the result of the last game. <br>
	 */
	private void printResult() {
		
		Map<String, Integer> scores = board.calculateScore();
		Integer scoreBlack = scores.get("BLACK");
		Integer scoreWhite = scores.get("WHITE");
		
		// Add the prisoners
		for (int i = 0; i < NUMBER_PLAYERS; i++) {
			if(players[i].getMark().equals(Mark.OO)) {
				// Black
				scoreBlack = scoreBlack + players[i].getPrisonersTaken();
			} else if (players[i].getMark().equals(Mark.XX)) {
				scoreWhite = scoreWhite + players[i].getPrisonersTaken();
			}
		}
		
		String scoreString = "\nThe final score is: Black " + scoreBlack + " vs. White " + scoreWhite + "\n"; 
		System.out.println(scoreString);
		sendMessageToBoth(scoreString);
		notifyObservers(scoreString);
		
	}

	private void printRules() {
		// TODO: PRINT RULES: Misschien handig om te implementeren
	}


	private void sendMessageToBoth(String strBoardMessage) {
		players[0].sentMessage(strBoardMessage);
		//clientHandlerP1.sendMessageToClient(strBoardMessage);
//		clientHandlerP2.sendMessageToClient(strBoardMessage);
		players[1].sentMessage(strBoardMessage);
	}

	// LEGAL MOVE CHECKER AND APPLY RULES OF GAME!!! --------------------------------
	public boolean checkLegalMove(int xCo, int yCo) {
		boolean validMove = false;
		int indexField = board.index(xCo - 1, yCo - 1);
		validMove = checkLegalMove(indexField);
		return validMove; 	
	}
	
	public boolean checkLegalMove(int indexField) {
		boolean validMove = false;
		if(board.isField(indexField) && board.isEmptyField(indexField)) {
			validMove = checkRulesMove(board, indexField);
		} else if (indexField == -1) {
			validMove = true;
		};
		return validMove; 	
	}
	
	public boolean checkRulesMove(Board board, int keuze) {
		boolean isValidMove = false;
		
			//				// Check the field for liberties..
			//				if(!board.hasLiberty(keuze)) {
			//					System.out.println("Piece has no liberty (e.g. suicide), try another move... \n");
			//				} else {
			
			// Perform the action on a testboard, to check the rules
			Board testBoard = board.deepCopy();
			Mark currentMark = this.players[this.current].getMark();
			// Rule 7.1: PLACE (wiki): Set the field
			testBoard.setField(keuze, currentMark);
			
			// Rule 7.2: CAPTURE (wiki)
			// Implement: check liberties opponent and remove from board
			int prisonersTest = testBoard.checkLiberties(currentMark.other());

			// Rule 7.3: SELF-CAPTURE (wiki): 
			// Implement: check liberties self and remove from board
			int suicidesTest = testBoard.checkLiberties(currentMark);

			// Optional rule 7.4: PROHIBIT SUICIDE (wiki)
			// Implemented the check of step 3 in the if-else statement below

			// Rule 8.0: PROHIBIT REPETITION (wiki):
			// Check versus previous state of game
			boolean isRepetition = testBoard.hasKo(testBoard);

			int prisonersTaken = this.players[this.current].getPrisonersTaken();
			// Break the loop if all rules apply and apply to real board
			if (!isRepetition && suicidesTest == 0) {
				board.setField(keuze, currentMark);
				int prisoners = board.checkLiberties(currentMark.other());
				prisonersTaken = prisonersTaken + prisoners;
				System.out.println(prisoners + " new prisoners were made, resulting in a total of " + prisonersTaken + " prisoners. \n");
				board.checkLiberties(currentMark);
				isValidMove = true;
			} else {
				System.out.println("Move is not valid, due to Ko rule or suicides");
			}

			
		this.players[this.current].setPrisonersTaken(prisonersTaken);
		return isValidMove;
	}
	
	// GETTERS AND SETTERS
	public Board getCurrentBoard() {
		return this.board;
	}
}
