package go_game.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import go_game.ComputerPlayer;
import go_game.CuttingStrategy;
//import go_game.ClientHandler;
import go_game.Game;
import go_game.HumanPlayer;
import go_game.Mark;
import go_game.MirrorStrategy;
import go_game.Player;
import go_game.RandomStrategy;
import go_game.SmartStrategy;
import go_game.Strategy;
import go_game.protocol.AdditionalConstants;
import go_game.protocol.Constants3;
import go_game.protocol.Constants4;

// TODO: Auto-generated Javadoc
/**
 * The Class GoGameServer.
 */
public class GoGameServer extends Thread implements Constants4, Observer, AdditionalConstants {

	/** The p1. */
	// Instance variables
	private Player p1;
	
	/** The p2. */
	private Player p2;
	
	/** The mark challenger. */
	private Mark markChallenger;
	
	/** The mark challenged. */
	private Mark markChallenged;
	
	/** The dim. */
	private int dim;

	/** The client handler p1. */
	// IO streams
	private ClientHandler clientHandlerP1;
	
	/** The client handler p2. */
	private ClientHandler clientHandlerP2;
	
	/** The game. */
	private Game game;
	
	/** The move has been made. */
	public boolean moveHasBeenMade;

	/** The observers. */
	// Observers for a game
	private List<ClientHandler> observers = new ArrayList<ClientHandler>();

	// Thread observer class
	/** The logger. */
	//	private ServerThreadObserver mServerThreadObserver;
	private Logger logger;

	/**
	 * Instantiates a new go game server.
	 *
	 * @param nameChallenger the name challenger
	 * @param nameChallenged the name challenged
	 * @param boardDim the board dim
	 * @param strMarkChallenger the str mark challenger
	 * @param clientHandlerP1 the client handler p1
	 * @param clientHandlerP2 the client handler p2
	 */
	// Constructor
	public GoGameServer(String nameChallenger, String nameChallenged, int boardDim, String strMarkChallenger,
			//BufferedReader inChallenged, BufferedWriter outChallenged, BufferedReader inChallenger, BufferedWriter outChallenger) {
			ClientHandler clientHandlerP1, ClientHandler clientHandlerP2) {
		//Name
		super("ThreadGoGame");
		this.logger = Server.LOGGER;

		// Set the string 'BLACK' to the correct corresponding MARK
		if(strMarkChallenger.trim().equals(BLACK)) {
			markChallenger = Mark.BB;
			markChallenged = Mark.WW;
		} else {
			markChallenger = Mark.WW;
			markChallenged = Mark.BB;
		}

		// Create players :
		// PLAYER 1 IS ALWAYS A HUMAN
		p1 = new HumanPlayer(nameChallenger, markChallenger, clientHandlerP1);
		clientHandlerP1.setLastMark(Mark.BB);

		// PLAYER 2 can be human or computer, depending on the parsed information
		if (nameChallenged.equals(COMPUTER)) {
			String chosenStrategy = clientHandlerP1.getChosenStrategy();
			Strategy strategy;
			switch (chosenStrategy) {
			case CUTTINGSTRATEGY:
				strategy = new CuttingStrategy(); break;
			case MIRRORSTRATEGY:
				strategy = new MirrorStrategy(); break;
			case RANDOMSTRATEGY:
				strategy = new RandomStrategy(); break;
			case SMARTSTRATEGY:
				strategy = new SmartStrategy(); break;
			default:
				strategy = new RandomStrategy(); break;
			}
			p2 = new ComputerPlayer(markChallenged, strategy);
			clientHandlerP1.sendMessageToClient(CHAT + DELIMITER + "Computer player created using a " + strategy.getName());
		} else {
			p2 = new HumanPlayer(nameChallenged, markChallenged, clientHandlerP2);
			clientHandlerP2.setLastMark(Mark.WW);
		}
		dim = boardDim;

		// Set games clients by adding the corresponding clienthandlers
		this.clientHandlerP1 = clientHandlerP1;
		this.clientHandlerP2 = clientHandlerP2;

		//Add the thread to the observer class
		clientHandlerP1.getServer().getServerThreadObserver().addGameThread(this);
		clientHandlerP1.setIsWaitingOnTurn(true);
		clientHandlerP2.setIsWaitingOnTurn(false);
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {	
		// Check input
		this.game = new Game(p1, p2, dim);//, clientHandlerP1, clientHandlerP2);

		// Set the GoGameServer as the observer
		this.game.addObserver(this);

		// Working on same thread as this GoGameServer
		game.start();

		//		String helloBoard = CHAT + DELIMITER + "\nNew board (or 'Goban') of dimensions " + dim + " X " +  dim + " created...\n";
		//		sendMessageBoth(helloBoard);
		if (game.getGameHasEnded()) {
			System.out.println("Closing gamethread \n");
		}
	}

	/**
	 * Send message both.
	 *
	 * @param msg the msg
	 */
	public void sendMessageBoth(String msg) {
		clientHandlerP1.sendMessageToClient(msg);
		clientHandlerP2.sendMessageToClient(msg);
	}

	//	public void sendMessageToObservers(String msg) {
	//		//		System.out.println("Amount observers sent: " + observers.size() +"\n");
	//		if(msg.equals(STOPGAME)) {
	//			
	//			
	//		} else {
	//			// Send the message to the observers
	//			sentMessageToObservers(msg);
	//		}
	//
	//	}

	/**
	 * Send message to observers.
	 *
	 * @param msg the msg
	 */
	protected void sendMessageToObservers(String msg) {
		if (observers.size() > 0) {
			for (ClientHandler currentObserver : observers) {
				currentObserver.sendMessageToClient(msg);
			}
		}
	}

	/**
	 * Sent message to observers server.
	 *
	 * @param msg the msg
	 */
	protected void sentMessageToObserversServer(String msg) {
		if (observers.size() > 0) {
			for (ClientHandler currentObserver : observers) {
				currentObserver.sendMessageToServer(msg);
			}
		}
	}

	/**
	 * Gets the current game.
	 *
	 * @return the current game
	 */
	// GETTERS AND SETTERS -----------------------------------
	public Game getCurrentGame() {
		return this.game;
	}

	/**
	 * Adds the observer.
	 *
	 * @param observer the observer
	 */
	public void addObserver(ClientHandler observer) {
		observers.add(observer);
	}

	/**
	 * Removes the observer.
	 *
	 * @param observer the observer
	 */
	public void removeObserver(ClientHandler observer) {
		observers.remove(observer);
	}

	/**
	 * Gets the observers.
	 *
	 * @return the observers
	 */
	public List<ClientHandler> getObservers() {
		return this.observers;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {

		if(arg.equals(STOPGAME)) {

			// Close the game

			//						clientHandlerP1.sendMessageToClient(GAMEOVER);
			clientHandlerP1.sendMessageToServer(STOPGAME);
			//						clientHandlerP1.sendMessageToClient(GAMEOVER);
			//						clientHandlerP2.sendMessageToClient(GAMEOVER);
			clientHandlerP2.sendMessageToServer(STOPGAME);
			//						clientHandlerP2.sendMessageToClient(GAMEOVER);
			clientHandlerP1.setIsWaitingOnTurn(false);
			clientHandlerP2.setIsWaitingOnTurn(false);

			// Disconnect the observers
			sendMessageToObservers(CHAT + DELIMITER + "The move took to long and the game is forfeited, going back to the lobby. \n");
			sentMessageToObserversServer(STOPGAME);
			//						sendMessageToObservers(GAMEOVER);

			// End the gamethread
			logger.log(Level.INFO, "Game thread of " +clientHandlerP1.getClientName()+ " vs " +clientHandlerP2.getClientName()+"is interrupted");
			this.interrupt();

		} else if (arg.equals("TURNSWITCH")) {
			clientHandlerP1.setIsWaitingOnTurn(!clientHandlerP1.getIsWaitingOnTurn());
			clientHandlerP2.setIsWaitingOnTurn(!clientHandlerP1.getIsWaitingOnTurn());
		} else {
			sendMessageToObservers(arg + "");
		}
	}

}
