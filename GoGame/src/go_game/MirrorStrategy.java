package go_game;

import java.util.List;
import java.util.stream.Collectors;

// TODO: Auto-generated Javadoc
/**
 * The Class MirrorStrategy.
 */
// Matthijs' move stategy
public class MirrorStrategy implements Strategy {

	/** The name strategy. */
	private String nameStrategy;

	/**
	 * Instantiates a new mirror strategy.
	 */
	public MirrorStrategy() {
		this.nameStrategy = "Matthijs-strategy";
	}

	/** The next move. */
	private int nextMove;
	
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
		// TODO Auto-generated method stub
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

	// Telkens aan tegenover gestelde hoek

	// Na midden passen
}
