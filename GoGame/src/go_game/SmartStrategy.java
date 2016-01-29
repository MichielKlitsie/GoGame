/**
 * 
 */
package go_game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// TODO: Auto-generated Javadoc
/**
 * The Class SmartStrategy.
 *
 * @author michiel.klitsie
 */
public class SmartStrategy implements Strategy {

	/** The winning move. */
	//TODO: BUILD THE SMART STRATEGY
	private int winningMove;
	
	/** The name strategy. */
	private String nameStrategy;
	
	/** The best move. */
	private int bestMove;
	
	/** The best qual. */
	private int bestQual;
	
	/** The current field. */
	private int currentField;
	

	/**
	 * The Enum QUAL.
	 */
	// Rating the quality of the moves
	public enum QUAL{
		
		/** The losing. */
		LOSING(0),
		
		/** The neutral. */
		NEUTRAL(1),
		
		/** The winning. */
		WINNING(2);
		
		/** The value. */
		private int value;
		
		/**
		 * Instantiates a new qual.
		 *
		 * @param newValue the new value
		 */
		QUAL(int newValue) {
			value = newValue;
		}
		
		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public int getValue() { return value; }
	}

	/**
	 * Instantiates a new smart strategy.
	 */
	public SmartStrategy() {
		this.nameStrategy = "Smart strategy";
		this.winningMove = 0;
	}

	/* (non-Javadoc)
	 * @see go_game.Strategy#getName()
	 */
	@Override
	public String getName() {
		return nameStrategy;
	}

	/* (non-Javadoc)
	 * @see go_game.Strategy#determineMove(go_game.Board, go_game.Mark)
	 */
	@Override
	public int determineMove(Board b, Mark m) {
		// Create a list from 1 to DIM * DIM
		Board deepCopyBoard = b.deepCopy();
		List<Integer> listEmptyFields = getEmptyFields(deepCopyBoard);
		Integer bestOpeningMove = determineBestOpeningMove(deepCopyBoard, m, listEmptyFields);

		this.bestMove = 0; // Field
		this.bestQual = QUAL.LOSING.getValue(); // Quality

		return 0;
	}

	/**
	 * Determine best opening move.
	 *
	 * @param b the b
	 * @param m the m
	 * @param listEmptyFields the list empty fields
	 * @return the int
	 */
	public int determineBestOpeningMove(Board b, Mark m, List<Integer> listEmptyFields) {
		// Step 1: Determine if opening moves are still relevant
		// Step 1a: Opening: FIRST, get one of the corner positions
		List<Integer> listOpenStarPointsLineFour = checkOpenStarPoints(b, listEmptyFields, 4);
		List<Integer> listOpenStarPointsLineThree = checkOpenStarPoints(b, listEmptyFields, 3);
		// Step 1b: Opening: SECOND, get one of the side positions
		// Step 1c: Opening: THIRD, get the center position
		return 0;
	}


	// Step 2: PLAYS
	/**
	 * Determine best playing move.
	 *
	 * @param b the b
	 * @param m the m
	 * @return the int
	 */
	//	Plays are usually on the third or fourth line—the second makes too little territory, while the fifth is too easily undermined by a play on the third. 
	public int determineBestPlayingMove(Board b, Mark m) {
		if (bestQual == QUAL.WINNING.getValue()) {
			return this.bestMove;
		} else {
			if (checkDirectWin(b, m) == true) {
				this.bestQual = QUAL.WINNING.getValue();
				return this.currentField;
			} else if (checkDirectWin(b, m.other()) == true) {
				this.bestQual = QUAL.LOSING.getValue();
			} else {
				this.bestQual = QUAL.NEUTRAL.getValue();
			};

			this.currentField = this.currentField + 1; 
			b.setField(this.currentField, m);
//			determineBestOpeningMove(b, m);
		}
		return 0;
	}

	/**
	 * Check direct win.
	 *
	 * @param b the b
	 * @param m the m
	 * @return true, if successful
	 */
	public boolean checkDirectWin(Board b, Mark m) {
		return false;
	}

	/**
	 * Gets the empty fields.
	 *
	 * @param b the b
	 * @return the empty fields
	 */
	// Get the empty field on the board
	public List<Integer> getEmptyFields(Board b) {
		List<Integer> allFieldIndexes = IntStream.range(0, (b.DIM * b.DIM)).boxed().collect(Collectors.toList());
		List<Integer> allEmptyFieldIndexes = allFieldIndexes.stream().filter(p -> b.isEmptyField(p)).collect(Collectors.toList());
		return allEmptyFieldIndexes; 
	}

	// Opening tactics
	/**
	 * Check open star points.
	 *
	 * @param b the b
	 * @param listEmptyFields the list empty fields
	 * @param line the line
	 * @return the list
	 */
	// FUSEKI / OPENING MOVES
	public List<Integer> checkOpenStarPoints(Board b, List<Integer> listEmptyFields, int line) {
		//4-4 star points in the corners
		//		The first moves are usually played on or near the 4-4 star points in the corners, because in those places it is easiest to gain territory or influence. (In order to be totally secure alone, a corner stone must be placed on the 3-3 point. However, if a stone is placed at a 4-4 point and the opponent invades, the first player can build a surrounding wall as the second (invader) is forming a live group, thus exerting strong influence on a large area.) After that, standard sequences (Joseki) can be used to develop corner positions, and extensions along the side can be made. Usually, the center area is kept empty the longest. Plays are usually on the third or fourth line—the second makes too little territory, while the fifth is too easily undermined by a play on the third. A play on the fourth line is directed more towards influence to the center, a play on the third line more towards making territory along the side.
		List<Integer> listStarPointsLineFour = Arrays.asList(b.index(line-1, line-1), b.index(line-1, b.DIM - line), b.index(b.DIM - line, line-1), b.index(b.DIM - line, b.DIM - line)); 
		List<Integer> listOpenStarPoints = new ArrayList<Integer>();
		// Variable names edited for readability
		for (Integer item : listStarPointsLineFour) {
		    if (listEmptyFields.contains(item)) {
		    	listOpenStarPoints.add(item);
		    }
		}
		return listOpenStarPoints;
	}

	/**
	 * Builds the joseki.
	 *
	 * @param b the b
	 */
	private void buildJoseki(Board b) {
		//		After that, standard sequences (Joseki) can be used to develop corner positions, and extensions along the side can be made. 
	}

	// OFFENCE TACTICS ------------------------------------
	/**
	 * Builds the group.
	 *
	 * @param b the b
	 */
	// Connection & Separation
	private void buildGroup(Board b) {
		//		Connecting a group with one eye to another one-eyed group makes them live together. Connecting individual stones into a single group results in an increase of liberties; 
	}

	/**
	 * Cut other group.
	 *
	 * @param b the b
	 */
	private void cutOtherGroup(Board b) {
		// Prevent other player from building groups
	}

	// CLASSIFICTION OF GROUPS
	/**
	 * Checking eyes.
	 *
	 * @param b the b
	 */
	// ALIVE or DEAD or UNSETTLED
	private void checkingEyes(Board b) {
		// A single empty space inside a group is called an eye
	}
}
