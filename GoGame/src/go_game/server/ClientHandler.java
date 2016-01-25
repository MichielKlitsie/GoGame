package go_game.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import go_game.Board;
import go_game.Mark;
import go_game.protocol.Constants2;
import go_game.protocol.Constants3;

/**
 * ClientHandler.
 * @author  Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public class ClientHandler extends Thread implements Constants3 {
	// Instance variables -------------------------------------------------------------
	private Server server;
	private BufferedReader in;
	private BufferedWriter out;
	private String clientName;
	private NetworkIOParser mNetworkIOParser;
	private boolean terminated = false;
	
	// The server thread that plays a game
	private GoGameServer goGameServer;
	
	// STATES of the Client
	private boolean isPendingChallenge;
	private boolean moveHasBeenMade;
	private boolean passMoveMade;
	private boolean indexMoveMade;
	private boolean isWaiting;
	private boolean isPlaying;
	private boolean inLobby;
	private boolean isAlreadyChallenged;
	

	// Keep track of making a move
	private int[] lastMove = {-999, -999};
	private int lastIndexMove;
	private Logger logger;
	private ClientHandler clientHandlerOpponent;
	
	// OPTIONS MENUs corresponding to different states
	// TODO: FINALIZE THE OPTION MENUS
	private String optionsMenuLobby = "\nOptions menu lobby:\n" +
			"1. PLAY: Play a game agains a random person.\n" + 
			"2. CHALLENGE/AVAILABLEPLAYERS: Get a list of the people in the lobby.\n" +
			"3. CHALLENGE <namePlayer>: Challenge a specific player in the lobby.\n" +
			"4. PRACTICE: Practice against a computer player.\n" +
			"5. CHAT: Send a message to the players in the lobby.\n" +
			"6. CURRENTGAMES: Get a list of the people playing a game. \n" +
			"7. extra options: tba. \n" +
			"x. GETOPTIONS: Get this options menu.\n";
	private String optionsMenuPlaying = "\nOptions menu playing:\n" +
			"1. MOVE: Play a move in the game. Input as 'MOVE int row, int column', 'MOVE char row, int column', 'MOVE index' or 'MOVE PASS'.\n" + 
			"2. PASS: Challenge a specific player.\n" + 
			"5. CHAT: Send a message to the opponent players.\n" +
			"4. ...: ....\n" + 
			"x. GETOPTIONS: Get this options menu.\n";
	private String optionsMenuPendingChallenge = "\nOptions menu pending challenge:\n" +
			"5. CHAT: Send a message to the players in the lobby.\n" +
			"6. CURRENTGAMES: Get a list of the people playing a game. \n" +
			"x. GETOPTIONS: Get this options menu.\n";
	private String optionsMenuWaitingOnMove = "\nOptions menu waiting on move:\n" +
			"5. CHAT: Send a message to the opponent players.\n" +
			"x. GETOPTIONS: Get this options menu.\n";

	// Constructor ----------------------------------------------------------------

	/**
	 * Constructs a ClientHandler object, who is a server-thread handling the communication
	 * Initialises both Data streams.
	 */
	//@ requires serverArg != null && sockArg != null;
	public ClientHandler(Server serverArg, Socket sockArg) throws IOException {
		super("ThreadClientHandler");
		this.server = serverArg;
		this.in = new BufferedReader(new InputStreamReader(sockArg.getInputStream(), "UTF-8"));
		this.out = new BufferedWriter( new OutputStreamWriter(sockArg.getOutputStream(), "UTF-8"));
		clientName = in.readLine(); 
	
		//logger.log(Level.INFO,clientName + "'s thread started");
		mNetworkIOParser = new NetworkIOParser(this, serverArg);
		logger = server.LOGGER;
		
		// Set initial state to in the Lobby
		setPendingChallengeStatus(false);
		setIsInLobby(true);
		setIsPlaying(false);
		setIsWaiting(false);
		setIsAlreadyChallenged(false);
	}


	/**
	 * Reads the name of a Client from the input stream and sends 
	 * a broadcast message to the Server to signal that the Client
	 * is participating in the chat. Not0,
	 * ice that this method should 
	 * be called immediately after the ClientHandler has been constructed.
	 */
	public void announce() throws IOException {
//		clientName = in.readLine(); //<---- HIER GEBLEVEN
		server.broadcast("[" + clientName + " has entered the lobby]");
	}
	
	public void deny() throws IOException {
//		clientName = in.readLine(); //<---- HIER GEBLEVEN
//		server.broadcast("USER_DENIED");
		logger.log(Level.INFO,"Still implement the request denied service");
	}

	// Run ----------------------------------------------------------------------------------

	/**
	 * This method takes care of sending (receiving??) messages from the Client.
	 * Every message that is received, is preprended with the name
	 * of the Client, and the new message is offered to the Server
	 * for broadcasting. If an IOException is thrown while reading
	 * the message, the method concludes that the socket connection is
	 * broken and shutdown() will be called. 
	 */
	public void run() {
		// Present options to client
		sendMessageToClient(getOptionsLobby() + "\n");
		
		// Chat-loop
		char[] inputChars = new char[1024];
		int charsRead = 0;
		try {
			while (!terminated) {
				if ((charsRead = in.read(inputChars)) != -1) {
					// Get string 
					String temp = new String(inputChars).substring(0, charsRead);
					
					// Show the command on the server side
					logger.log(Level.INFO,"Command received: " + temp);
					
					// Parse input string, optional outputString?
					String outputString = mNetworkIOParser.parseInput(temp);
				}
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method can be used to send a message over the socket
	 * connection to the Client. If the writing of a message fails,
	 * the method concludes that the socket connection has been lost
	 * and shutdown() is called.
	 */
	public void sendMessageToClient(String msg) {
		try {
			out.write(msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			shutdown();
		}
	}
	
	public void sendMessageToServer(String msg) {
		// Show the command on the server side
		logger.log(Level.INFO,"Command received: " + msg);
		
		// Parse input string, optional outputString?
		String outputString = mNetworkIOParser.parseInput(msg);
		
	}

	
	/**
	 * This ClientHandler signs off from the Server and subsequently
	 * sends a last broadcast to the Server to inform that the Client
	 * is no longer participating in the chat. 
	 */
	public void shutdown() {
		server.removeHandler(this);
		server.broadcast("[" + clientName + " has left]");
		
		// Thread terminates
		terminated = true;
		
	}

	
	// MAAK THE MESSAGE TO SERVER TO START THE GAME
	public void sendGameStartToServer(String nameChallenger, String nameChallenged, int boardDim, String strMarkChallenger, ClientHandler clientHandlerChallenger) {
		
		// Set the opponent
		setClientHandlerOpponent(clientHandlerChallenger);
		
		// Change state of clientHandler of the challenged and the challenger
		setIsPlaying(true);
		setIsInLobby(false);
		clientHandlerChallenger.setIsPlaying(true);
		clientHandlerChallenger.setIsInLobby(false);
		
		// And let the server create a new thread which executes the game with the corresponding sockets used for communication
		this.goGameServer = server.startGameThread(nameChallenger, nameChallenged, boardDim, strMarkChallenger, 
				 //inChallenged,  outChallenged,  inChallenger, outChallenger);
				clientHandlerChallenger, this);
	}

	// GETTERS AND SETTERS ------------------------------------------------
	public String getOptionsLobby() {
		return this.optionsMenuLobby;
	}
	
	public String getOptionsGame() {
		return this.optionsMenuPlaying;
	}

	public String getOptionsPendingChallenge() {
		return this.optionsMenuPendingChallenge;
	}

	public String getOptionsWaitingOnMove() {
		return this.optionsMenuWaitingOnMove;
	}

	/**
	 * This method will return the client name working on this thread
	 */
	
	public String getClientName() {
		return clientName;
	}
	
	public void setClientName(String newName) {
		this.clientName = newName;
	}

	public boolean getPendingChallengeStatus() {
		return this.isPendingChallenge;
	}


	public void setPendingChallengeStatus(boolean pendingChallengeStatus) {
		this.isPendingChallenge = pendingChallengeStatus;
	}
	
	// Wait for input
	public void waitForInput() {
		Scanner line = new Scanner(in);
		String input = "";
		try{
			input = line.nextLine();
		} finally {
			line.close();
		}
		//return input;
	}


	public boolean sentParsedMoveToGoGameServer(int xCo, int yCo) {
		logger.log(Level.INFO,"A (x,y) move has been registered by the client handler");
		this.sendMessageToClient("\nYour move is registered, checking validity...\n");
		
		// Misschien niet nodig
//		goGameServer.ParseMove(xCo, yCo);
		
		this.lastMove[0] = xCo;
		this.lastMove[1] = yCo;
		// Toggle the boolean, die wordt opgevraagd door de human player player (blijft tot die tijd in een while loop hangen)
		setMoveHasBeenMade(true);
		setPassMoveMade(false);

		return this.moveHasBeenMade;
	}
	
	public boolean sentParsedMoveToGoGameServer(String passMove) {
		logger.log(Level.INFO,"A passing move has been registered by the client handler");
		this.sendMessageToClient("\nYour pass move is registered\n");
		
		// Toggle the boolean, die wordt opgevraagd door de human player player (blijft tot die tijd in een while loop hangen)
		setMoveHasBeenMade(true);
		setPassMoveMade(true);
		
		return this.moveHasBeenMade;
	}
	
	public boolean sentParsedMoveToGoGameServer(int fieldIndex) {
		logger.log(Level.INFO,"A fieldIndex move has been registered by the client handler");
		this.sendMessageToClient("\n Your fieldIndex move is registered \n");
		
		// Toggle the boolean, die wordt opgevraagd door de human player player (blijft tot die tijd in een while loop hangen)
		setMoveHasBeenMade(true);
		setPassMoveMade(false);
		setIndexMoveMade(true);
		this.lastIndexMove = fieldIndex;
		
		return this.moveHasBeenMade;
	}
	
	public void setIndexMoveMade(boolean indexMoveMade) {
		this.indexMoveMade = indexMoveMade;	
	}

	public boolean getIndexMoveMade() {
		return this.indexMoveMade;
	}

	public void setPassMoveMade(boolean passMoveMade) {
		this.passMoveMade = passMoveMade;
	}
	
	public boolean getPassMoveMade() {
		return this.passMoveMade;
	}
	
	public int[] getLastMove() {
		return this.lastMove;
	}
	
	public int getLastIndexMove() {
		// TODO Auto-generated method stub
		return this.lastIndexMove;
	}
	
	public boolean getMoveHasBeenMade() {
		return moveHasBeenMade;
	}

	public void setMoveHasBeenMade(boolean moveHasBeenMade) {
		this.moveHasBeenMade = moveHasBeenMade;
	}
	
	/**
	 * Make a string representation for the check in the HashMap for the 'SuperKo'.
	 * @param board
	 * @return String stringBoard
	 */
	public String getProtocolStringRepresentationBoard() {
		String stringBoard = "";
		Board currentBoard = goGameServer.getCurrentGame().getCurrentBoard();
		int dim = currentBoard.getDimensions();
		for (int i = 0; i < dim * dim; i++) {
			Mark markField = currentBoard.getField(i);
			if (markField.equals(Mark.EMPTY)) {
				stringBoard = stringBoard + E;
			} else if (markField.equals(Mark.OO)) {
				stringBoard = stringBoard + B;
			} else {
				stringBoard = stringBoard + W;
			}
		}
		
		return stringBoard;
	}


	public boolean getIsWaiting() {
		return isWaiting;
	}


	public void setIsWaiting(boolean isWaiting) {
		this.isWaiting = isWaiting;
	}

	public void setIsAlreadyChallenged(boolean isAlreadyChallenged) {
		this.isAlreadyChallenged = isAlreadyChallenged;
	}
	
	public boolean getIsAlreadyChallenged() {
		return this.isAlreadyChallenged;
	}

	public boolean getIsPlaying() {
		return isPlaying;
	}


	public void setIsPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}


	public boolean getIsInLobby() {
		return inLobby;
	}


	public void setIsInLobby(boolean inLobby) {
		this.inLobby = inLobby;
	}
	
	public ClientHandler getClientHandlerOpponent() {
		return this.clientHandlerOpponent;
	}
	
	public void setClientHandlerOpponent(ClientHandler clientHandlerOpponent) {
		this.clientHandlerOpponent = clientHandlerOpponent;
	}
}
