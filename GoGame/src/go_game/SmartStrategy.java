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

/**
 * @author michiel.klitsie
 *
 */
public class SmartStrategy implements Strategy {

	//TODO: BUILD THE SMART STRATEGY
	private int winningMove;
	private String nameStrategy;
	private int bestMove;
	private int bestQual;
	private int currentField;
	

	// Rating the quality of the moves
	public enum QUAL{
		LOSING(0),
		NEUTRAL(1),
		WINNING(2);
		private int value;
		QUAL(int newValue) {
			value = newValue;
		}
		public int getValue() { return value; }
	}

	/**
	 * 
	 */
	public SmartStrategy() {
		this.nameStrategy = "Smart";
		this.winningMove = 0;
	}

	@Override
	public String getName() {
		return nameStrategy;
	}

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

	public boolean checkDirectWin(Board b, Mark m) {
		return false;
	}

	// Get the empty field on the board
	public List<Integer> getEmptyFields(Board b) {
		List<Integer> allFieldIndexes = IntStream.range(0, (b.DIM * b.DIM)).boxed().collect(Collectors.toList());
		List<Integer> allEmptyFieldIndexes = allFieldIndexes.stream().filter(p -> b.isEmptyField(p)).collect(Collectors.toList());
		return allEmptyFieldIndexes; 
	}

	// Opening tactics
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

	private void buildJoseki(Board b) {
		//		After that, standard sequences (Joseki) can be used to develop corner positions, and extensions along the side can be made. 
	}

	// OFFENCE TACTICS ------------------------------------
	// Connection & Separation
	private void buildGroup(Board b) {
		//		Connecting a group with one eye to another one-eyed group makes them live together. Connecting individual stones into a single group results in an increase of liberties; 
	}

	private void cutOtherGroup(Board b) {
		// Prevent other player from building groups
	}

	// CLASSIFICTION OF GROUPS
	// ALIVE or DEAD or UNSETTLED
	private void checkingEyes(Board b) {
		// A single empty space inside a group is called an eye
	}
}
