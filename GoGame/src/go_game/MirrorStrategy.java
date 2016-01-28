package go_game;


// Matthijs' move stategy
public class MirrorStrategy implements Strategy {

	private String nameStrategy;

	public MirrorStrategy() {
		this.nameStrategy = "Matthijs-strategy";
	}
	
	@Override
	public String getName() {
		return this.nameStrategy; 
	}

	@Override
	public int determineMove(Board b, Mark m) {
		// TODO Auto-generated method stub
		return 0;
	}

	// Telkens aan tegenover gestelde hoek
	
	// Na midden passen
}
