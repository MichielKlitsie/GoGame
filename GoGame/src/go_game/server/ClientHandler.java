package go_game.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

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
	private boolean pendingChallengeStatus;
	private GoGameServer goGameServer;
	
	// Keep track of making a move
	private boolean moveHasBeenMade;
	private int[] lastMove = {-999, -999};
	private boolean passMoveMade;
	private boolean indexMoveMade;
	private int lastIndexMove;

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
		setPendingChallengeStatus(false);
		//System.out.println(clientName + "'s thread started");
		mNetworkIOParser = new NetworkIOParser(this, serverArg);
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
		System.out.println("Still implement the request denied service");
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
		sendMessageToClient(getOptions() + "\n");
		
		// Chat-loop
		char[] inputChars = new char[1024];
		int charsRead = 0;
		try {
			while (!terminated) {
				if ((charsRead = in.read(inputChars)) != -1) {
					// Get string 
					String temp = new String(inputChars).substring(0, charsRead);
					
					// Show the command on the server side
					System.out.println("Command received: " + temp);
					
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
		System.out.println("Command received: " + msg);
		
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
	

	// <-------------------------------------------------------------------------------
	// <---- HIER GEBLEVEN ------------------------------------------------------------
	// <-------------------------------------------------------------------------------
	
	// MAAK THE MESSAGE TO SERVER TO START THE GAME
	public void sendGameStartToServer(String nameChallenger, String nameChallenged, int boardDim, String strMarkChallenger, ClientHandler clientHandlerChallenger) {
		// Get the right In and Outputstreams of the socket
//		BufferedReader inChallenged = this.in;
//		BufferedWriter outChallenged = this.out;
//		// And those from the challenger
//		BufferedReader inChallenger = clientHandlerChallenger.in;
//		BufferedWriter outChallenger = clientHandlerChallenger.out;
		
		
		// And let the server create a new thread which executes the game with the corresponding sockets used for communication
		this.goGameServer = server.startGameThread(nameChallenger, nameChallenged, boardDim, strMarkChallenger, 
				 //inChallenged,  outChallenged,  inChallenger, outChallenger);
				clientHandlerChallenger, this);
	}

	// GETTERS AND SETTERS ------------------------------------------------
	public String getOptions() {
		String optionsMenu = "\nOptions menu:\n" +
				"1. PLAY: Play a game agains a random person.\n" + 
				"2. CHALLENGE: Challenge a specific player.\n" + 
				"3. PRACTICE: Practice against a computer player\n" +
				"4. CHAT: Send a message to the players in the lobby.\n";
		return optionsMenu;
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
		return pendingChallengeStatus;
	}


	public void setPendingChallengeStatus(boolean pendingChallengeStatus) {
		this.pendingChallengeStatus = pendingChallengeStatus;
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
		System.out.println("A (x,y) move has been registered by the client handler");
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
		System.out.println("A passing move has been registered by the client handler");
		this.sendMessageToClient("\nYour pass move is registered\n");
		
		// Toggle the boolean, die wordt opgevraagd door de human player player (blijft tot die tijd in een while loop hangen)
		setMoveHasBeenMade(true);
		setPassMoveMade(true);
		
		return this.moveHasBeenMade;
	}
	
	public boolean sentParsedMoveToGoGameServer(int fieldIndex) {
		System.out.println("A fieldIndex move has been registered by the client handler");
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


	
}
