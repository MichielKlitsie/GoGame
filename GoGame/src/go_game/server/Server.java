package go_game.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.*;
import java.util.stream.Collectors;

import go_game.Game;
import go_game.HumanPlayer;
import go_game.Mark;
import go_game.Player;
import go_game.protocol.Constants4;

// TODO: Auto-generated Javadoc
/**
 * Server. 
 * @author  Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public class Server extends Thread implements Constants4 {
	
	/** The Constant USAGE. */
	private static final String USAGE
	= "usage: " + Server.class.getName() + " <port>";

	/** The Constant LOGGER. */
	// Logging
	public final static Logger LOGGER = Logger.getLogger(Server.class.getName());
	
	/** The server. */
	public static Server server;
	// Main ----------------------------------------------------------------------------------
	/**
	 *  Start een Server-applicatie op.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println(USAGE);
			LOGGER.setLevel(Level.INFO);
			LOGGER.log(Level.INFO, USAGE);

			System.exit(0);
		}



		server = new Server(Integer.parseInt(args[0]));
		//		server.run();

		// Add shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// Nu kan ik bij de client
				System.out.println("Server not nicely closed...");
				//							Client.client.sendMessage("QUIT");
				//						Client.client.interrupt();
				Server.server.broadcast(QUIT);
			}
		});

		server.setName("Main serverthread");
		server.start();

	}

	/** The port. */
	// Instance variables----------------------------------------------------------
	private int port;
	
	// Purely for testing purposes
	private List<Socket> socks = new ArrayList<Socket>();
	public List<Socket> getSocks(){
		return this.socks;
	}
	
	/** The ssock. */
	private ServerSocket ssock;
	
	/** The challenge partners. */
	//	boolean nameIsTaken;
	public HashMap<String, String> challengePartners = new HashMap<>();
	
	/** The m server thread observer. */
	public static ServerThreadObserver mServerThreadObserver;

	/** The client handler threads. */
	private List<ClientHandler> clientHandlerThreads;

	/** The invalid names. */
	private List<String> invalidNames = Arrays.asList("Hitler", "Wout", "Joris", "asdf");

	// Constructor ----------------------------------------------------------------
	/**
	 *  Constructs a new Server object.
	 *
	 * @param portArg the port arg
	 */
	public Server(int portArg) {
		try {
			// Set the port 
			this.port = portArg;
			clientHandlerThreads = new ArrayList<ClientHandler>();
			//			nameIsTaken = false;

			// Create the socket
			this.ssock = createServerSocket(portArg);
			String serverString = "ServerSocket created on \n - port: " + ssock.getLocalPort() + 
					"\n - Local Socket address: " + ssock.getLocalSocketAddress() +
					"\n - Machines IP address: " + InetAddress.getLocalHost().getHostAddress();

			LOGGER.log(Level.INFO, serverString);		
			// Create the thread observer
			mServerThreadObserver = new ServerThreadObserver(this);
			mServerThreadObserver.start();

		} catch (IOException e) {
			String errorMessage = "ERROR: could not create a socket on port " + port;
			System.out.println(errorMessage);
			LOGGER.log(Level.SEVERE, errorMessage);
		}
	}

	/**
	 * Creates the server socket.
	 *
	 * @param portArg the port arg
	 * @return the server socket
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected ServerSocket createServerSocket(int portArg) throws IOException {
		ServerSocket serversock = new ServerSocket(port);
		return serversock;
	}

	/**
	 * Gets the server socket.
	 *
	 * @return the server socket
	 */
	public ServerSocket getServerSocket(){
		return this.ssock;
	}

	// Run ----------------------------------------------------------------------------------
	/**
	 * Listens to a port of this Server if there are any Clients that 
	 * would like to connect. For every new socket connection a new
	 * ClientHandler thread is started that takes care of the further
	 * communication with the Client.
	 */
	public void run() {
		try {
			while(true) {
				// Accept a new client and start a new thread
				//				nameIsTaken = false;
				String waitingMessage = "\n Waiting for new client to connect...";

				LOGGER.log(Level.INFO, waitingMessage);
				Socket sock = ssock.accept();
				socks.add(sock);
				
				//				System.out.println("Cliented connected, starting handler...");
				// Retrieve clients name

				ClientHandler mClientHandler = new ClientHandler(this, sock);

				// Add the thread to the observer
				//				mServerThreadObserver.addClientHandlerThread(mClientHandler);

				// Look if name already exists before adding to the list
				//				mClientHandler = checkDoubleName(mClientHandler);

				addHandler(mClientHandler);
				//				mClientHandler.announce();
				mClientHandler.start();

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Check double name.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	public boolean checkDoubleName(String name) {
		boolean nameIsTaken;
		List<ClientHandler> listDoubleNames = getPlayersInLobby().stream().filter(p -> name.equals(p.getClientName().trim())).collect(Collectors.toList());
		if (listDoubleNames.size() == 0) {
			nameIsTaken = false;
		} else {
			nameIsTaken = true;
			LOGGER.log(Level.INFO,"Name already exists.");
		}
		return nameIsTaken;
	}

	/**
	 * Check not allowed name.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	public boolean checkNotAllowedName(String name) {
		boolean notAllowedName;
		List<String> invalidNames = getInvalidNames().stream().filter(p -> name.equalsIgnoreCase(p)).collect(Collectors.toList());
		if (invalidNames.size() == 0) {
			notAllowedName = false;
		} else {
			notAllowedName = true;
			LOGGER.log(Level.INFO,"Not allowed name chosen: " + name);
		}
		return notAllowedName;
	}

	/**
	 * Check invalid name.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	public boolean checkInvalidName(String name) {
		boolean inValidName;
		String patternOneNumber = "[0-9]+";
		String patternContainsSpace = "\\S";
		return name.trim().isEmpty() || name.matches(patternOneNumber) || name.matches(patternContainsSpace); 
	}

	/**
	 * Name suggestor.
	 *
	 * @param newName the new name
	 * @return the string
	 */
	public String nameSuggestor(String newName) {
		int i = 1;
		while(checkDoubleName(newName)) {
			newName = newName + i;
			i++;
		}

		return newName;
	}


	/**
	 * Prints the.
	 *
	 * @param message the message
	 */
	// Server functions ----------------------------------------------------------------------------------
	public void print(String message){
		System.out.println(message);
	}

	/**
	 * Sends a message using the collection of connected ClientHandlers
	 * to all connected Clients.
	 * @param msg message that is send
	 */
	public void broadcast(String msg) {
		synchronized(clientHandlerThreads) {
			LOGGER.log(Level.INFO,"Broadcasted to all clients: " + msg);
			// Loop through threads-list and send the message
			Iterator<ClientHandler> threadsIterator = clientHandlerThreads.iterator();
			while (threadsIterator.hasNext()) {
				ClientHandler currentThread = threadsIterator.next();
				currentThread.sendMessageToClient(msg + "\n");
			}
		}
	}

	/**
	 * Broadcast in lobby.
	 *
	 * @param msg the msg
	 */
	public void broadcastInLobby(String msg) {

		LOGGER.log(Level.INFO,"Broadcasted to all clients in the lobby: " + msg);
		// Loop through threads-list and send the message
		List<ClientHandler> playerInLobby = getPlayersInLobby();
		for (ClientHandler currentClientHandler : playerInLobby) {
			currentClientHandler.sendMessageToClient(msg + "\n");
		}

	}


	/**
	 * Add a ClientHandler to the collection of ClientHandlers.
	 * @param handler ClientHandler that will be added
	 */
	public void addHandler(ClientHandler handler) {
		synchronized(clientHandlerThreads) {
			clientHandlerThreads.add(handler);
		}
	}

	/**
	 * Remove a ClientHandler from the collection of ClientHanlders. 
	 * @param handler ClientHandler that will be removed
	 */
	public void removeHandler(ClientHandler handler) {
		synchronized(clientHandlerThreads) {
			clientHandlerThreads.remove(handler);
		}
	}

	// <-------------------------------------------------------------------------------
	// <---- HIER GEBLEVEN ------------------------------------------------------------
	/**
	 * Start game thread.
	 *
	 * @param nameChallenger the name challenger
	 * @param nameChallenged the name challenged
	 * @param boardDim the board dim
	 * @param strMarkChallenger the str mark challenger
	 * @param clientHandlerP1 the client handler p1
	 * @param clientHandlerP2 the client handler p2
	 * @return the go game server
	 */
	// <-------------------------------------------------------------------------------
	public GoGameServer startGameThread(String nameChallenger, String nameChallenged, int boardDim, String strMarkChallenger,  
			//BufferedReader inChallenged, BufferedWriter outChallenged, BufferedReader inChallenger, BufferedWriter outChallenger) {
			ClientHandler clientHandlerP1, ClientHandler clientHandlerP2) {
		LOGGER.log(Level.INFO,"Starting a game...");

		// If the computer is played, the clientHandler1 == clientHandler2
		GoGameServer goGameServer = new GoGameServer(nameChallenger, nameChallenged, boardDim, strMarkChallenger, 
				//inChallenged, outChallenged, inChallenger, outChallenger);
				clientHandlerP1, clientHandlerP2);

		// Add the thread to the observer class
		mServerThreadObserver.addGameThread(goGameServer);

		//		Game game = new Game(p1, p2, dim, );
		goGameServer.start();

		// Return the goGameServer, so the clienthandler can communicate directly with this thread...
		return goGameServer;
	}

	/**
	 * Gets the all players.
	 *
	 * @return the all players
	 */
	// GETTERS AND SETTERS ---------------------------------------------------------------
	public List<ClientHandler> getAllPlayers() {
		return clientHandlerThreads;
	}

	/**
	 * Gets the challenge partners.
	 *
	 * @return the challenge partners
	 */
	public HashMap<String, String> getChallengePartners() {
		return this.challengePartners;
	}

	/**
	 * Adds the challenge partners.
	 *
	 * @param nameChallenger the name challenger
	 * @param nameToBeChallenged the name to be challenged
	 */
	public void addChallengePartners(String nameChallenger, String nameToBeChallenged) {
		this.challengePartners.put(nameToBeChallenged, nameChallenger);
	}

	/**
	 * Gets the players in lobby.
	 *
	 * @return the players in lobby
	 */
	// GET PEOPLE IN LOBBY
	public List<ClientHandler> getPlayersInLobby() {
		List<ClientHandler> allPlayers = getAllPlayers();
		List<ClientHandler> clientHandlerThreadsInLobby = allPlayers.stream()
				.filter(p -> p.getIsInLobby())
				.collect(Collectors.toList());

		return clientHandlerThreadsInLobby;
	}

	/**
	 * Gets the players playing.
	 *
	 * @return the players playing
	 */
	// GET PEOPLE PLAYING
	public List<ClientHandler> getPlayersPlaying() {
		List<ClientHandler> allPlayers = getAllPlayers();
		List<ClientHandler> clientHandlerThreadsIsPlaying = allPlayers.stream()
				.filter(p -> p.getIsPlaying())
				.collect(Collectors.toList());

		return clientHandlerThreadsIsPlaying;
	}

	/**
	 * Gets the players waiting for random play.
	 *
	 * @return the players waiting for random play
	 */
	public List<ClientHandler> getPlayersWaitingForRandomPlay() {
		List<ClientHandler> allPlayers = getAllPlayers();
		List<ClientHandler> clientHandlerThreadsIsPlaying = allPlayers.stream()
				.filter(p -> p.getIsWaitingForRandomPlay())
				.collect(Collectors.toList());

		return clientHandlerThreadsIsPlaying;
	}

	/**
	 * Gets the invalid names.
	 *
	 * @return the invalid names
	 */
	public List<String> getInvalidNames() {
		return this.invalidNames ;
	}

	/**
	 * Gets the server thread observer.
	 *
	 * @return the server thread observer
	 */
	public ServerThreadObserver getServerThreadObserver() {
		return mServerThreadObserver;
	}

}
