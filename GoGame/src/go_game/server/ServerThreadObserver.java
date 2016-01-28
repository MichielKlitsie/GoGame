package go_game.server;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import go_game.protocol.Constants4;

public class ServerThreadObserver extends Thread implements Constants4 {


	// <-------------------------------------------------------------------------------
	// <---- HIER GEBLEVEN ------------------------------------------------------------
	// <-------------------------------------------------------------------------------
	private Server server;
	private Set<Thread> gameThreadSet = new HashSet<Thread>();
	private Set<ClientHandler> clientHandlerThreadSet = new HashSet<ClientHandler>();
	private Set<Thread> otherThreadSet = new HashSet<Thread>();;
	private int cleanupTime = 10;
	private Logger logger;

	// Constructor - Wordt aangemaakt bij starten van de server...
	public ServerThreadObserver(Server server){
		// Add the server
		this.server = server;
		this.logger = this.server.LOGGER;
		// And create a threadSet of currently running threads
		otherThreadSet = Thread.getAllStackTraces().keySet();
	}

	public void run(){
		logger.log(Level.INFO, "Thread observer is initiated");
		logger.log(Level.INFO, "Thread clean-up crew started every " + cleanupTime + " seconds");
		while(server.isAlive()) {
			// Look at game threads
			if (gameThreadSet.size() != 0) {
				for (Thread currentThread : gameThreadSet) {
					// cleanup and stop execution
					if(currentThread.isInterrupted() || !currentThread.isAlive()){
						// TODO CLEAN UP GAME THREAD
						gameThreadSet.remove(currentThread);
						logger.log(Level.INFO, "A game thread was cleaned up, " + gameThreadSet.size() + " living game-threads left");
					}
				}
			}

			// Look at clientHandler threads
			if (clientHandlerThreadSet.size() != 0) {
				for (ClientHandler currentClientHandler : clientHandlerThreadSet) {
					if(currentClientHandler.isInterrupted() || currentClientHandler.isAlive()){
						// TODO CLEAN UP clientHandler THREAD
						clientHandlerThreadSet.remove(currentClientHandler);
						logger.log(Level.INFO, currentClientHandler.getName() + " was cleaned up, state was " + currentClientHandler.getState() + ". " + clientHandlerThreadSet.size() + " living CH-threads left");
					}
				}
			}

			// Sleep for a while
			try {
				this.sleep(cleanupTime * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				shutDownAllThreads();
			}
		}
	}
	// Server thread
	public Server getServerThread(){
		return this.server;
	}

	// ClientHandler threads
	public Set<ClientHandler> getClientHandlerThreads(){
		return clientHandlerThreadSet;
	}

	public void addClientHandlerThread(ClientHandler clientHandlerThread) {
		System.out.println("Client handler thread is added");
		clientHandlerThreadSet.add(clientHandlerThread);
	}

	// Game threads
	public Set<Thread> getGameThreads(){
		return gameThreadSet;
	}

	public void addGameThread(Thread gameThread) {
		System.out.println("Game thread is added");
		gameThreadSet.add(gameThread);
	}

	// When server closes, close all threads
	public void shutDownAllThreads() {
		// TODO: SHUTDOWN: Implements general shutdown
		server.broadcast(QUIT);
	}

}
