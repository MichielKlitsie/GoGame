package go_game.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import go_game.Board;
import go_game.Game;
import go_game.Mark;
import go_game.protocol.AdditionalConstants;
import go_game.protocol.Constants2;
import go_game.protocol.Constants3;
import go_game.protocol.Constants4;

// TODO: Auto-generated Javadoc
/**
 * ClientHandler.
 * @author  Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public class ClientHandler extends Thread implements Constants4, AdditionalConstants {
	
	/** The server. */
	// Instance variables -------------------------------------------------------------
	private Server server;
	
	/** The in. */
	private BufferedReader in;
	
	/** The out. */
	private BufferedWriter out;
	
	/** The client name. */
	private String clientName;
	
	/** The m network io parser. */
	private NetworkIOParser mNetworkIOParser;
	
	/** The terminated. */
	private boolean terminated = false;

	/** The go game server. */
	// The server thread that plays a game
	private GoGameServer goGameServer;

	/** The is pending challenge. */
	// STATES of the Client
	private boolean isPendingChallenge;
	
	/** The move has been made. */
	private boolean moveHasBeenMade;
	
	/** The pass move made. */
	private boolean passMoveMade;
	
	/** The index move made. */
	private boolean indexMoveMade;
	
	/** The is waiting on turn. */
	private boolean isWaitingOnTurn;
	
	/** The is playing. */
	private boolean isPlaying;
	
	/** The in lobby. */
	private boolean inLobby;
	
	/** The is already challenged. */
	private boolean isAlreadyChallenged;
	
	/** The is observing. */
	private boolean isObserving;
	
	/** The is waiting for random play. */
	private boolean isWaitingForRandomPlay;
	
	/** The is in waiting room. */
	private boolean isInWaitingRoom;

	/** The chosen strategy. */
	// Chosen strategy
	private String chosenStrategy;

	/** The last move. */
	// Keep track of making a move
	private int[] lastMove = {-999, -999};
	
	/** The last index move. */
	private int lastIndexMove;
	
	/** The logger. */
	private Logger logger;
	
	/** The client handler opponent. */
	private ClientHandler clientHandlerOpponent;
	
	/** The server timer. */
	private ServerTimer serverTimer;
	
	/** The time out server. */
	private int timeOutServer = 3000;

	/** The observe go game server. */
	// OBSERVER 
	private GoGameServer observeGoGameServer;
	
	/** The sock. */
	private Socket sock;
	
	/** The last mark. */
	private Mark lastMark;

	// Constructor ----------------------------------------------------------------

	/**
	 * Constructs a ClientHandler object, who is a server-thread handling the communication
	 * Initialises both Data streams.
	 *
	 * @param serverArg the server arg
	 * @param sockArg the sock arg
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	//@ requires serverArg != null && sockArg != null;
	public ClientHandler(Server serverArg, Socket sockArg) throws IOException {
		super("ThreadClientHandler");
		this.server = serverArg;
		this.in = new BufferedReader(new InputStreamReader(sockArg.getInputStream(), "UTF-8"));
		this.out = new BufferedWriter( new OutputStreamWriter(sockArg.getOutputStream(), "UTF-8"));
		this.sock = sockArg;
		//		clientName = "StillUnknown";

		//logger.log(Level.INFO,clientName + "'s thread started");
		mNetworkIOParser = new NetworkIOParser(this, serverArg);
		logger = server.LOGGER;

		// Set initial state to in the Lobby
		setPendingChallengeStatus(false);
		setIsInLobby(false); // WAS TRUE
		setIsPlaying(false);
		setIsWaitingOnTurn(false);
		setIsAlreadyChallenged(false);
		setIsObserving(false);
		setIsWaitingForRandomPlay(false);
		setIsInWaitingRoom(true);

		// Get the name
		clientName = "Anonymous";

		// Add to the thread observer
		server.getServerThreadObserver().addClientHandlerThread(this);

		// Set the server timer
		serverTimer = new ServerTimer(this.timeOutServer, this);
		
		// Set the default strategy
		chosenStrategy = RANDOMSTRATEGY;
	}


	/**
	 * Reads the name of a Client from the input stream and sends 
	 * a broadcast message to the Server to signal that the Client
	 * is participating in the chat. Not0,
	 * ice that this method should 
	 * be called immediately after the ClientHandler has been constructed.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void announce() throws IOException {
		server.broadcast(CHAT + DELIMITER + "[" + clientName + " has entered the lobby]" + "\n");
	}

	/**
	 * Deny.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void deny() throws IOException {
		//		clientName = in.readLine(); //<---- HIER GEBLEVEN
		//		server.broadcast("USER_DENIED");
		logger.log(Level.INFO,"Still implement the request denied service");
	}

	// Run ----------------------------------------------------------------------------------

	/**
	 * This method takes care of receiving messages from the Client.
	 * Every message that is received, is preprended with the name
	 * of the Client, and the new message is offered to the Server
	 * for broadcasting. If an IOException is thrown while reading
	 * the message, the method concludes that the socket connection is
	 * broken and shutdown() will be called. 
	 */
	public void run() {
		// Show options waiting room
		sendMessageToClient(CHAT + DELIMITER + getOptionsIsInWaitingRoom() + "\n");

		// Chat-loop input
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

					// Reset the timer
					serverTimer.setStopTimer();
					serverTimer = new ServerTimer(this.timeOutServer, this);

					// Waiting room check
					if(isInWaitingRoom) {
						waitingRoomCheck();
					}

				}
			} 


		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Waiting room check.
	 */
	protected void waitingRoomCheck() {
		// THE WAITING ROOM: Check the double names, invalid names or not allowed names
		boolean nameIsTaken = true;
		boolean nameIsInvalid = true;
		boolean nameIsNotAllowed = true;
		sendMessageToClient(CHAT + DELIMITER + "[You are currently in the waiting room, checking for double names]" + "\n");
		//		while (nameIsTaken || nameIsInvalid || nameIsNotAllowed) {
		String clientCurrentName = getClientName().trim();
		
		nameIsTaken = server.checkDoubleName(clientCurrentName);
		if (nameIsTaken) {
			// Name is taken failure and suggestion
			sendMessageToClient(CHAT + DELIMITER + "Suggestion for name: " + server.nameSuggestor(clientCurrentName) + "\n");
			sendMessageToClient(FAILURE + DELIMITER + NAMETAKEN + "\n");
			
		}

		nameIsNotAllowed = server.checkNotAllowedName(clientCurrentName);
		if (nameIsNotAllowed) {
			System.out.println("Kom ik hier?");
			sendMessageToClient(CHAT + DELIMITER + "The name " + clientCurrentName + " is specifically not allowed." + "\n");
			sendMessageToClient(FAILURE + DELIMITER + NAMENOTALLOWED + "\n");
			
			System.out.println("Kom ik hier ook?");
		}

		nameIsInvalid = server.checkInvalidName(clientCurrentName);
		if (nameIsInvalid) { 
			sendMessageToClient(CHAT + DELIMITER + "The name " + clientCurrentName + " is invalid." + "\n");
			sendMessageToClient(FAILURE + DELIMITER + INVALIDNAME + "\n");
			
		}

		if(!nameIsTaken && !nameIsNotAllowed && !nameIsInvalid) {
			// Change the status
			setIsInWaitingRoom(false);
			setIsInLobby(true);
			sendMessageToClient(NEWPLAYERACCEPTED + "\n");
			sendMessageToServer(GETOPTIONS + "\n");
			sendMessageToServer(GETEXTENSIONS + "\n");
			try {
				announce();
			} catch (IOException e) {
				logger.log(Level.INFO, "Announcing went wrong");
				//					e.printStackTrace();
			}
		}
	}

	/**
	 * This method can be used to send a message over the socket
	 * connection to the Client. If the writing of a message fails,
	 * the method concludes that the socket connection has been lost
	 * and shutdown() is called.
	 *
	 * @param msg the msg
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

	/**
	 * Send message to server.
	 *
	 * @param msg the msg
	 */
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
		this.serverTimer.setStopTimer();
		this.server.removeHandler(this);
		this.server.broadcast(CHAT + DELIMITER + "[" + clientName + " has left the server]\n");

		// Thread terminates
		terminated = true;
		this.interrupt();
		logger.log(Level.INFO, "Shutdown command clienthandler executed");
	}


	/**
	 * Send game start to server.
	 *
	 * @param nameChallenger the name challenger
	 * @param nameChallenged the name challenged
	 * @param boardDim the board dim
	 * @param strMarkChallenger the str mark challenger
	 * @param clientHandlerChallenger the client handler challenger
	 */
	// MAAK THE MESSAGE TO SERVER TO START THE GAME
	public void sendGameStartToServer(String nameChallenger, String nameChallenged, int boardDim, String strMarkChallenger, ClientHandler clientHandlerChallenger) {

		// Set the opponent
//		setClientHandlerOpponent(clientHandlerChallenger);

		// Change state of clientHandler of the challenged and the challenger
		setIsPlaying(true);
		setIsInLobby(false);
		setIsWaitingForRandomPlay(false);
		setPendingChallengeStatus(false);
		clientHandlerChallenger.setIsPlaying(true); 
		clientHandlerChallenger.setIsInLobby(false);
		clientHandlerChallenger.setIsWaitingForRandomPlay(false);
		clientHandlerChallenger.setPendingChallengeStatus(false);
		
		// And let the server create a new thread which executes the game with the corresponding sockets used for communication
		this.goGameServer = server.startGameThread(nameChallenger, nameChallenged, boardDim, strMarkChallenger, 
				//inChallenged,  outChallenged,  inChallenger, outChallenger);
				clientHandlerChallenger, this);

		// Let the other clientHandler get the same reference to the game server
		clientHandlerChallenger.setCurrentGameServer(this.goGameServer);
	}


	/**
	 * Wait for input.
	 */
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

	/**
	 * Sent parsed move to go game server.
	 *
	 * @param xCo the x co
	 * @param yCo the y co
	 * @return true, if successful
	 */
	// SENT MOVE COMMANDS
	public boolean sentParsedMoveToGoGameServer(int xCo, int yCo) {
		logger.log(Level.INFO,"A (x,y) move has been registered by the client handler");
		this.sendMessageToClient(CHAT + "\nYour move is registered, checking validity..." + "\n");

		this.lastMove[0] = xCo;
		this.lastMove[1] = yCo;
		// Toggle the boolean, die wordt opgevraagd door de human player player (blijft tot die tijd in een while loop hangen)
		setMoveHasBeenMade(true);
		setPassMoveMade(false);

		return this.moveHasBeenMade;
	}

	/**
	 * Sent parsed move to go game server.
	 *
	 * @param passMove the pass move
	 * @return true, if successful
	 */
	public boolean sentParsedMoveToGoGameServer(String passMove) {
		logger.log(Level.INFO,"A passing move has been registered by the client handler");
		this.sendMessageToClient("\nYour pass move is registered" + "\n");

		// Toggle the boolean, die wordt opgevraagd door de human player player (blijft tot die tijd in een while loop hangen)
		setMoveHasBeenMade(true);
		setPassMoveMade(true);

		return this.moveHasBeenMade;
	}

	/**
	 * Sent parsed move to go game server.
	 *
	 * @param fieldIndex the field index
	 * @return true, if successful
	 */
	public boolean sentParsedMoveToGoGameServer(int fieldIndex) {
		logger.log(Level.INFO,"A fieldIndex move has been registered by the client handler");
		this.sendMessageToClient("\n Your fieldIndex move is registered" + "\n");

		// Toggle the boolean, die wordt opgevraagd door de human player player (blijft tot die tijd in een while loop hangen)
		setMoveHasBeenMade(true);
		setPassMoveMade(false);
		setIndexMoveMade(true);
		this.lastIndexMove = fieldIndex;

		return this.moveHasBeenMade;
	}

	/**
	 * Sets the observer mode on.
	 *
	 * @param clientHandlerToObserve the new observer mode on
	 */
	// OBSERVER MODE ------------------------------------------------------------
	public void setObserverModeOn(ClientHandler clientHandlerToObserve) {
		// Get the game of the server
		ClientHandler clientHandlerObserved = clientHandlerToObserve;
		this.observeGoGameServer = clientHandlerToObserve.getCurrentGameServer();
		Game observeGame = observeGoGameServer.getCurrentGame();
		observeGoGameServer.addObserver(this);
		int blackCaptives = observeGame.getPlayers()[0].getPrisonersTaken();
		int whiteCaptives = observeGame.getPlayers()[1].getPrisonersTaken();
		sendMessageToClient(BOARD + DELIMITER + observeGame.getCurrentBoard().createStringRepresentationBoard(observeGame.getCurrentBoard()) + DELIMITER + blackCaptives + DELIMITER + whiteCaptives + "\n");
//		sendMessageToClient(CHAT + DELIMITER + observeGame.getCurrentBoard().toStringOnCommandLine());
		this.observeGoGameServer.sendMessageBoth(CHAT + DELIMITER + this.getClientName() + " is now observing your game." + "\n");
	}

	/**
	 * Sets the observer mode off.
	 */
	public void setObserverModeOff() {
		this.observeGoGameServer.removeObserver(this);
	}
	
	/**
	 * Gets the observed game server.
	 *
	 * @return the observed game server
	 */
	public GoGameServer getObservedGameServer() {
		return this.observeGoGameServer;
	}

	/**
	 * Gets the current game server.
	 *
	 * @return the current game server
	 */
	public GoGameServer getCurrentGameServer() {
		return this.goGameServer;
	}

	/**
	 * Sets the current game server.
	 *
	 * @param goGameServer the new current game server
	 */
	public void setCurrentGameServer(GoGameServer goGameServer) {
		this.goGameServer = goGameServer;
	}


	/**
	 * Gets the options lobby.
	 *
	 * @return the options lobby
	 */
	// GETTERS AND SETTERS ------------------------------------------------
	public String getOptionsLobby() {
		return this.OPTIONSMENULOBBY;
	}

	/**
	 * Gets the options game.
	 *
	 * @return the options game
	 */
	public String getOptionsGame() {
		return this.OPTIONSMENUPLAYING;
	}

	/**
	 * Gets the options pending challenge.
	 *
	 * @return the options pending challenge
	 */
	public String getOptionsPendingChallenge() {
		return this.OPTIONSMENUPENDINGCHALLENGE;
	}

	/**
	 * Gets the options waiting on move.
	 *
	 * @return the options waiting on move
	 */
	public String getOptionsWaitingOnMove() {
		return this.OPTIONSMENUWAITINGMOVE;
	}

	/**
	 * Gets the options observing.
	 *
	 * @return the options observing
	 */
	public String getOptionsObserving() {
		return this.OPTIONSMENUOBSERVING;
	}

	/**
	 * Gets the options is in waiting room.
	 *
	 * @return the options is in waiting room
	 */
	public String getOptionsIsInWaitingRoom() {
		return this.OPTIONSMENUWAITINGROOM;
	}
	
	/**
	 * This method will return the client name working on this thread.
	 *
	 * @return the client name
	 */

	public String getClientName() {
		return clientName;
	}

	/**
	 * Sets the client name.
	 *
	 * @param newName the new client name
	 */
	public void setClientName(String newName) {
		this.clientName = newName;
	}

	/**
	 * Gets the pending challenge status.
	 *
	 * @return the pending challenge status
	 */
	public boolean getPendingChallengeStatus() {
		return this.isPendingChallenge;
	}


	/**
	 * Sets the pending challenge status.
	 *
	 * @param pendingChallengeStatus the new pending challenge status
	 */
	public void setPendingChallengeStatus(boolean pendingChallengeStatus) {
		this.isPendingChallenge = pendingChallengeStatus;
	}

	/**
	 * Sets the index move made.
	 *
	 * @param indexMoveMade the new index move made
	 */
	public void setIndexMoveMade(boolean indexMoveMade) {
		this.indexMoveMade = indexMoveMade;	
	}

	/**
	 * Gets the index move made.
	 *
	 * @return the index move made
	 */
	public boolean getIndexMoveMade() {
		return this.indexMoveMade;
	}

	/**
	 * Sets the pass move made.
	 *
	 * @param passMoveMade the new pass move made
	 */
	public void setPassMoveMade(boolean passMoveMade) {
		this.passMoveMade = passMoveMade;
	}

	/**
	 * Gets the pass move made.
	 *
	 * @return the pass move made
	 */
	public boolean getPassMoveMade() {
		return this.passMoveMade;
	}

	/**
	 * Gets the last move.
	 *
	 * @return the last move
	 */
	public int[] getLastMove() {
		return this.lastMove;
	}

	/**
	 * Gets the last index move.
	 *
	 * @return the last index move
	 */
	public int getLastIndexMove() {
		return this.lastIndexMove;
	}

	/**
	 * Gets the move has been made.
	 *
	 * @return the move has been made
	 */
	public boolean getMoveHasBeenMade() {
		return moveHasBeenMade;
	}

	/**
	 * Sets the move has been made.
	 *
	 * @param moveHasBeenMade the new move has been made
	 */
	public void setMoveHasBeenMade(boolean moveHasBeenMade) {
		this.moveHasBeenMade = moveHasBeenMade;
	}


	/**
	 * Gets the checks if is waiting for random play.
	 *
	 * @return the checks if is waiting for random play
	 */
	public boolean getIsWaitingForRandomPlay() {
		return isWaitingForRandomPlay;
	}


	/**
	 * Sets the checks if is waiting for random play.
	 *
	 * @param isWaitingForRandomPlay the new checks if is waiting for random play
	 */
	public void setIsWaitingForRandomPlay(boolean isWaitingForRandomPlay) {
		this.isWaitingForRandomPlay = isWaitingForRandomPlay;
	}

	/**
	 * Make a string representation for the check in the HashMap for the 'SuperKo'.
	 *
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
			} else if (markField.equals(Mark.BB)) {
				stringBoard = stringBoard + B;
			} else {
				stringBoard = stringBoard + W;
			}
		}

		return stringBoard;
	}

	/**
	 * Gets the checks if is waiting on turn.
	 *
	 * @return the checks if is waiting on turn
	 */
	// TURN
	public boolean getIsWaitingOnTurn() {
		return isWaitingOnTurn;
	}


	/**
	 * Sets the checks if is waiting on turn.
	 *
	 * @param isWaitingOnTurn the new checks if is waiting on turn
	 */
	public void setIsWaitingOnTurn(boolean isWaitingOnTurn) {
		this.isWaitingOnTurn = isWaitingOnTurn;
	}

	/**
	 * Sets the checks if is already challenged.
	 *
	 * @param isAlreadyChallenged the new checks if is already challenged
	 */
	public void setIsAlreadyChallenged(boolean isAlreadyChallenged) {
		this.isAlreadyChallenged = isAlreadyChallenged;
	}

	/**
	 * Gets the checks if is already challenged.
	 *
	 * @return the checks if is already challenged
	 */
	public boolean getIsAlreadyChallenged() {
		return this.isAlreadyChallenged;
	}

	/**
	 * Gets the checks if is playing.
	 *
	 * @return the checks if is playing
	 */
	public boolean getIsPlaying() {
		return isPlaying;
	}


	/**
	 * Sets the checks if is playing.
	 *
	 * @param isPlaying the new checks if is playing
	 */
	public void setIsPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}


	/**
	 * Gets the checks if is in lobby.
	 *
	 * @return the checks if is in lobby
	 */
	public boolean getIsInLobby() {
		return inLobby;
	}


	/**
	 * Sets the checks if is in lobby.
	 *
	 * @param inLobby the new checks if is in lobby
	 */
	public void setIsInLobby(boolean inLobby) {
		this.inLobby = inLobby;
	}

	/**
	 * Gets the client handler opponent.
	 *
	 * @return the client handler opponent
	 */
	public ClientHandler getClientHandlerOpponent() {
		return this.clientHandlerOpponent;
	}

	/**
	 * Sets the client handler opponent.
	 *
	 * @param clientHandlerOpponent the new client handler opponent
	 */
	public void setClientHandlerOpponent(ClientHandler clientHandlerOpponent) {
		this.clientHandlerOpponent = clientHandlerOpponent;
	}

	/**
	 * Sets the checks if is observing.
	 *
	 * @param isObserving the new checks if is observing
	 */
	public void setIsObserving(boolean isObserving) {
		this.isObserving = isObserving;
	}

	/**
	 * Gets the checks if is observing.
	 *
	 * @return the checks if is observing
	 */
	public boolean getIsObserving() {
		return this.isObserving;
	}


	/**
	 * Sets the checks if is in waiting room.
	 *
	 * @param isInWaitingRoom the new checks if is in waiting room
	 */
	public void setIsInWaitingRoom(boolean isInWaitingRoom) {
		this.isInWaitingRoom = isInWaitingRoom;
	}

	/**
	 * Gets the checks if is in waiting room.
	 *
	 * @return the checks if is in waiting room
	 */
	public boolean getIsInWaitingRoom() {
		return this.isInWaitingRoom;
	}

	/**
	 * Gets the server.
	 *
	 * @return the server
	 */
	public Server getServer() {
		return this.server;
	}

	/**
	 * Gets the checks if is socket closed.
	 *
	 * @return the checks if is socket closed
	 */
	public boolean getIsSocketClosed() {
		//		try {
		this.terminated = sock.isClosed() || sock.isConnected();

		//		} catch (IOException e) {
		//			System.out.println("Not alive");
		//			this.terminated = true;
		//		}

		//			sendMessageToClient(CHAT + DELIMITER + "Check if your alive");
		return this.terminated;

	}

	/**
	 * Gets the chosen strategy.
	 *
	 * @return the chosen strategy
	 */
	public String getChosenStrategy() {
		return chosenStrategy;
	}


	/**
	 * Sets the chosen strategy.
	 *
	 * @param chosenStrategy the new chosen strategy
	 */
	public void setChosenStrategy(String chosenStrategy) {
		this.chosenStrategy = chosenStrategy;
	}

	/**
	 * Sets the last mark.
	 *
	 * @param mark the new last mark
	 */
	public void setLastMark(Mark mark) {
		this.lastMark = mark;
	}
	
	/**
	 * Gets the last mark.
	 *
	 * @return the last mark
	 */
	public Mark getLastMark() {
		return this.lastMark;
	}
}
