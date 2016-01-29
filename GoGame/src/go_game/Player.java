package go_game;

import java.util.Iterator;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Abstract class for keeping a player in the Go game. 
 * 
 * 
 * @author Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public abstract class Player {

	// -- Instance variables -----------------------------------------

	/** The name. */
	private String name;
	
	/** The mark. */
	private Mark mark;
	
	/** The prisoners taken. */
	private int prisonersTaken;
	
	/** The is timed out. */
	protected boolean isTimedOut;
	

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
	 * @param name the name
	 * @param mark the mark
	 */
	public Player(String name, Mark mark) {
		this.name = name;
		this.mark = mark;
		this.prisonersTaken = 0;
		this.isTimedOut = false;
	}

	// -- Queries ----------------------------------------------------

	/**
	 * Returns the name of the player.
	 *
	 * @return the name
	 */
	/*@ pure */ public String getName() {
		return name;
	}

	/**
	 * Returns the mark of the player.
	 *
	 * @return the mark
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
	 * @param board            the current board
	 * @return the int
	 */
	public int makeMove(Board board) {
		// Step 1: Determine the best move
		// determineMove for a Human: Wait for input
		// determineMove for a Computer: Determine move based on implemented strategy
		int choice = determineMove(board);
		return choice;
	}

	/**
	 * Sent message.
	 *
	 * @param msg the msg
	 */
	public abstract void sentMessage(String msg);
	
	/**
	 * Gets the prisoners taken.
	 *
	 * @return the prisoners taken
	 */
	// GETTERS AND SETTERS
		public int getPrisonersTaken() {
			return prisonersTaken;
		}

		/**
		 * Sets the prisoners taken.
		 *
		 * @param prisonersTaken the new prisoners taken
		 */
		public void setPrisonersTaken(int prisonersTaken) {
			this.prisonersTaken = prisonersTaken;
		}

		/**
		 * Gets the checks if is timed out.
		 *
		 * @return the checks if is timed out
		 */
		public boolean getIsTimedOut() {
			return isTimedOut;
		}

		/**
		 * Sets the timed out.
		 *
		 * @param isTimedOut the new timed out
		 */
		public void setTimedOut(boolean isTimedOut) {
			this.isTimedOut = isTimedOut;
		}


}
