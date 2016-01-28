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
import go_game.protocol.Constants2;
import go_game.protocol.Constants3;
import go_game.protocol.Constants4;

/**
 * ClientHandler.
 * @author  Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public class ClientHandler extends Thread implements Constants4 {
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
	private boolean isWaitingOnTurn;
	private boolean isPlaying;
	private boolean inLobby;
	private boolean isAlreadyChallenged;
	private boolean isObserving;
	private boolean isWaitingForRandomPlay;
	private boolean isInWaitingRoom;



	// Keep track of making a move
	private int[] lastMove = {-999, -999};
	private int lastIndexMove;
	private Logger logger;
	private ClientHandler clientHandlerOpponent;
	private ServerTimer serverTimer;
	private int timeOutServer = 300;

	// OPTIONS MENUs corresponding to different states
	// TODO: FINALIZE THE OPTION MENUS
	private String optionsMenuLobby = "Options menu lobby:\n" +
			"1. PLAY: Play a game agains a random person.\n" + 
			"2. CHALLENGE/AVAILABLEPLAYERS: Get a list of the people in the lobby.\n" +
			"3. CHALLENGE <namePlayer>: Challenge a specific player in the lobby.\n" +
			"4. PRACTICE: Practice against a computer player.\n" +
			"5. CHAT: Send a message to the players in the lobby.\n" +
			"6. CURRENTGAMES: Get a list of the people playing a game. \n" +
			"7. GETSTATUS: Get your current status. \n" +
			"x. GETOPTIONS: Get this options menu.\n";
	private String optionsMenuPlaying = "Options menu playing:\n" +
			"1. MOVE: Play a move in the game. Input as 'MOVE int row, int column', 'MOVE char row, int column', 'MOVE index' or 'MOVE PASS'.\n" + 
			"2. PASS: Challenge a specific player.\n" + 
			"5. CHAT: Send a message to the opponent players.\n" +
			"4. ...: ....\n" + 
			"x. GETOPTIONS: Get this options menu.\n";
	private String optionsMenuPendingChallenge = "Options menu pending challenge:\n" +
			"5. CHAT: Send a message to the players in the lobby.\n" +
			"6. CURRENTGAMES: Get a list of the people playing a game. \n" +
			"x. GETOPTIONS: Get this options menu.\n";
	private String optionsMenuWaitingOnMove = "Options menu waiting on move:\n" +
			"5. CHAT: Send a message to the opponent players.\n" +
			"x. GETOPTIONS: Get this options menu.\n";
	private String optionsMenuObserving = "Options menu observing:\n" +
			"5. CHAT: Send a message to the playing players.\n" +
			"x. GETOPTIONS: Get this options menu.\n";
	private String optionsMenuIsInWaitingRoom = "Options menu waiting room:\n" +
			"1. NEWPLAYER <name>: Set a new name.\n" +
			"2. QUIT: Exit the server.\n" +
			"x. GETOPTIONS: Get this options menu.\n";

	// OBSERVER 
	private GoGameServer observeGoGameServer;
	private Socket sock;
	

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
		setWaitingForRandomPlay(false);
		setIsInWaitingRoom(true);

		// Get the name
		//		String inputCommando = in.readLine();
		//		clientName = inputCommando.split(DELIMITER)[1];
		//		mNetworkIOParser.parseInput(inputCommando);
		//		clientName = newName;
		clientName = "Anonymous";

		// Add to the thread observer
		server.getServerThreadObserver().addClientHandlerThread(this);

		// Set the server timer
		serverTimer = new ServerTimer(this.timeOutServer, this);
	}


	/**
	 * Reads the name of a Client from the input stream and sends 
	 * a broadcast message to the Server to signal that the Client
	 * is participating in the chat. Not0,
	 * ice that this method should 
	 * be called immediately after the ClientHandler has been constructed.
	 */
	public void announce() throws IOException {
		server.broadcast(CHAT + DELIMITER + "[" + clientName + " has entered the lobby]");
	}

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


	protected void waitingRoomCheck() {
		// THE WAITING ROOM: Check the double names, invalid names or not allowed names
		boolean nameIsTaken = true;
		boolean nameIsInvalid = true;
		boolean nameIsNotAllowed = true;
		sendMessageToClient(CHAT + DELIMITER + "[You are currently in the waiting room, checking for double names]\n");
		//		while (nameIsTaken || nameIsInvalid || nameIsNotAllowed) {
		String clientCurrentName = getClientName().trim();
		
		nameIsTaken = server.checkDoubleName(clientCurrentName);
		if (nameIsTaken) {
			// Name is taken failure and suggestion
			sendMessageToClient(CHAT + DELIMITER + "Suggestion for name: " + server.nameSuggestor(clientCurrentName) + "\n");
			sendMessageToClient(FAILURE + DELIMITER + NAMETAKEN);
			
		}

		nameIsNotAllowed = server.checkNotAllowedName(clientCurrentName);
		if (nameIsNotAllowed) {
			System.out.println("Kom ik hier?");
			sendMessageToClient(CHAT + DELIMITER + "The name " + clientCurrentName + " is specifically not allowed.\n");
			sendMessageToClient(FAILURE + DELIMITER + NAMENOTALLOWED);
			
			System.out.println("Kom ik hier ook?");
		}

		nameIsInvalid = server.checkInvalidName(clientCurrentName);
		if (nameIsInvalid) { 
			sendMessageToClient(CHAT + DELIMITER + "The name " + clientCurrentName + " is invalid.\n");
			sendMessageToClient(FAILURE + DELIMITER + INVALIDNAME);
			
		}

		if(!nameIsTaken && !nameIsNotAllowed && !nameIsInvalid) {
			// Change the status
			setIsInWaitingRoom(false);
			setIsInLobby(true);
			sendMessageToClient(NEWPLAYERACCEPTED);
			sendMessageToServer(GETOPTIONS);
			sendMessageToServer(GETEXTENSIONS);
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
		this.serverTimer.setStopTimer();
		this.server.removeHandler(this);
		this.server.broadcast(CHAT + DELIMITER + "[" + clientName + " has left the server]\n");

		// Thread terminates
		terminated = true;
		this.interrupt();
		logger.log(Level.INFO, "Shutdown command clienthandler executed");
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

		// Let the other clientHandler get the same reference to the game server
		clientHandlerChallenger.setCurrentGameServer(this.goGameServer);
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

	// SENT MOVE COMMANDS
	public boolean sentParsedMoveToGoGameServer(int xCo, int yCo) {
		logger.log(Level.INFO,"A (x,y) move has been registered by the client handler");
		this.sendMessageToClient(CHAT + "\nYour move is registered, checking validity...\n");

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

	// OBSERVER MODE ------------------------------------------------------------
	public void setObserverModeOn(ClientHandler clientHandlerToObserve) {
		// Get the game of the server
		ClientHandler clientHandlerObserved = clientHandlerToObserve;
		this.observeGoGameServer = clientHandlerToObserve.getCurrentGameServer();
		Game observeGame = observeGoGameServer.getCurrentGame();
		observeGoGameServer.addObserver(this);
		sendMessageToClient(observeGame.getCurrentBoard().toString());
	}

	public void setObserverModeOff() {
		this.observeGoGameServer.removeObserver(this);

	}

	public GoGameServer getCurrentGameServer() {
		return this.goGameServer;
	}

	public void setCurrentGameServer(GoGameServer goGameServer) {
		this.goGameServer = goGameServer;
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

	public String getOptionsObserving() {
		return this.optionsMenuObserving;
	}

	public String getOptionsIsInWaitingRoom() {
		return this.optionsMenuIsInWaitingRoom;
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
		return this.lastIndexMove;
	}

	public boolean getMoveHasBeenMade() {
		return moveHasBeenMade;
	}

	public void setMoveHasBeenMade(boolean moveHasBeenMade) {
		this.moveHasBeenMade = moveHasBeenMade;
	}


	public boolean getIsWaitingForRandomPlay() {
		return isWaitingForRandomPlay;
	}


	public void setWaitingForRandomPlay(boolean isWaitingForRandomPlay) {
		this.isWaitingForRandomPlay = isWaitingForRandomPlay;
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
			} else if (markField.equals(Mark.BB)) {
				stringBoard = stringBoard + B;
			} else {
				stringBoard = stringBoard + W;
			}
		}

		return stringBoard;
	}

	// TURN
	public boolean getIsWaitingOnTurn() {
		return isWaitingOnTurn;
	}


	public void setIsWaitingOnTurn(boolean isWaitingOnTurn) {
		this.isWaitingOnTurn = isWaitingOnTurn;
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

	public void setIsObserving(boolean isObserving) {
		this.isObserving = isObserving;
	}

	public boolean getIsObserving() {
		return this.isObserving;
	}


	public void setIsInWaitingRoom(boolean isInWaitingRoom) {
		this.isInWaitingRoom = isInWaitingRoom;
	}

	public boolean getIsInWaitingRoom() {
		return this.isInWaitingRoom;
	}

	public Server getServer() {
		return this.server;
	}

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


	
}
