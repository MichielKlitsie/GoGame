package go_game;

import java.util.Iterator;
import java.util.List;

/**
 * Abstract class for keeping a player in the Go game. 
 * 
 * 
 * @author Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public abstract class Player {

	// -- Instance variables -----------------------------------------

	private String name;
	private Mark mark;
	
	private int prisonersTaken;
	

	// -- Constructors -----------------------------------------------

	/*@
       requires name != null;
       requires mark == Mark.XX || mark== Mark.OO;
       ensures this.getName() == name;
       ensures this.getMark() == mark;
	 */
	/**
	 * Creates a new Player object.
	 * 
	 */
	public Player(String name, Mark mark) {
		this.name = name;
		this.mark = mark;
		this.prisonersTaken = 0;
	}

	// -- Queries ----------------------------------------------------

	/**
	 * Returns the name of the player.
	 */
	/*@ pure */ public String getName() {
		return name;
	}

	/**
	 * Returns the mark of the player.
	 */
	/*@ pure */ public Mark getMark() {
		return mark;
	}

	/*@
       requires board != null & !board.isFull();
       ensures board.isField(\result) & board.isEmptyField(\result);

	 */
	/**
	 * Determines the field for the next move.
	 * 
	 * @param board
	 *            the current game board
	 * @return the player's choice
	 */
	public abstract int determineMove(Board board);

	// -- Commands ---------------------------------------------------

	/*@
       requires board != null & !board.isFull();
	 */
	/**
	 * Makes a move on the board. <br>
	 * 
	 * @param board
	 *            the current board
	 */
	public int makeMove(Board board) {
		// Step 1: Determine the best move
		// determineMove for a Human: Wait for input
		// determineMove for a Computer: Determine move based on implemented strategy
		int choice = determineMove(board);
		return choice;
	}

	public abstract void sentMessage(String msg);
	
	// GETTERS AND SETTERS
		public int getPrisonersTaken() {
			return prisonersTaken;
		}

		public void setPrisonersTaken(int prisonersTaken) {
			this.prisonersTaken = prisonersTaken;
		}

	

}
