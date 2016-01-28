package go_game.tests;

import org.junit.Before;
import org.junit.Test;

import go_game.Board;
import go_game.Game;
import go_game.HumanPlayer;
import go_game.Mark;
import go_game.Player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BoardTest {
	private Board boardSmall;
	private Board boardBig;

	@Before
	public void setUp() {
		// Asuming a 9x9 board
		boardSmall = new Board(9);
		
	}

	// TESTS FOR SETTING UP OF THE BOARD, VALIDITY OF INDICES, SET FIELDS, RESETS, COPY ETC... 
	@Test
	public void testIndex() {
		assertEquals(0, boardSmall.index(0, 0));
		assertEquals(1, boardSmall.index(0, 1));
		assertEquals(9, boardSmall.index(1, 0));
		assertEquals((2 * 9) + 2, boardSmall.index(2, 2));
	}

	@Test
	public void testIsFieldIndex() {
		assertFalse(boardSmall.isField(-1));
		assertTrue(boardSmall.isField(0));
		assertTrue(boardSmall.isField(8));
		assertTrue(boardSmall.isField(9));
		assertFalse(boardSmall.isField(9 * 9));
	}

	@Test
	public void testIsFieldRowCol() {
		assertFalse(boardSmall.isField(-1, 0));
		assertFalse(boardSmall.isField(0, -1));
		assertTrue(boardSmall.isField(0, 0));
		assertTrue(boardSmall.isField(2, 2));
		assertTrue(boardSmall.isField(2, 3));
		assertTrue(boardSmall.isField(3, 2));
		assertFalse(boardSmall.isField(9, 8));
		assertFalse(boardSmall.isField(8, 9));
	}

	@Test
	public void testSetAndGetFieldIndex() {
		boardSmall.setField(0, Mark.WW);
		assertEquals(Mark.WW, boardSmall.getField(0));
		assertEquals(Mark.EMPTY, boardSmall.getField(1));
	}

	@Test
	public void testSetFieldRowCol() {
		boardSmall.setField(0, 0, Mark.WW);
		assertEquals(Mark.WW, boardSmall.getField(0));
		assertEquals(Mark.EMPTY, boardSmall.getField(0, 1));
		assertEquals(Mark.EMPTY, boardSmall.getField(1, 0));
		assertEquals(Mark.EMPTY, boardSmall.getField(1, 1));
	}

	@Test
	public void testSetup() {
		assertEquals(Mark.EMPTY, boardSmall.getField(0));
		assertEquals(Mark.EMPTY, boardSmall.getField(boardSmall.DIM * boardSmall.DIM - 1));
	}

	@Test
	public void testReset() {
		boardSmall.reset();
		assertEquals(Mark.EMPTY, boardSmall.getField(0));
		assertEquals(Mark.EMPTY, boardSmall.getField(boardSmall.DIM * boardSmall.DIM - 1));
	}

	@Test
	public void testDeepCopy() {
		boardSmall.setField(0, Mark.WW);
		Board deepCopyBoard = boardSmall.deepCopy();
		deepCopyBoard.setField(0, Mark.BB);

		assertEquals(Mark.WW, boardSmall.getField(0));
		assertEquals(Mark.BB, deepCopyBoard.getField(0));
	}

	@Test
	public void testIsEmptyFieldIndex() {
		boardSmall.setField(0, Mark.WW);
		assertFalse(boardSmall.isEmptyField(0));
		assertTrue(boardSmall.isEmptyField(1));
	}

	@Test
	public void testIsEmptyFieldRowCol() {
		boardSmall.setField(0, 0, Mark.WW);
		assertFalse(boardSmall.isEmptyField(0, 0));
		assertTrue(boardSmall.isEmptyField(0, 1));
		assertTrue(boardSmall.isEmptyField(1, 0));
	}

	// TESTS FOR GO RULES (a.k.a. GAME RULES)
	//    Players and equipment[edit]
	//    		Rule 1.[7] Players: Go is a game between two players, called Black and White.
	//    		Rule 2.[8] Board: Go is played on a plain grid of 19 horizontal and 19 vertical lines, called a board.
	//   Definition.[9] ("Intersection", "Adjacent") A point on the board where a horizontal line meets a vertical line is called an intersection. Two intersections are said to be adjacent if they are connected by a horizontal or vertical line with no other intersections between them.
	//    		Rule 3.[10][11] Stones: Go is played with playing tokens known as stones. Each player has at their disposal an adequate supply (usually 180) of stones of the same color.
	//    		Positions[edit]
	//    		Rule 4.[12][13] Positions: At any time in the game, each intersection on the board is in one and only one of the following three states: 1) empty; 2) occupied by a black stone; or 3) occupied by a white stone. A position consists of an indication of the state of each intersection.
	//    		Definition.[14] ("Connected") Two placed stones of the same color (or two empty intersections) are said to be connected if it is possible to draw a path from one intersection to the other by passing through adjacent intersections of the same state (empty, occ. by white, or occ. by black).
	//    		Definition.[15] ("Liberty") In a given position, a liberty of a stone is an empty intersection adjacent to that stone or adjacent to a stone which is connected to that stone.
	//   Play[edit]
	//    		Rule 5.[16] Initial position: At the beginning of the game, the board is empty.
	//    		Rule 6.[17] Turns: Black moves first. The players alternate thereafter.
	//    		Rule 7.[18] Moving: When it is their turn, a player may either pass (by announcing "pass" and performing no action) or play. A play consists of the following steps (performed in the prescribed order):
	//    		Step 1. (Playing a stone) Placing a stone of their color on an empty intersection (chosen subject to Rule 8 and, if it is in effect, to Optional Rule 7A). It can never be moved to another intersection after being played.
	//    		Step 2. (Capture) Removing from the board any stones of their opponent's color that have no liberties.
	//    		Step 3. (Self-capture) Removing from the board any stones of their own color that have no liberties.
	//   		Optional Rule 7A.[19] Prohibition of suicide: A play is illegal if one or more stones of that player's color would be removed in Step 3 of that play.
	//    		Rule 8.[20] Prohibition of repetition: A play is illegal if it would have the effect (after all steps of the play have been completed) of creating a position that has occurred previously in the game.
	//   End[edit]
	//    		Rule 9.[21] End: The game ends when both players have passed consecutively. The final position is the position on the board at the time the players pass consecutively.
	//    		Definition.[22] ("Dead") In the final position, stones are said to be dead if the players agree they would inevitably be removed if the game continued.
	//    		Definition.[23][24] ("Territory") In the final position, an empty intersection is said to belong to a player's territory if, after all dead stones are removed, all stones adjacent to it or to an empty intersection connected to it are of that player's color.
	//    		Definition.[25] ("Area") In the final position, an intersection is said to belong to a player's area if either: 1) it belongs to that player's territory; or 2) it is occupied by a stone of that player's color.
	//    		Definition.[26] ("Score") A player's score is the number of intersections in their area in the final position.
	//    		Rule 10.[27] Winner: If one player has a higher score than the other, then that player wins. Otherwise, the game is drawn.

//	@Test
//	public void setUpGame() {
//		Player p1 = new HumanPlayer("Pies (Black)", Mark.OO);
//		Player p2 = new HumanPlayer("Wimpie (White)", Mark.XX);
//		Game game = new Game(p1, p2, 9); 
//	}
//
//	// Rule 1
//	@Test
//	public void testAmountPlayers() {
//		assertEquals(2, Game.NUMBER_PLAYERS);
//	}

	// Rule 7.2: Captures
	@Test
	public void testCornersCapture() {
		int dim = Board.DIM - 1; // Convert the dimension to start at 0
		boardSmall.setField(0, 0, Mark.WW);
		boardSmall.setField(1, 0, Mark.BB);
		boardSmall.setField(0, 1, Mark.BB);
		boardSmall.checkLiberties(Mark.WW);
		assertEquals(Mark.EMPTY, boardSmall.getField(0, 0));
		boardSmall.setField(dim, dim, Mark.WW);
		boardSmall.setField(dim - 1, dim, Mark.BB);
		boardSmall.setField(dim, dim - 1, Mark.BB);
		boardSmall.checkLiberties(Mark.WW);
		assertEquals(Mark.EMPTY, boardSmall.getField(dim, dim));
		boardSmall.setField(0, dim, Mark.WW);
		boardSmall.setField(1, dim, Mark.BB);
		boardSmall.setField(0, dim - 1, Mark.BB);
		boardSmall.checkLiberties(Mark.WW);
		assertEquals(Mark.EMPTY, boardSmall.getField(0, dim));
		boardSmall.setField(dim, 0, Mark.WW);
		boardSmall.setField(dim - 1, 0, Mark.BB);
		boardSmall.setField(dim, 1, Mark.BB);
		boardSmall.checkLiberties(Mark.WW);
		assertEquals(Mark.EMPTY, boardSmall.getField(dim, 0));
		//    	
	}

	@Test
	public void testUpperSideCapture() {
		int x = 3; // place
		boardSmall.setField(0, x, Mark.WW);
		boardSmall.setField(0, x - 1, Mark.BB);
		boardSmall.setField(0, x + 1, Mark.BB);
		boardSmall.setField(1, x, Mark.BB);
		boardSmall.checkLiberties(Mark.WW);
		assertEquals(Mark.EMPTY, boardSmall.getField(0, x));

	}

	@Test
	public void testLowerSideCapture() {
		int x = 3; // place
		int dim = Board.DIM - 1; // Convert the dimension to start at 0
		boardSmall.setField(x, dim, Mark.WW);
		boardSmall.setField(x - 1, dim, Mark.BB);
		boardSmall.setField(x + 1, dim, Mark.BB);
		boardSmall.setField(x, dim - 1, Mark.BB);
		boardSmall.checkLiberties(Mark.WW);
		assertEquals(Mark.EMPTY, boardSmall.getField(x, dim));

	}

	@Test
	public void testLeftSideCapture() {
		int x = 3; // place
		boardSmall.setField(x, 0, Mark.WW);
		boardSmall.setField(x - 1, 0, Mark.BB);
		boardSmall.setField(x + 1, 0, Mark.BB);
		boardSmall.setField(x, 1, Mark.BB);
		boardSmall.checkLiberties(Mark.WW);
		assertEquals(Mark.EMPTY, boardSmall.getField(x, 0));
	}

	@Test
	public void testRightSideCapture() {
		int x = 3; // place
		int dim = Board.DIM - 1; // Convert the dimension to start at 0
		boardSmall.setField(dim, x, Mark.WW);
		boardSmall.setField(dim, x - 1, Mark.BB);
		boardSmall.setField(dim, x + 1, Mark.BB);
		boardSmall.setField(dim - 1, x, Mark.BB);
		boardSmall.checkLiberties(Mark.WW);
		assertEquals(Mark.EMPTY, boardSmall.getField(dim, x));
	}

	@Test
	public void testCenterCapture() {
		int x = 4; // place
		boardSmall.setField(x, x, Mark.WW);
		boardSmall.setField(x, x - 1, Mark.BB);
		boardSmall.setField(x, x + 1, Mark.BB);
		boardSmall.setField(x + 1, x, Mark.BB);
		boardSmall.setField(x - 1, x, Mark.BB);
		boardSmall.checkLiberties(Mark.WW);
		assertEquals(Mark.EMPTY, boardSmall.getField(x, x));
	}

	// Prohibition of suicide
	@Test
	public void testSuicide() {
		boardSmall.setField(0, 0, Mark.BB);
		boardSmall.setField(1, 0, Mark.BB);
		boardSmall.setField(0, 1, Mark.BB);
		assertTrue(boardSmall.checkSuicide(Mark.BB));
	}
	
	// Repeating board (The Ko-rule)
	@Test
	public void testRepeatingBoard() {
		// Situation alike http://senseis.xmp.net/diagrams/9/7a64a8c8ca6bfadf45207c0400ede024.png
		// Create a cross figure
		boardSmall.setField(0, 1, Mark.BB);
		boardSmall.setField(1, 2, Mark.BB);
		boardSmall.setField(1, 0, Mark.BB);
		boardSmall.setField(2, 1, Mark.BB);
		// And a triangle right next to it
		boardSmall.setField(0, 2, Mark.WW);
		boardSmall.setField(1, 3, Mark.WW);
		boardSmall.setField(2, 2, Mark.WW);
		// Save the state of the board
		String boardState1 = boardSmall.createStringRepresentationBoard(boardSmall);
//		System.out.println("State1: \n" + boardState1 + "\n" + boardSmall.toString());
		boardSmall.addPreviousBoard(boardSmall);
		// Let XX capture a stone of OO
		boardSmall.setField(1, 1, Mark.WW);
		boardSmall.checkLiberties(Mark.BB);
		// Now one stone should be captured
		assertTrue(boardSmall.isEmptyField(1, 2));
		// Save the state of the board
		boardSmall.addPreviousBoard(boardSmall);
		String boardState2 = boardSmall.createStringRepresentationBoard(boardSmall);
//		System.out.println("State2: \n" + boardState2 + "\n" + boardSmall.toString());

		// Let OO try to take the same position back
		Board boardSmallTest = boardSmall.deepCopy();
		boardSmallTest.setField(1, 2, Mark.BB);
		boardSmallTest.checkLiberties(Mark.BB);
		String boardState3 = boardSmallTest.createStringRepresentationBoard(boardSmallTest);
//		System.out.println("State3: \n" + boardState3 + "\n" + boardSmallTest.toString());
		assertTrue("Test check repeating board",boardSmall.hasKo(boardSmallTest));
	}


	// TEST SCORING
	@Test
	public void testTerritoryScoringBlackCornerSmall() {
		//CHAIN 1: CORNER BLACK
		int dim = 9;
		boardSmall.setField(0, 2, Mark.BB);
		boardSmall.setField(1, 2, Mark.BB);
		boardSmall.setField(2, 2, Mark.BB);
		boardSmall.setField(2, 1, Mark.BB);
		boardSmall.setField(2, 0, Mark.BB);
//		System.out.println(boardSmall.toString());
		int chainAmount = boardSmall.calculateChainsByStreams(Mark.BB).size();
		assertEquals("Wrong amount of chains", 1, chainAmount);
		
		int chainSize = boardSmall.calculateChainsByStreams(Mark.BB).get(0).size();
		assertEquals("Wrong chain size", 5, chainSize);
		
		List<Integer> listIndices = Arrays.asList(2,dim + 2, (2*dim) + 2, (2*dim) + 1, (2*dim)).stream().sorted().collect(Collectors.toList());
		List<Integer> calculatedListIndices = boardSmall.calculateChainsByStreams(Mark.BB).get(0).stream().sorted().collect(Collectors.toList());
		assertEquals("Wrong indices added", listIndices, calculatedListIndices);
	}
	@Test
	public void testTerritoryScoringWhiteCornerBig() {
		// CHAIN 2: CORNER WHITE
		int dim = 19;
		Board boardBig = new Board(dim);
		boardBig.setField(0, 2, Mark.WW);
		boardBig.setField(1, 2, Mark.WW);
		boardBig.setField(2, 2, Mark.WW);
		boardBig.setField(2, 1, Mark.WW);
		boardBig.setField(2, 0, Mark.WW);
//		System.out.println(boardSmall.toString());
		int chainAmount = boardBig.calculateChainsByStreams(Mark.WW).size();
		assertEquals("Wrong amount of chains", 1, chainAmount);
		
		int chainSize = boardBig.calculateChainsByStreams(Mark.WW).get(0).size();
		assertEquals("Wrong chain size", 5, chainSize);
		
		List<Integer> listIndices = Arrays.asList(2,dim + 2, (2*dim) + 2, (2*dim) + 1, (2*dim)).stream().sorted().collect(Collectors.toList());
		List<Integer> calculatedListIndices = boardBig.calculateChainsByStreams(Mark.WW).get(0).stream().sorted().collect(Collectors.toList());
		assertEquals("Wrong indices added", listIndices, calculatedListIndices);
		
	}
	
	@Test
	public void testSplitChain() {
		// CHAIN 3: SPLIT CHAIN
		int dim = 9;
		boardSmall.setField(0, dim - 1, Mark.BB);
		boardSmall.setField(0, 0, Mark.BB);
		boardSmall.setField(1, 0, Mark.BB);
		boardSmall.setField(1, dim - 1, Mark.WW);
		boardSmall.setField(2, dim - 1, Mark.BB);
//		boardSmall.checkLiberties(Mark.XX);
		System.out.println("Split chain: \n" + boardSmall.toString());
		
		List<List<Integer>> chainsBlack = boardSmall.calculateChainsByStreams(Mark.BB);
		List<List<Integer>> chainsWhite = boardSmall.calculateChainsByStreams(Mark.WW);
		System.out.println("Chains Black: " + chainsBlack);
		System.out.println("Chains White: " + chainsWhite);
		
		assertEquals("Wrong amount of black chains", 3, chainsBlack.size());
		assertEquals("Wrong amount of white chains", 1, chainsWhite.size());
	}
		
		// CHAIN 4: CORNER Capture
	@Test
	public void testCornerCapture() {
		int dim = 9 - 1;
		assertEquals(Mark.EMPTY, boardSmall.getField(0, dim));
		boardSmall.setField(dim, 0, Mark.WW);
		boardSmall.setField(dim - 1, 0, Mark.BB);
		boardSmall.setField(dim, 1, Mark.BB);
		boardSmall.checkLiberties(Mark.WW);
//		System.out.println("Corner chain: \n" + boardSmall.toString());
		assertEquals(Mark.EMPTY, boardSmall.getField(dim, 0));
	}

	@Test
	public void testMultipleChains() {
		Board boardSmall = new Board(9);
		int dim = 9;
		// Chain 1
		boardSmall.setField(0, 2, Mark.BB);
		boardSmall.setField(1, 2, Mark.BB);
		boardSmall.setField(2, 2, Mark.BB);
		boardSmall.setField(2, 1, Mark.BB);
		boardSmall.setField(2, 0, Mark.BB);
		
		// Chain 2
		boardSmall.setField(0, dim-3, Mark.BB);
		boardSmall.setField(1, dim-3, Mark.BB);
		boardSmall.setField(2, dim-3, Mark.BB);
		boardSmall.setField(2, dim-2, Mark.BB);
		boardSmall.setField(2, dim-1, Mark.BB);
		
		// Chain 3
		for (int j = 0; j < dim; j++) {
			boardSmall.setField(6, j, Mark.WW);
		}
		
//		System.out.println("Multiple chains: \n" + boardSmall.toString());
		List<List<Integer>> chainsBlack = boardSmall.calculateChainsByStreams(Mark.BB);
		List<List<Integer>> chainsWhite = boardSmall.calculateChainsByStreams(Mark.WW);
		assertEquals("Wrong amount of black chains", 2, chainsBlack.size());
		assertEquals("Wrong amount of white chains", 1, chainsWhite.size());
//		System.out.println("Chains Black: " + chainsBlack);
//		System.out.println("Chains White: " + chainsWhite);
	}
	
	@Test
	public void testAreas(){
		Board boardSmall = new Board(9);
		int dim = 9;
		// Chain 1
		boardSmall.setField(0, 2, Mark.BB);
		boardSmall.setField(1, 2, Mark.BB);
		boardSmall.setField(2, 2, Mark.BB);
		boardSmall.setField(2, 1, Mark.BB);
		boardSmall.setField(2, 0, Mark.BB);
		
		// Chain 2
		boardSmall.setField(0, dim-3, Mark.BB);
		boardSmall.setField(1, dim-3, Mark.BB);
		boardSmall.setField(2, dim-3, Mark.BB);
		boardSmall.setField(2, dim-2, Mark.BB);
		boardSmall.setField(2, dim-1, Mark.BB);
		
		// Chain 3
		for (int j = 0; j < dim; j++) {
			boardSmall.setField(6, j, Mark.WW);
		}
		
//		System.out.println("Multiple chains: \n" + boardSmall.toString());
		List<List<Integer>> chainsBlack = boardSmall.calculateChainsByStreams(Mark.BB);
		List<List<Integer>> chainsWhite = boardSmall.calculateChainsByStreams(Mark.WW);
		List<List<Integer>> areas = boardSmall.calculateAreas(Mark.WW);
		System.out.println("Areas: " + areas);
		
		Map<String, Integer> a = boardSmall.calculateScore();
	}
	
	@Test
	public void testDimensions(){
		assertEquals("Check dimensions", 9, boardSmall.getDimensions());
	}
	
	@Test
	public void testLastMove(){
		boardSmall.setField(0, 2, Mark.WW);
		boardSmall.setField(1, 2, Mark.BB);
		
		assertEquals("Check last move", 9+2, boardSmall.getIndexLastMove());
	}
	
	// TODO: DeadStones
}


