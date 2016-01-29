package go_game.server;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import go_game.protocol.Constants4;

// TODO: Auto-generated Javadoc
/**
 * An asynchronous update interface for receiving notifications
 * about ServerThread information as the ServerThread is constructed.
 */
public class ServerThreadObserver extends Thread implements Constants4 {


	// <-------------------------------------------------------------------------------
	// <---- HIER GEBLEVEN ------------------------------------------------------------
	/** The server. */
	// <-------------------------------------------------------------------------------
	private Server server;
	
	/** The game thread set. */
	private Set<GoGameServer> gameThreadSet = new HashSet<GoGameServer>();
	
	/** The client handler thread set. */
	private Set<ClientHandler> clientHandlerThreadSet = new HashSet<ClientHandler>();
	
	/** The other thread set. */
	private Set<Thread> otherThreadSet = new HashSet<Thread>();;
	
	/** The cleanup time. */
	private int cleanupTime = 10;
	
	/** The logger. */
	private Logger logger;

	/**
	 * This method is called when information about an ServerThread
	 * which was previously requested using an asynchronous
	 * interface becomes available.
	 *
	 * @param server the server
	 */
	// Constructor - Wordt aangemaakt bij starten van de server...
	public ServerThreadObserver(Server server){
		// Add the server
		this.server = server;
		this.logger = this.server.LOGGER;
		// And create a threadSet of currently running threads
		otherThreadSet = Thread.getAllStackTraces().keySet();
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		logger.log(Level.INFO, "Thread observer is initiated");
		logger.log(Level.INFO, "Thread clean-up crew started every " + cleanupTime + " seconds");
		while(server.isAlive()) {
			// Look at game threads
			if (gameThreadSet.size() != 0) {
				for (GoGameServer currentThread : gameThreadSet) {
					// cleanup and stop execution
					if(currentThread.isInterrupted() || !currentThread.isAlive()){
						// TODO CLEAN UP GAME THREAD
						gameThreadSet.remove(currentThread);
						logger.log(Level.INFO, "A game thread was cleaned up, " + gameThreadSet.size() + " living game-threads left");
					}
				}
			}

			// Look at clientHandler threads
//			if (clientHandlerThreadSet.size() != 0) {
//				for (ClientHandler currentClientHandler : clientHandlerThreadSet) {
//					if(currentClientHandler.isInterrupted() || currentClientHandler.isAlive()){
//						// TODO CLEAN UP clientHandler THREAD
//						clientHandlerThreadSet.remove(currentClientHandler);
//						logger.log(Level.INFO, currentClientHandler.getName() + " was cleaned up, state was " + currentClientHandler.getState() + ". " + clientHandlerThreadSet.size() + " living CH-threads left");
//					}
//				}
//			}

			// Sleep for a while
			try {
				this.sleep(cleanupTime * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				shutDownAllThreads();
			}
		}
	}
	
	/**
	 * This method is called when information about an ServerThread
	 * which was previously requested using an asynchronous
	 * interface becomes available.
	 *
	 * @return the server thread
	 */
	// Server thread
	public Server getServerThread(){
		return this.server;
	}

	/**
	 * This method is called when information about an ServerThread
	 * which was previously requested using an asynchronous
	 * interface becomes available.
	 *
	 * @return the client handler threads
	 */
	// ClientHandler threads
	public Set<ClientHandler> getClientHandlerThreads(){
		return clientHandlerThreadSet;
	}

	/**
	 * This method is called when information about an ServerThread
	 * which was previously requested using an asynchronous
	 * interface becomes available.
	 *
	 * @param clientHandlerThread the client handler thread
	 */
	public void addClientHandlerThread(ClientHandler clientHandlerThread) {
		System.out.println("Client handler thread is added");
		clientHandlerThreadSet.add(clientHandlerThread);
	}

	/**
	 * This method is called when information about an ServerThread
	 * which was previously requested using an asynchronous
	 * interface becomes available.
	 *
	 * @return the game threads
	 */
	// Game threads
	public Set<GoGameServer> getGameThreads(){
		return gameThreadSet;
	}

	/**
	 * This method is called when information about an ServerThread
	 * which was previously requested using an asynchronous
	 * interface becomes available.
	 *
	 * @param gameThread the game thread
	 */
	public void addGameThread(GoGameServer gameThread) {
		System.out.println("Game thread is added");
		gameThreadSet.add(gameThread);
	}

	/**
	 * This method is called when information about an ServerThread
	 * which was previously requested using an asynchronous
	 * interface becomes available.
	 */
	// When server closes, close all threads
	public void shutDownAllThreads() {
		// TODO: SHUTDOWN: Implements general shutdown
		server.broadcast(QUIT);
	}

}
