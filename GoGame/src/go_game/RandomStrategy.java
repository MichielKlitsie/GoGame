package go_game;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomStrategy implements Strategy {

	private String nameStrategy;
	private int nextMove;
	/**
	 * 
	 */
	public RandomStrategy() {
		nameStrategy = "CompletelyRandom";
	}

	@Override
	public String getName() {
		return this.nameStrategy;
	}

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
	
	// Get the empty field on the board
		public List<Integer> getEmptyFields(Board b) {
			List<Integer> allFieldIndexes = IntStream.range(0, (b.DIM * b.DIM)).boxed().collect(Collectors.toList());
			List<Integer> allEmptyFieldIndexes = allFieldIndexes.stream().filter(p -> b.isEmptyField(p)).collect(Collectors.toList());
			return allEmptyFieldIndexes; 
		}
}
