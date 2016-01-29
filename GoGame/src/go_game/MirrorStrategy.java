package go_game;


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
		return 0;
	}

	// Telkens aan tegenover gestelde hoek
	
	// Na midden passen
}
