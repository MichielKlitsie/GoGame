package go_game;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// TODO: Auto-generated Javadoc
/**
 * The Class RandomStrategy.
 */
public class RandomStrategy implements Strategy {

	/** The name strategy. */
	private String nameStrategy;
	
	/** The next move. */
	private int nextMove;
	
	/**
	 * Instantiates a new random strategy.
	 */
	public RandomStrategy() {
		nameStrategy = "CompletelyRandom";
	}

	/* (non-Javadoc)
	 * @see go_game.Strategy#getName()
	 */
	@Override
	public String getName() {
		return this.nameStrategy;
	}

	/* (non-Javadoc)
	 * @see go_game.Strategy#determineMove(go_game.Board, go_game.Mark)
	 */
	@Override
	public int determineMove(Board b, Mark m) {
		// Random as in... do choose from the empty fields
		List<Integer> emptyFieldsList = getEmptyFields(b);
		// Equal possibilities for every possible field, including passing
		nextMove = (int) Math.round(Math.random() * (emptyFieldsList.size() + 1));
		if(nextMove == emptyFieldsList.size() + 1) {
			nextMove = -1;
		}
//		System.out.println("Computer '"+ this.getName() + "' chose field " + nextMove + ". Naive move, ~(‾▿‾)~");
		return nextMove;
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
}
