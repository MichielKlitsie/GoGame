package go_game.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import go_game.ComputerPlayer;
//import go_game.ClientHandler;
import go_game.Game;
import go_game.HumanPlayer;
import go_game.Mark;
import go_game.Player;
import go_game.RandomStrategy;
import go_game.Strategy;
import go_game.protocol.Constants3;

public class GoGameServer extends Thread implements Constants3, Observer {

	// Instance variables
	private Player p1;
	private Player p2;
	private Mark markChallenger;
	private Mark markChallenged;
	private int dim;
	
	// IO streams
	private ClientHandler clientHandlerP1;
	private ClientHandler clientHandlerP2;
	private Game game;
	public boolean moveHasBeenMade;
	
	// Observers
	private List<ClientHandler> observers = new ArrayList<ClientHandler>();

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

	public void run() {	
		// Check input
		this.game = new Game(p1, p2, dim);//, clientHandlerP1, clientHandlerP2);
		
		// Set the GoGameServer as the observer
		this.game.addObserver(this);
		
		// Working on same thread as this GoGameServer
		game.start();
		
		String helloBoard = "\nNew board (or 'Goban') of dimensions " + dim + " X " +  dim + " created...\n";
		sendMessageBoth(helloBoard);
	}
	
	public void sendMessageBoth(String msg) {
		clientHandlerP1.sendMessageToClient(msg);
		clientHandlerP2.sendMessageToClient(msg);
	}
	
	public void sendMessageToObservers(String msg) {
		System.out.println("Amount observers sent: " + observers.size() +"\n");
		if (observers.size() > 0) {
			for (ClientHandler currentObserver : observers) {
				currentObserver.sendMessageToClient(msg);
			}
		}

	}
	
	// GETTERS AND SETTERS -----------------------------------
	public Game getCurrentGame() {
		return this.game;
	}
	
	public void addObserver(ClientHandler observer) {
		observers.add(observer);
	}
	
	public void removeObserver(ClientHandler observer) {
		observers.remove(observer);
	}
	
	public List<ClientHandler> getObservers() {
		return this.observers;
	}

	@Override
	public void update(Observable o, Object arg) {

		String stringBoard = "Update of board: \n" + arg;
		sendMessageToObservers(stringBoard);
		
	}

}
