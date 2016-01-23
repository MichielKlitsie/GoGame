package go_game.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import go_game.ComputerPlayer;
//import go_game.ClientHandler;
import go_game.Game;
import go_game.HumanPlayer;
import go_game.Mark;
import go_game.Player;
import go_game.RandomStrategy;
import go_game.Strategy;
import go_game.protocol.Constants3;

public class GoGameServer extends Thread implements Constants3 {

	// Instance variables
	private Player p1;
	private Player p2;
	private Mark markChallenger;
	private Mark markChallenged;
	private int dim;
	
	// IO streams
//	private BufferedReader inChallenger;
//	private BufferedReader inChallenged;
//	private BufferedWriter outChallenger;
//	private BufferedWriter outChallenged;
	private ClientHandler clientHandlerP1;
	private ClientHandler clientHandlerP2;
	private Game game;
	public boolean moveHasBeenMade;

	// Constructor
	public GoGameServer(String nameChallenger, String nameChallenged, int boardDim, String strMarkChallenger,
			//BufferedReader inChallenged, BufferedWriter outChallenged, BufferedReader inChallenger, BufferedWriter outChallenger) {
			ClientHandler clientHandlerP1, ClientHandler clientHandlerP2) {
		//Name
		super("ThreadGoGame");

		// Set the string 'BLACK' to the correct corresponding MARK
		if(strMarkChallenger.trim().equals(BLACK)) {
			markChallenger = Mark.OO;
			markChallenged = Mark.XX;
		} else {
			markChallenger = Mark.XX;
			markChallenged = Mark.OO;
		}
		
		// Create players :
		// PLAYER 1 IS ALWAYS A HUMAN
		p1 = new HumanPlayer(nameChallenger, markChallenger, clientHandlerP1);
		
		// PLAYER 2 can be human or computer, depending on the parsed information
		if (nameChallenged.equals(COMPUTER)) {
			Strategy strategy = new RandomStrategy();
			p2 = new ComputerPlayer(markChallenged, strategy);
		} else {
			p2 = new HumanPlayer(nameChallenged, markChallenged, clientHandlerP2);
		}
		dim = boardDim;
		
		// Set games clients by adding the corresponding clienthandlers
		this.clientHandlerP1 = clientHandlerP1;
		this.clientHandlerP2 = clientHandlerP2;
		
		//Add the thread to the observer class
		
	}

	// <-------------------------------------------------------------------------------
	// <---- HIER GEBLEVEN ------------------------------------------------------------
	// <-------------------------------------------------------------------------------
	
	public void run() {	
		// Check input
		this.game = new Game(p1, p2, dim);//, clientHandlerP1, clientHandlerP2);
		
		// Working on same thread as this GoGameServer
		game.start();
		
		String helloBoard = "New board (or 'Goban') of dimensions " + dim + " X " +  dim + " created...";
		sendMessageBoth(helloBoard);
	}
	
	public void sendMessageBoth(String msg) {
		clientHandlerP1.sendMessageToClient(msg);
		clientHandlerP2.sendMessageToClient(msg);
	}
	
//	public boolean ParseMove(int xCo, int yCo) {
//		// Let the GoGameServer ask the game if the move is valid
////		boolean validMove = game.checkLegalMove(xCo, yCo);
////		return validMove;
//		this.moveHasBeenMade = true;
//		return this.moveHasBeenMade;
//	}
	
	

	
//	/**
//	 * This method can be used to send a message over the socket
//	 * connection to the Client. If the writing of a message fails,
//	 * the method concludes that the socket connection has been lost
//	 * and shutdown() is called.
//	 */
//	public void sendMessageP1(String msg) {
//		// TODO insert body
//		
//		try {
//			outChallenger.write(msg);
//			outChallenger.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
////			shutdown();
//		}
//	}
//	
//	/**
//	 * This method can be used to send a message over the socket
//	 * connection to the Client. If the writing of a message fails,
//	 * the method concludes that the socket connection has been lost
//	 * and shutdown() is called.
//	 */
//	public void sendMessageP2(String msg) {
//		// TODO insert body		
//		try {
//			outChallenged.write(msg);
//			outChallenged.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
////			shutdown();
//		}
//	}
//	
//	// GETTERS AND SETTERS -----------------------------------
	public Game getCurrentGame() {
		return this.game;
	}

}
