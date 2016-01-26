package go_game.server;

import java.util.HashSet;
import java.util.Set;

public class ServerThreadObserver extends Thread {


	// <-------------------------------------------------------------------------------
	// <---- HIER GEBLEVEN ------------------------------------------------------------
	// <-------------------------------------------------------------------------------
	private Server server;
	private Set<Thread> gameThreadSet = new HashSet<Thread>();
	private Set<Thread> clientHandlerThreadSet = new HashSet<Thread>();
	private Set<Thread> otherThreadSet = new HashSet<Thread>();;


	// Constructor - Wordt aangemaakt bij starten van de server...
	public ServerThreadObserver(Server server){
		// Add the server
		this.server = server;
		
		// And create a threadSet of currently running threads
		otherThreadSet = Thread.getAllStackTraces().keySet();
	}
	
	// Server thread
	public Server getServerThread(){
		return this.server;
	}
	
	// ClientHandler threads
	public Set<Thread> getClientHandlerThreads(){
		return clientHandlerThreadSet;
	}
	
	public void addClientHandlerThread(Thread clientHandlerThread) {
		clientHandlerThreadSet.add(clientHandlerThread);
	}
	
	// Game threads
	public Set<Thread> getGameThreads(){
		return gameThreadSet;
	}
	
	public void addGameThread(Thread gameThread) {
		gameThreadSet.add(gameThread);
	}
	
	// When server closes, close all threads
	public void shutDownAllThreads() {
		// TODO: SHUTDOWN: Implements general shutdown
	}
	
}
