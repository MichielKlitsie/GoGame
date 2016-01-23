package go_game;

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
		// Equal possibilities for every possible field, including passing
		nextMove = (int) Math.round(Math.random() * (b.DIM * b.DIM + 1));
		if(nextMove == b.DIM * b.DIM + 1) {
			nextMove = -1;
		}
//		System.out.println("Computer '"+ this.getName() + "' chose field " + nextMove + ". Naive move, ~(‾▿‾)~");
		return nextMove;
	}
}
