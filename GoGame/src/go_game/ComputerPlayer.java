package go_game;

public class ComputerPlayer extends Player {
	// Instance variables;
	private Strategy strategy;
	//private String namePlayer;
	private Mark mark;
	//private Board board;

	//
	public ComputerPlayer(Mark mark, Strategy strategy) {
		//super(this.namePlayer, mark);
        super(strategy.getName() + " (" + mark.toString() + ")", mark);
		this.strategy = strategy;
		this.mark = mark;
	}
	
	// When no strategy is inputted, the random strategy is chosen
	public ComputerPlayer(Mark mark) {
		this(mark, new RandomStrategy());
	}

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

	@Override
	public void sentMessage(String msg) {
		// DO NOTHING?
		// OR PARSE FUNCTIONALITY OF MESSAGE
		
	}

}
