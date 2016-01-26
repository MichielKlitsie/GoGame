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

/**
 * The board for the Go game. Module Software System final project.
 *
 * @author Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public class Board {
	// Change number to increase or decrease size of board
	//	public static final int DIM = 9;
	public static int DIM = 9;

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

	// The 'inbetween line', taken from the second line of the empty board
	private static final String LINE = NUMBERING[1]; 
	private static final String EMPTYLINEPIECE = "|   ";
	// The delimiter, empty in this case
	private static final String DELIM = "";
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
	//	public List<Board> previousBoards = new ArrayList<Board>();
	HashSet<String> previousBoards  = new HashSet<String>();
	private int indexLastMove;


	// -- Constructors -----------------------------------------------
	/**
	 * Creates an empty Go board.
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
	// TIC TAC TOE RULES!!!
	//	/**
	//	 * Checks whether there is a row which is full and only contains the mark
	//	 * m.
	//	 *
	//	 * @param m
	//	 *            the mark of interest
	//	 * @return true if there is a row controlled by m
	//	 */
	//	/*@ pure */
	//	public boolean hasRow(Mark m) {
	//		
	//		for (int i = 0; i < DIM; i++) {  // rows
	//			for (int j = 0; j < DIM; j++) { // cols
	//				if (m != this.getField(i,j)) {
	//					break;
	//				} else if (j == DIM - 1) {
	//					return true;
	//				}
	//			}
	//		}
	//		return false;
	//	}
	//
	//	/**
	//	 * Checks whether there is a column which is full and only contains the mark
	//	 * m.
	//	 *
	//	 * @param m
	//	 *            the mark of interest
	//	 * @return true if there is a column controlled by m
	//	 */
	//	/*@ pure */
	//	public boolean hasColumn(Mark m) {
	//		
	//		for (int i = 0; i < DIM; i++) {  // rows
	//			for (int j = 0; j < DIM; j++) { // cols
	//				if (m != this.getField(j,i)) { // SUBTLE CHANGE I AND J SWITCHED!!!
	//					break;
	//				} else if (j == DIM - 1) {
	//					return true;
	//				}
	//			}
	//		}
	//		return false;
	//	}
	//
	//	/**
	//	 * Checks whether there is a diagonal which is full and only contains the
	//	 * mark m.
	//	 *
	//	 * @param m
	//	 *            the mark of interest
	//	 * @return true if there is a diagonal controlled by m
	//	 */
	//	/*@ pure */
	//	public boolean hasDiagonal(Mark m) {
	//		
	//		if (this.getField(0) == m && 
	//				this.getField(4) == m && 
	//				this.getField(8) == m) { // Diagonal 1
	//			return true;
	//		} else if (this.getField(2) == m && 
	//				this.getField(4) == m && 
	//				this.getField(6) == m) { // Diagonal 2
	//			return true;			
	//		}
	//		return false;
	//	}

	// GO RULES!!!
	/**
	 * Checks whether a made move results in a stone without a liberty
	 *
	 * @param index
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
	 * @param index
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
	 * Check the liberties of every field with the supplied mark
	 * @param mark
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
	 * 
	 */
	public boolean checkSuicide(Mark mark) {
		return checkLiberties(mark) < 1;
	}

	/**
	 * Checks the Ko-rule, e.g. if a previous position of stones is repeated 
	 *
	 * @param m
	 *            the mark of interest
	 * @return true if there is still a liberty left
	 */
	public boolean hasKo(Board board) {
		// Check is a previous position is repeated
		return previousBoards.contains(createStringRepresentationBoard(board)); 

	}

	// CHECKS FOR WINNER ---------------------------------------------------------------------------------------------

	public Map<String, Integer> calculateScore() {
		// Afhankelijk van type scoring, maar volgens regels is territory scoring 

		// Aantal vakjes van territory
		Mark mark = Mark.OO;
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
						if(getField((b.get(z))).equals(Mark.OO)) {areaTouchesBlack = true; }
						if(getField((b.get(z))).equals(Mark.XX)) {areaTouchesWhite = true; }
						if(areaTouchesWhite && areaTouchesBlack) {break arealoop;}
					}
				}
			
			// Add scores
			if(areaTouchesWhite && areaTouchesBlack) {
				areasMarks.add(Mark.EMPTY); 
				System.out.println("...of nobody!"); 
			} else if(areaTouchesWhite && !areaTouchesBlack) {
				areasMarks.add(Mark.XX); 
				scoreWhite = scoreWhite + area.size();
				System.out.println("...WHITE!");
			} else if(!areaTouchesWhite && areaTouchesBlack) {
				areasMarks.add(Mark.OO); 
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

	public List<List<Integer>> calculateChainsByStreams(Mark mark) {
		// Create a list from 1 to DIM * DIM
		List<Integer> allFieldIndexes = IntStream.range(0, DIM * DIM).boxed().collect(Collectors.toList());
		List<Integer> filteredIndices = allFieldIndexes.stream().filter(f -> getField(f).equals(mark)).collect(Collectors.toList());
		System.out.println("Filtered list of size " + filteredIndices.size());

		// Filter connected marks
		List<List<Integer>> chains = new ArrayList<List<Integer>>();
		while(filteredIndices.size() > 1) {
			List<Integer> newChain = new ArrayList<Integer>();
			List<Integer> newChainFilled = recursiveChainMaker(newChain,filteredIndices.get(0), mark);
			chains.add(newChainFilled);
			filteredIndices.removeAll(newChainFilled);
		}

		return chains;
	}

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

	public List<Integer> addToChain(List<Integer> chain, int fieldIndex) {
		if(!chain.contains(fieldIndex)) {
			chain.add(fieldIndex);
		}
		return chain;
	}
	public List<Integer> getSameMarkAdjecentFields(int fieldIndex, Mark mark) {
		List<Integer> adjecentFields  = getAdjecentFields(fieldIndex);
		List<Integer> sameMarkAdjecentFields = adjecentFields.stream()
				.filter(f -> getField(f) == mark)
				.collect(Collectors.toList());
		return sameMarkAdjecentFields;
	}

	//	public List<List<Integer>> calculateChains(Mark mark) {
	//		// Step 0: Initialize a list to keep the chain of fields
	//		List<List<Integer>> chains = new ArrayList<List<Integer>>();
	//		List<Integer> singleChainIndices = new ArrayList<Integer>();
	//
	//		// Step 1: CHECK FOR EVERY FIELD...
	//		for(int currentField = 0; currentField < DIM * DIM; currentField++) {
	//
	//			// Step 2a: IF THE FIELD IS IS OF THE INPUTTED MARK...
	//			if (getField(currentField).equals(mark)) {
	//
	//				// Step 2b: CHECK IF THE FIELD IS ALREADY PART OF A PREVIOUS CHAIN
	//				boolean alreadyInChain = false;
	//				innerloop:
	//					for(int j = 0; j < chains.size(); j++) {
	//						List<Integer> currentChain = chains.get(j);
	//						alreadyInChain = currentChain.contains(currentField);
	//						break innerloop;
	//					}
	//
	//				// Step 3a: IF NOT ALREADY IN CHAIN
	//				if (!alreadyInChain) {
	//					// Step 3b: start a new chain
	//					singleChainIndices = new ArrayList<Integer>();
	//					// Step 3c: and add the index
	//					singleChainIndices.add(currentField);
	//					
	//
	//
	//					// Step 4: From here, GET THE SURROUNDING FIELDS of the current field 
	//					List<Integer> adjecentFields  = getAdjecentFields(currentField);
	//					// Initialize list to keep track of the newly added fields
	//					List<Integer> newlyAddedIndices = new ArrayList<Integer>();
	//
	//					// Step 5: CHECK THE MARKS OF THE SURROUNDING FIELDS
	//					
	//					// IF MORE THAN 1 INDEX WAS ADDED, THE CHAIN IS SPLIT
	//					if(newlyAddedIndices.size() < 1) {
	//
	//						// Just one index added, make starting point of further calculation
	//					} else if (newlyAddedIndices.size() == 1) {
	//						
	//						// No index is added, the chain has been ended...
	//					} else {
	//						// End chain
	//						chains.add(singleChainIndices);
	//					}
	//				}
	//			}
	//		}
	//
	//		return chains;
	//	}




	public void removeDeadStones() {
		// At the end of the game, the stones that are still on the board, but unable to avoid capture become prisoners
	}
	//	/**
	//	 * Checks if the mark m has won. A mark wins if it controls at
	//	 * least one row, column or diagonal.
	//	 *
	//	 * @param m
	//	 *            the mark of interest
	//	 * @return true if the mark has won
	//	 */
	//	//@requires m == Mark.XX | m == Mark.OO;
	//	//@ ensures \result == this.hasRow(m) || this.hasColumn(m) | this.hasDiagonal(m);
	//	/*@ pure */
	//	public boolean isWinner(Mark m) {
	////		assert m == Mark.XX || m == Mark.OO;
	////		
	////		if (this.hasColumn(m) || this.hasDiagonal(m) || this.hasRow(m)) {
	////			return true;
	////		}
	//		
	//		// ADD RULE FOR TWO PASSES
	//		return false;
	//	}

	//	/**
	//	 * Returns true if the game has a winner. This is the case when one of the
	//	 * marks controls at least one row, column or diagonal.
	//	 *
	//	 * @return true if the student has a winner.
	//	 */
	//	//@ ensures \result == isWinner(Mark.XX) | \result == isWinner(Mark.OO);
	//	/*@pure*/
	//	public boolean hasWinner() {
	//		
	//		if (this.isWinner(Mark.XX) || this.isWinner(Mark.OO)) {
	//			return true;
	//		}
	//		return false;
	//	}

	// DRAWING OF THE BOARD ---------------------------------------------------------------------------------------------
	/**
	 * Returns a String representation of this board. In addition to the current
	 * situation, the String also shows the numbering of the fields.
	 *
	 * @return the game situation as String
	 */
	public String toString() {
		// Initialize empty string
		String s = "";

		// First string showing the playboard and the empty board
		//		String firstLine = "Play-board                         "+ DELIM_BETWEEN + "Empty board                         \n";
		//		s = firstLine;

		// Fill each row
		for (int i = 0; i < DIM; i++) {
			String row = "";

			// Fill each column
			for (int j = 0; j < DIM; j++) {
				// Append the previous row with a field and "---" or the letter if last
				row = row + getField(i, j).toString();
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
		s = s + "\n" + lastLine; // + DELIM_BETWEEN + lastLine;
		return s;
	}

	//	public String toString() {
	//		// Initialize empty string
	//		String s = "";
	//
	//		// First string showing the playboard and the empty board
	//		String firstLine = "Play-board                         "+ DELIM_BETWEEN + "Empty board                         \n";
	//		s = firstLine;
	//
	//		// Fill each row
	//		for (int i = 0; i < DIM; i++) {
	//			String row = "";
	//
	//			// Fill each column
	//			for (int j = 0; j < DIM; j++) {
	//				// Append the previous row with a field and "---" or the letter if last
	//				row = row + getField(i, j).toString();
	//				if (j < DIM - 1) {
	//					// Draw line between each field
	//					row = row + "---";
	//				} else {
	//					// End the row with the corresponding row letter
	//					String emptyBoardRow = NUMBERING[i * 2];
	//					String letterRow = emptyBoardRow.substring(emptyBoardRow.length() - 1);
	//					row = row + " " + letterRow;
	//				}
	//			}
	//			// Append previous string with the new row + optional delimiter + an line from the full empty board
	//			s = s + row + DELIM_BETWEEN + NUMBERING[i * 2];
	//
	//			// Between the fields an empty line and a line from the full empty board
	//			if (i < DIM - 1) {
	//				s = s + "\n" + LINE + DELIM_BETWEEN + NUMBERING[i * 2 + 1] + "\n";
	//			}
	//		}
	//
	//		String lastLine = NUMBERING[(2 * (DIM - 1)) + 1];
	//		s = s + "\n" + lastLine + DELIM_BETWEEN + lastLine;
	//		return s;
	//	}

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

	public void addPreviousBoard(Board board) {
		// Make a string representatie
		String stringBoard = createStringRepresentationBoard(board);
		// Add to hashset
		previousBoards.add(stringBoard);
	}

	/**
	 * Make a string representation for the check in the HashMap for the 'SuperKo'.
	 * @param board
	 * @return String stringBoard
	 */
	public String createStringRepresentationBoard(Board board) {
		String stringBoard = "";
		for (int i = 0; i < DIM * DIM; i++) {
			stringBoard = stringBoard + board.getField(i).toString();
		}
		//		System.out.println(stringBoard);

		return stringBoard;
	}

	public int getIndexLastMove() {
		return this.indexLastMove;
	}

	public int getDimensions() {
		return DIM;
	}
}
