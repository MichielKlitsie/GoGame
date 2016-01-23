package go_game;

import java.util.Scanner;

import go_game.server.ClientHandler;

/**
 * Class for maintaining a human player in the Go game.
 * 
 * @author Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public class HumanPlayer extends Player {

	//	static Scanner line = new Scanner(System.in);

	// -- Constructors -----------------------------------------------

	/*@
       requires name != null;
       requires mark == Mark.XX || mark == Mark.OO;
       ensures this.getName() == name;
       ensures this.getMark() == mark;
	 */
	private ClientHandler clientHandler;
	


	/**
	 * Creates a new human player object.
	 * 
	 */
	public HumanPlayer(String name, Mark mark, ClientHandler clientHandler) {
		super(name, mark);
		this.clientHandler = clientHandler;
	}

	// -- Commands ---------------------------------------------------

	/*@
       requires board != null;
       ensures board.isField(\result) && board.isEmptyField(\result);

	 */
	/**
	 * Asks the user to input the field where to place the next mark. This is
	 * done using the standard input/output. \
	 * 
	 * @param board
	 *            the game board
	 * @return the player's chosen field
	 */
	public int determineMove(Board board) {
		int choice = -999;
		// What does the human player see when making a move
		String prompt = "> " + getName() + " (" + getMark().toString() + ")"
				+ ", what is your choice? \n" +
				"INPUT: MOVE (int row, int column), MOVE (char row, int column), MOVE index or MOVE 'pass' \n" +
				"Enter input: ";

		// Scan for input, see method readInt(prompt) below
		clientHandler.sendMessageToClient(prompt);
		
		// Input asked, which makes use of a blocking call
		// WAIT FOR INPUT FUNCTIONALITY
		while(!clientHandler.getMoveHasBeenMade()) {
			try {
				Thread.sleep(500); // Wait for a bit
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		// Directly changing the move status
		clientHandler.setMoveHasBeenMade(false);
				//clientHandler.waitForInput();
		
		// Obtain result of determine move
		int[] lastMove = clientHandler.getLastMove();
		System.out.println("Last move in HumanPlayer: (" + lastMove[0] + ", " + lastMove[1] + ")");

		choice = board.index(lastMove[0] - 1, lastMove[1] - 1);
		
		
		// Convert the input to the a field. Example (A,1) = 0 or (C,5) = 22
		if (choice == -1) {
			String passMessage = this.getName() + " has passed on his turn...";
			System.out.println(passMessage);
			clientHandler.sendMessageToClient(passMessage);
			return choice;
		}
		return choice;
	}

	/**
	 * Writes a prompt to standard out and tries to read an int value from
	 * standard in. This is repeated until an int value is entered.
	 * 
	 * @param prompt
	 *            the question to prompt the user
	 * @return the first int value which is entered by the user
	 */
	private int readInt(String prompt) {
		int value = 0;
		boolean intRead = false;

		// Scan the command line input
		@SuppressWarnings("resource")
		Scanner line = new Scanner(System.in);
		try {
			do {
				System.out.print(prompt);
				try (Scanner scannerLine = new Scanner(line.nextLine());) {
					if (scannerLine.hasNextInt()) {
						intRead = true;
						value = scannerLine.nextInt();
					}
				}
			} while (!intRead);
			return value; 
		} finally {
			line.close();
		}

	}
	
	public ClientHandler getClientHandler() {
		// Message functionality here
		return this.clientHandler;
	}
	
	public void sentMessage(String msg) {
		getClientHandler().sendMessageToClient(msg);
	}

	
}
