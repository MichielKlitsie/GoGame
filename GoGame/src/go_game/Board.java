package go_game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// TODO: Auto-generated Javadoc
/**
 * The board for the Go game. Module Software System final project.
 *
 * @author Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public class Board {
	// Change number to increase or decrease size of board
	/** The dim. */
	//	public static final int DIM = 9;
	public static int DIM = 9;

	/** The Constant NUMBERING. */
	/* An empty board
//	private static final String[] NUMBERING = {
//			"|———|———|———|———|———|———|———|———| A",
//			"| — | — | — | — | — | — | — | — |  ",
//			"|———|———|———|———|———|———|———|———| B",
//			"| — | — | — | — | — | — | — | — |  ",
//			"|———|———|———|———|———|———|———|———| C",
//			"| — | — | — | — | — | — | — | — |  ",
//			"|———|———|———|———|———|———|———|———| D",
//			"| — | — | — | — | — | — | — | — |  ",
//			"|———|———|———|———|———|———|———|———| E",
//			"| — | — | — | — | — | — | — | — |  ",
//			"|———|———|———|———|———|———|———|———| F",
//			"| — | — | — | — | — | — | — | — |  ",
//			"|———|———|———|———|———|———|———|———| G",
//			"| — | — | — | — | — | — | — | — |  ",
//			"|———|———|———|———|———|———|———|———| H",
//			"| — | — | — | — | — | — | — | — |  ",
//			"|———|———|———|———|———|———|———|———| I",
//			"1   2   3   4   5   6   7   8   9  "}; */
	private static final String[] NUMBERING = {		
			"|———|———|———|———|———|———|———|———| A",
			"|   |   |   |   |   |   |   |   |  ",
			"|———|———|———|———|———|———|———|———| B",
			"|   |   |   |   |   |   |   |   |  ",
			"|———|———|———|———|———|———|———|———| C",
			"|   |   |   |   |   |   |   |   |  ",
			"|———|———|———|———|———|———|———|———| D",
			"|   |   |   |   |   |   |   |   |  ",
			"|———|———|———|———|———|———|———|———| E",
			"|   |   |   |   |   |   |   |   |  ",
			"|———|———|———|———|———|———|———|———| F",
			"|   |   |   |   |   |   |   |   |  ",
			"|———|———|———|———|———|———|———|———| G",
			"|   |   |   |   |   |   |   |   |  ",
			"|———|———|———|———|———|———|———|———| H",
			"|   |   |   |   |   |   |   |   |  ",
			"|———|———|———|———|———|———|———|———| I",
	"1   2   3   4   5   6   7   8   9   "};

	/** The Constant LINE. */
	// The 'inbetween line', taken from the second line of the empty board
	private static final String LINE = NUMBERING[1]; 
	
	/** The Constant EMPTYLINEPIECE. */
	public static final String EMPTYLINEPIECE = "|   ";
	
	/** The Constant DELIM. */
	// The delimiter, empty in this case
	private static final String DELIM = "";
	
	/** The Constant DELIM_BETWEEN. */
	private static final String DELIM_BETWEEN = "   ";

	/**
	 * The DIM by DIM fields of the Go board. See NUMBERING for the
	 * coding of the fields.
	 */
	//@ private invariant fields.length == DIM*DIM;
	/*@ invariant (\forall int i; 0 <= i & i < DIM*DIM;
        getField(i) == Mark.EMPTY || getField(i) == Mark.XX || getField(i) == Mark.OO); */
	private Mark[] fields;

	// Keep track of previous boards for the Ko-Rule
	/** The previous boards. */
	//	public List<Board> previousBoards = new ArrayList<Board>();
	HashSet<String> previousBoards  = new HashSet<String>();
	
	/** The index last move. */
	private int indexLastMove;


	// -- Constructors -----------------------------------------------
	/**
	 * Creates an empty Go board.
	 *
	 * @param dim the dim
	 */
	//@ ensures (\forall int i; 0 <= i & i < DIM * DIM; this.getField(i) == Mark.EMPTY);
	public Board(int dim) {
		this.DIM = dim;

		fields = new Mark[DIM * DIM];
		for (int i = 0; i < fields.length; i++) {
			this.fields[i] = Mark.EMPTY;
		}
	}

	/**
	 * Creates a deep copy of this field.
	 *
	 * @return the board
	 */
	/*@ ensures \result != this;
        ensures (\forall int i; 0 <= i & i < DIM * DIM;
                                \result.getField(i) == this.getField(i));
      @*/
	public Board deepCopy() {

		Board deepBoard = new Board(DIM);
		for (int i = 0; i < DIM * DIM; i++) {
			deepBoard.setField(i, this.getField(i));
		}
		return deepBoard;
	}


	// Calculate the index of the field ------------------------------------------
	/**
	 * Calculates the index in the linear array of fields from a (int row, int col)
	 * pair.
	 *
	 * @param row the row
	 * @param col the col
	 * @return the index belonging to the (row,col)-field
	 */
	//@ requires 0 <= row & row < DIM;
	//@ requires 0 <= col & col < DIM;
	/*@pure*/
	public int index(int row, int col) {
		assert 0 <= row & row < DIM && 0 <= col & col < DIM;
		int index = (row * DIM) + col;
		return index;
	}

	/**
	 * Gets the row.
	 *
	 * @param index the index
	 * @return the row
	 */
	public int getRow(int index) {
		return (index - getCol(index)) / DIM;
	}
	
	/**
	 * Gets the col.
	 *
	 * @param index the index
	 * @return the col
	 */
	public int getCol(int index) {
		return index % DIM;
	}
	//	/**
	//	 * Calculates the index in the linear array of fields from a (String row, int col)
	//	 * pair. The row is indicated with a letter and the column with a number.
	//	 * @return the index belonging to the (row,col)-field
	//	 */
	//	//@ requires 0 <= row & row < DIM;
	//	//@ requires 0 <= col & col < DIM;
	//	/*@pure*/
	//	public int index(String row, int col) {
	//		// Convert the letter to a number and input above (taken into account ASCII positions)
	//		String rowLowerCase = row.toLowerCase();
	//		char[] ch = rowLowerCase.toCharArray();
	//		int rowInt = ch[0] - 'a' + 1;
	//		int colInt = col;		
	//		
	//		int index = index(rowInt, colInt);
	//		return index;
	//	}

	// Check validity of index a field from the board ------------------------------------------
	/**
	 * Returns true if index is a valid index of a field on the board.
	 *
	 * @param index the index
	 * @return true if 0 <= index < DIM*DIM
	 */
	//@ ensures \result == (0 <= index && index < DIM * DIM);
	/*@pure*/
	public boolean isField(int index) {
		if (index >= (DIM * DIM) || index < 0) {
			return false;
		} else { 
			return true;
		}
	}

	/**
	 * Returns true of the (int row, int col) pair refers to a valid field on the student.
	 *
	 * @param row the row
	 * @param col the col
	 * @return true if 0 <= row < DIM && 0 <= col < DIM
	 */
	//@ ensures \result == (0 <= row && row < DIM && 0 <= col && col < DIM);
	/*@pure*/
	public boolean isField(int row, int col) {
		return this.isField(index(row, col));
	}

	//	/**
	//	 * Returns true of the (String row, int col) pair refers to a valid field on the student.
	//	 *
	//	 * @return true if 0 <= row < DIM && 0 <= col < DIM
	//	 */
	//	//@ ensures \result == (0 <= row && row < DIM && 0 <= col && col < DIM);
	//	/*@pure*/
	//	public boolean isField(String row, int col) {
	//		return this.isField(index(row, col));
	//	}

	// Return the field of the inputted index ------------------------------------------
	/**
	 * Returns the content of the field i.
	 *
	 * @param i
	 *            the number of the field (see NUMBERING)
	 * @return the mark on the field
	 */
	//@ requires this.isField(i);
	//@ ensures \result == Mark.EMPTY || \result == Mark.XX || \result == Mark.OO;
	/*@pure*/
	public Mark getField(int i) {
		
		return this.fields[i];
	}

	/**
	 * Returns the content of the field referred to by the (row,col) pair.
	 *
	 * @param row
	 *            the row of the field
	 * @param col
	 *            the column of the field
	 * @return the mark on the field
	 */
	//@ requires this.isField(row,col);
	//@ ensures \result == Mark.EMPTY || \result == Mark.XX || \result == Mark.OO;
	/*@pure*/
	public Mark getField(int row, int col) {
		
		return this.fields[index(row, col)];
	}

	/**
	 * Returns true if the field i is empty.
	 *
	 * @param i
	 *            the index of the field (see NUMBERING)
	 * @return true if the field is empty
	 */
	//@ requires this.isField(i);
	//@ ensures \result == (this.getField(i) == Mark.EMPTY);
	/*@pure*/
	public boolean isEmptyField(int i) {
		
		return this.fields[i] == Mark.EMPTY;
	}

	/**
	 * Returns true if the field referred to by the (row,col) pair it empty.
	 *
	 * @param row
	 *            the row of the field
	 * @param col
	 *            the column of the field
	 * @return true if the field is empty
	 */
	//@ requires this.isField(row,col);
	//@ ensures \result == (this.getField(row,col) == Mark.EMPTY);
	/*@pure*/
	public boolean isEmptyField(int row, int col) {
		
		return this.isEmptyField(index(row, col));
	}

	// BOARD CHECKS ---------------------------------------------------------------------------------------------
	//	/**
	//	 * Tests if the whole student is full.
	//	 *
	//	 * @return true if all fields are occupied
	//	 */
	//	//@ ensures \result == (\forall int i; i <= 0 & i < DIM * DIM; this.getField(i) != Mark.EMPTY);
	//	/*@pure*/
	//	public boolean isFull() {
	//		
	//		int i = 0;
	//		while (!isEmptyField(i)) {
	//			if (i == this.fields.length - 1) {
	//				return true;
	//			}
	//			i++;
	//		}
	//		return false;
	//
	//	}

	//	/**
	//	 * Returns true if the game is over. The game is over when there is a winner
	//	 * or the whole student is full.
	//	 *
	//	 * @return true if the game is over
	//	 */
	//	//@ ensures \result == this.isFull() || this.hasWinner();
	//	/*@pure*/
	//	public boolean gameOver() {
	//		
	////		return this.isFull() || this.hasWinner();
	//		return false;
	//	}


	// GAME RULES ---------------------------------------------------------------------------------------------
	// GO RULES!!!
	/**
	 * Checks whether a made move results in a stone without a liberty.
	 *
	 * @param index the index
	 * @param mark the mark
	 * @return true if there is still a liberty left
	 */
	public boolean hasLiberty(int index, Mark mark) {
		// Get the possible adjecent fields 
		List <Integer> adjecentFields = getAdjecentFields(index);
		// Check if fields are empty
		Iterator<Integer> adjecentFieldsIterator = adjecentFields.iterator();
		List<Boolean> isLiberty = new ArrayList<Boolean>();
		boolean isFreeField = true;
		// And keep track of the marks
		List<Mark> adjecentMarks = new ArrayList<Mark>();

		while (adjecentFieldsIterator.hasNext()) {
			Integer current = adjecentFieldsIterator.next();
			isLiberty.add(isEmptyField(current));
			adjecentMarks.add(getField(current));
		}

		// Additional check for marks..
		// Implement for surround free fields check for other marks
		if (!isLiberty.contains(true) && !adjecentMarks.contains(mark)) {
			isFreeField = false;
		}

		return isFreeField;
	}

	/**
	 * Obtains a list of the adjecent fields, if they exist.
	 *
	 * @param index the index
	 * @return List<Integer> adjecentFields;
	 */
	public List<Integer> getAdjecentFields(int index) {
		List<Integer> adjecentFieldsList = new ArrayList<Integer>();

		//Upper
		if ((index - DIM) > 0) {
			adjecentFieldsList.add(index - DIM);
		}
		//Left
		if (!((index % DIM) ==  0)) {
			adjecentFieldsList.add(index - 1);
		}
		//Right
		if (!(((index + 1) % DIM) == 0)) {
			adjecentFieldsList.add(index + 1);
		}
		//Under
		if ((index + DIM) < (DIM * DIM)) {
			adjecentFieldsList.add(index + DIM);
		}

		return adjecentFieldsList;
	}

	/**
	 * /**
	 * Check the liberties of every field with the supplied mark.
	 *
	 * @param mark the mark
	 * @return prisoners.size()
	 */
	public int checkLiberties(Mark mark) {
		// For the fields...
		List <Integer> prisoners = new ArrayList<Integer>();
		for (int i = 0; i < DIM * DIM; i++) {
			// ... that are not empty and are of mark supplied...
			if (!isEmptyField(i) && getField(i) == mark) {	
				// Check the liberties of that field...
				boolean isFreeField = hasLiberty(i, mark);

				// ... and if there are no liberties... 
				if (!isFreeField) {
					//					System.out.println("Field " + (i + 1) + " has no liberties.");
					//					System.out.println("Field " + (i + 1) + " is taken prisoner");
					prisoners.add(i);
					setField(i, Mark.EMPTY);
				}
			}
		}
		return prisoners.size();
	}

	//	/**
	//	 * /**
	//	 * Check the liberties of a single field with the supplied mark
	//	 * @param mark
	//	 * @return prisoners.size()
	//	 */
	//	public int checkLiberties(int index, Mark mark) {
	//		// For the fields...
	//		List <Integer> prisoners = new ArrayList<Integer>();
	//		for (int i = 0; i < DIM * DIM; i++) {
	//			// ... that are not empty and are of mark supplied...
	//			if (!isEmptyField(i) && getField(i) == mark) {	
	//				// Check the liberties of that field...
	//				boolean isFreeField = hasLiberty(i, mark);
	//
	//				// ... and if there are no liberties... 
	//				if (!isFreeField) {
	////					System.out.println("Field " + (i + 1) + " has no liberties.");
	////					System.out.println("Field " + (i + 1) + " is taken prisoner");
	//					prisoners.add(i);
	//					setField(i, Mark.EMPTY);
	//				}
	//			}
	//		}
	//		return prisoners.size();
	//	}

	/**
	 * Check suicide.
	 *
	 * @param mark the mark
	 * @return true, if successful
	 */
	public boolean checkSuicide(Mark mark) {
		return checkLiberties(mark) < 1;
	}

	/**
	 * Checks the Ko-rule, e.g. if a previous position of stones is repeated 
	 *
	 * @param board the board
	 * @return true if there is still a liberty left
	 */
	public boolean hasKo(Board board) {
		// Check is a previous position is repeated
		return previousBoards.contains(createStringRepresentationBoard(board)); 

	}

	// CHECKS FOR WINNER ---------------------------------------------------------------------------------------------

	/**
	 * Calculate score.
	 *
	 * @return the map
	 */
	public Map<String, Integer> calculateScore() {
		// Afhankelijk van type scoring, maar volgens regels is territory scoring 

		// Aantal vakjes van territory
		Mark mark = Mark.BB;
		List<List<Integer>> areas = calculateAreas(mark);

		// Who's area is that whos mark
		List<Mark> areasMarks = new ArrayList<Mark>();
		int scoreWhite = 0;
		int scoreBlack = 0;
		for (int i = 0; i < areas.size(); i++) {
			System.out.println("Area " + i + " is...");
			boolean areaTouchesWhite = false;
			boolean areaTouchesBlack = false;
			
			// Check if area for mark of bounderies
			List<Integer> area = areas.get(i);
			arealoop:
				for (int j = 0; j < area.size(); j++) {
					Integer emptyFieldIndex = area.get(j);
					List<Integer> b = getAdjecentFields(emptyFieldIndex);
					for (int z = 0; z < b.size(); z++) {
						if(getField((b.get(z))).equals(Mark.BB)) {areaTouchesBlack = true; }
						if(getField((b.get(z))).equals(Mark.WW)) {areaTouchesWhite = true; }
						if(areaTouchesWhite && areaTouchesBlack) {break arealoop;}
					}
				}
			
			// Add scores
			if(areaTouchesWhite && areaTouchesBlack) {
				areasMarks.add(Mark.EMPTY); 
				System.out.println("...of nobody!"); 
			} else if(areaTouchesWhite && !areaTouchesBlack) {
				areasMarks.add(Mark.WW); 
				scoreWhite = scoreWhite + area.size();
				System.out.println("...WHITE!");
			} else if(!areaTouchesWhite && areaTouchesBlack) {
				areasMarks.add(Mark.BB); 
				scoreBlack = scoreBlack + area.size();
				System.out.println("...BLACK!");
			} else {
				System.out.println("Er gaat iets mis");
			}
		}
		System.out.println("Score before prisoners: White " + scoreWhite + " vs. Black " + scoreBlack);

		// create a hashMap of the scores
		Map<String, Integer> scores = new HashMap<String, Integer>();
		scores.put("BLACK", scoreBlack);
		scores.put("WHITE", scoreWhite);
		
		return scores;
		
	}


	/**
	 * Calculate areas.
	 *
	 * @param mark the mark
	 * @return the list
	 */
	public List<List<Integer>> calculateAreas(Mark mark) {
		// Calculate chains
		List<List<Integer>> chains = calculateChainsByStreams(mark);

		// Bereken alle lege hokjes
		List<Integer> allFieldIndexes = IntStream.range(0, DIM * DIM).boxed().collect(Collectors.toList());
		List<Integer> filteredIndices = allFieldIndexes.stream().filter(f -> getField(f).equals(Mark.EMPTY)).collect(Collectors.toList());

		// Bereken het aantal ingesloten delen
		List<List<Integer>> areas = new ArrayList<List<Integer>>();

		while(filteredIndices.size() > 1) {
			List<Integer> newArea = new ArrayList<Integer>();
			List<Integer> newChainFilled = recursiveChainMaker(newArea,filteredIndices.get(0), Mark.EMPTY);
			areas.add(newArea);
			filteredIndices.removeAll(newArea);
		}

		return areas;
	}

	/**
	 * Calculate chains by streams.
	 *
	 * @param mark the mark
	 * @return the list
	 */
	public List<List<Integer>> calculateChainsByStreams(Mark mark) {
		// Create a list from 1 to DIM * DIM
		List<Integer> allFieldIndexes = IntStream.range(0, DIM * DIM).boxed().collect(Collectors.toList());
		List<Integer> filteredIndices = allFieldIndexes.stream().filter(f -> getField(f).equals(mark)).collect(Collectors.toList());
//		System.out.println("Filtered list of size " + filteredIndices.size());

		// Filter connected marks
		List<List<Integer>> chains = new ArrayList<List<Integer>>();
		while(filteredIndices.size() > 0) {
			List<Integer> newChain = new ArrayList<Integer>();
			List<Integer> newChainFilled = recursiveChainMaker(newChain,filteredIndices.get(0), mark);
			chains.add(newChainFilled);
			filteredIndices.removeAll(newChainFilled);
		}

		return chains;
	}

	/**
	 * Recursive chain maker.
	 *
	 * @param previousChain the previous chain
	 * @param previousIndex the previous index
	 * @param mark the mark
	 * @return the list
	 */
	public List<Integer> recursiveChainMaker(List<Integer> previousChain, int previousIndex, Mark mark) {
		// Add to the List
		if (!previousChain.contains(previousIndex)) {
			previousChain.add(previousIndex);
		}
		// Get the new candidates
		List<Integer> sameMarkAdjecentFields = getSameMarkAdjecentFields(previousIndex,mark);
		for(Integer newIndex : sameMarkAdjecentFields) {
			// Recursive step
			if (!previousChain.contains(newIndex)) {
				previousChain = recursiveChainMaker(previousChain, newIndex, mark);
			}
		}
		return previousChain;
	}
	
	/**
	 * Gets the same mark adjecent fields.
	 *
	 * @param fieldIndex the field index
	 * @param mark the mark
	 * @return the same mark adjecent fields
	 */
	public List<Integer> getSameMarkAdjecentFields(int fieldIndex, Mark mark) {
		List<Integer> adjecentFields  = getAdjecentFields(fieldIndex);
		List<Integer> sameMarkAdjecentFields = adjecentFields.stream()
				.filter(f -> getField(f) == mark)
				.collect(Collectors.toList());
		return sameMarkAdjecentFields;
	}

	
	/**
	 * Removes the dead stones.
	 */
	public void removeDeadStones() {
		// At the end of the game, the stones that are still on the board, but unable to avoid capture become prisoners
	}
	
	// DRAWING OF THE BOARD ---------------------------------------------------------------------------------------------
	/**
	 * Returns a String representation of this board. In addition to the current
	 * situation, the String also shows the numbering of the fields.
	 *
	 * @return the game situation as String
	 */
	public String toStringOnCommandLine() {
		// Initialize empty string
		String s = "";

		// Fill each row
		for (int i = 0; i < DIM; i++) {
			String row = "";

			// Fill each column
			for (int j = 0; j < DIM; j++) {
				// Append the previous row with a field and "---" or the letter if last
				row = row + getField(i, j).toStringNiceInclHint();
				if (j < DIM - 1) {
					// Draw line between each field
					row = row + "---";
				} else {
					// End the row with the corresponding row letter
					//					String emptyBoardRow = NUMBERING[i * 2];
					//					String letterRow = emptyBoardRow.substring(emptyBoardRow.length() - 1);

					String letterRow = String.valueOf((char) (i + 65));
					row = row + " " + letterRow;
				}
			}
			// Append previous string with the new row + optional delimiter + an line from the full empty board
			//			s = s + row + DELIM_BETWEEN + NUMBERING[i * 2];
			s = s + row;

			// Between the fields an empty line and a line from the full empty board
			if (i < DIM - 1) {
				String emptyLine = String.join("", Collections.nCopies(DIM - 1, EMPTYLINEPIECE));
				s = s + "\n" + emptyLine + "|\n"; // + DELIM_BETWEEN + NUMBERING[i * 2 + 1] + "\n";
			}
		}

		//		String lastLine = NUMBERING[(2 * (DIM - 1)) + 1];
		String lastLine = "";
		for (int z = 1; z <= DIM; z++) {
			if (z < 10) {
				lastLine = lastLine + z + "   ";
			} else {
				lastLine = lastLine + z + "  ";
			}
		}
		s = s + "\n" + lastLine + "\n"; // + DELIM_BETWEEN + lastLine;
		return s;
	}
	

	/**
	 * Make a string representation for the check in the HashMap for the 'SuperKo'.
	 *
	 * @param board the board
	 * @return String stringBoard
	 */
	public String createStringRepresentationBoard(Board board) {
		String stringBoard = "";
		for (int i = 0; i < DIM * DIM; i++) {
			stringBoard = stringBoard + board.getField(i).toStringForProtocol();
		}
		//		System.out.println(stringBoard);

		return stringBoard;
	}


	// GAME RESET: CLEAR ALL FIELDS ---------------------------------------------------------------------------------------------
	/**
	 * Empties all fields of this student (i.e., let them refer to the value
	 * Mark.EMPTY).
	 */
	/*@ ensures (\forall int i; 0 <= i & i < DIM * DIM;
                                this.getField(i) == Mark.EMPTY); @*/
	public void reset() {
		
		for (int i = 0; i < fields.length; i++) {
			this.fields[i] = Mark.EMPTY;
		}
	}


	// AFTER A MOVE: SET THE CHOSEN FIELD ---------------------------------------------------------------------------------------------
	/**
	 * Sets the content of field i to the mark m.
	 *
	 * @param i
	 *            the field number (see NUMBERING)
	 * @param m
	 *            the mark to be placed
	 */
	//@ requires this.isField(i);
	//@ ensures this.getField(i) == m;
	public void setField(int i, Mark m) {
		
		this.fields[i] = m;
		indexLastMove = i;
	}

	/**
	 * Sets the content of the field represented by the (row,col) pair to the
	 * mark m.
	 *
	 * @param row
	 *            the field's row
	 * @param col
	 *            the field's column
	 * @param m
	 *            the mark to be placed
	 */
	//@ requires this.isField(row,col);
	//@ ensures this.getField(row,col) == m;
	public void setField(int row, int col, Mark m) {
		
		this.fields[index(row, col)] = m;
		indexLastMove = index(row, col);
	}

	/**
	 * Adds the previous board.
	 *
	 * @param board the board
	 */
	public void addPreviousBoard(Board board) {
		// Make a string representatie
		String stringBoard = createStringRepresentationBoard(board);
		// Add to hashset
		previousBoards.add(stringBoard);
	}

	/**
	 * Gets the index last move.
	 *
	 * @return the index last move
	 */
	public int getIndexLastMove() {
		return this.indexLastMove;
	}

	/**
	 * Gets the dimensions.
	 *
	 * @return the dimensions
	 */
	public int getDimensions() {
		return DIM;
	}
}
