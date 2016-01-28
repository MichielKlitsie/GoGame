package go_game.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import go_game.server.Client;
import go_game.server.Server;

public class ServerClientTest {
	private Server server;
	private int port;
	private InetAddress adress;
	private ServerSocket ssock;
	boolean setUpIsDone;

	@Before
	public void setUp() throws Exception {
		// Set up server
		if (this.setUpIsDone) {
			return;
		}
		// do the setup
		this.port = 2727;
		this.server = new Server(port);
		this.adress = InetAddress.getByName("localhost");
		server.setName("Main serverthread");
		server.start();
		this.setUpIsDone = true;
	}

	@Test
	public void testServerZeroClients() {
		assertEquals("Zero clienthandlers",0,this.server.getAllPlayers().size());
		assertEquals("Zero clienthandlers lobby",0,this.server.getPlayersInLobby().size());
		assertEquals("Zero clienthandlers playing",0,this.server.getPlayersPlaying().size());
		assertEquals("Zero clienthandlers challenges",0,this.server.getChallengePartners().size());
	}

	@Test
	public void testServerOneClient() {
		try {
			Client client1 = new Client("Krabbel", this.adress , this.port);
			client1.sendMessage("Krabbel");
			client1.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		assertEquals("One clienthandler",1,this.server.getAllPlayers().size());
		assertEquals("One clienthandlers lobby",1,this.server.getPlayersInLobby().size());
		assertEquals("Zero clienthandlers playing",0,this.server.getPlayersPlaying().size());
		assertEquals("Zero clienthandlers challenges",0,this.server.getChallengePartners().size());
		
	}

	@Test	
	public void testServerFiveClients() {
		// Set up five clients
		try {
			this.adress = InetAddress.getByName("localhost");

			Client client1 = new Client("Krabbel", adress , port);
			Client client2 = new Client("Krabbel2", adress , port);
			Client client3 = new Client("Krabbel3", adress , port);
			// One with the same name
			Client client4 = new Client("Krabbel", adress , port);
			Client client5 = new Client("Krabbel", adress , port);

		} catch (UnknownHostException eUH) {
			eUH.printStackTrace();
		} catch (IOException eIO) {
			eIO.printStackTrace();
		}


		fail("Not yet implemented");
	}

	@Test
	public void testClientFunctionality() {
		fail("Not yet implemented");
	}

	@Test
	public void testClientHandlerFunctionality() {
		fail("Not yet implemented");
	}

}
