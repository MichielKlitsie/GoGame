package go_game;

// TODO: Auto-generated Javadoc
/**
 * The Class ComputerPlayer.
 */
public class ComputerPlayer extends Player {
	
	/** The strategy. */
	// Instance variables;
	private Strategy strategy;
	
	/** The mark. */
	//private String namePlayer;
	private Mark mark;
	//private Board board;

	/**
	 * Instantiates a new computer player.
	 *
	 * @param mark the mark
	 * @param strategy the strategy
	 */
	//
	public ComputerPlayer(Mark mark, Strategy strategy) {
		//super(this.namePlayer, mark);
        super(strategy.getName() + " (" + mark.toString() + ")", mark);
		this.strategy = strategy;
		this.mark = mark;
	}
	
	/**
	 * Instantiates a new computer player.
	 *
	 * @param mark the mark
	 */
	// When no strategy is inputted, the random strategy is chosen
	public ComputerPlayer(Mark mark) {
		this(mark, new RandomStrategy());
	}

	/* (non-Javadoc)
	 * @see go_game.Player#determineMove(go_game.Board)
	 */
	// Determine move according to the strategy
	@Override
	public int determineMove(Board board) {
//		boolean isValidMove = false;
		int choice = -999;
		// Check validity of move
//		while (!isValidMove) {
			choice = this.strategy.determineMove(board, this.mark);
			// Rule 7.0: PASS  (wiki): If the choice is -1, the move is passed, 
			//otherwise check Go-rules and make the move
//			isValidMove = checkValidityMove(board, choice);
//		}
//		return keuze;
//		while (!board.isEmptyField(choice)) {
//			choice = this.strategy.determineMove(board, this.mark);
//		}
		
//		System.out.println("Computer '" + this.getName() + "' chose field " + choice + ", ~(‾▿‾)~");
			
		return choice;
	}

	/* (non-Javadoc)
	 * @see go_game.Player#sentMessage(java.lang.String)
	 */
	@Override
	public void sentMessage(String msg) {
		// DO NOTHING?
		// OR PARSE FUNCTIONALITY OF MESSAGE
		
	}
	
	/**
	 * Gets the strategy.
	 *
	 * @return the strategy
	 */
	public Strategy getStrategy() {
		return this.strategy;
	}

}
