package go_game;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Wout's annoying cutting strategy
public class CuttingStrategy implements Strategy  {

	// DETERMINE
	// Telkens een steen naast de laatst gelegde
	//1. Vraag de laatst gemaakte move op van het board
	//2. Check the liberties from that previous stone
	//3. Make a random or clockwise choice from free fields
	//4. If pass: pass as well and end the game
	//5. If center: pass

	// Make the move
	//Bij passen na de laatste eigen??

	private String nameStrategy;
	private int nextMove;
	/**
	 * 
	 */
	public CuttingStrategy() {
		nameStrategy = "Wout-strategy";
	}

	@Override
	public String getName() {
		return this.nameStrategy;
	}

	@Override
	public int determineMove(Board b, Mark m) {
		// Obtain last move from board.
		//int amountMovesMade = b.previousBoards.size();
		int indexLastMove = b.getIndexLastMove();
		
		// Get a list of the adjecent fields
		List<Integer> adjecentFieldsList = b.getAdjecentFields(indexLastMove);
		Mark markLastMove = b.getField(indexLastMove);
		List<Integer> listEmptyFields = adjecentFieldsList.stream().filter(i -> b.isEmptyField(i)).collect(Collectors.toList());
		System.out.println(adjecentFieldsList.size() + " adjecent fields next to last move " + indexLastMove + ". " + listEmptyFields.size() + " of them are empty");
		
		// Equal possibilities for every possible field, including passing
		nextMove = listEmptyFields.get(0);
		System.out.println("Computer '"+ this.getName() + "' chose field " + nextMove + 1 + ". Annoying move, ლ(ಠ益ಠლ)");
		return nextMove;
	}

}
