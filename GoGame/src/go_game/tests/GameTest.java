package go_game.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import go_game.Board;
import go_game.ComputerPlayer;
import go_game.Game;
import go_game.HumanPlayer;
import go_game.Mark;
import go_game.Player;
import go_game.RandomStrategy;
import go_game.Strategy;
import go_game.server.Client;
import go_game.server.ClientHandler;
import go_game.server.ClientParsing;
import go_game.server.Server;

public class GameTest {

//	private Server server = server = new Server(1234);
//	private ServerSocket serversock;
//	private Socket sockA;
//	private ClientParsing c1;
//	private ClientParsing c2;
//	private Socket sockB;
//	private ClientHandler ch1;
	private Player p1;
	private Player p2;
	private Game game;
//	private ClientHandler ch2;
	
//	public GameTest(){
//		
//		try {
//			
////			server.start();
////			c1 = new ClientParsing("Piet", InetAddress.getByName("localhost"), 1234);
////			c1.start();
////			c2 = new ClientParsing("Henk", InetAddress.getByName("localhost"), 1234);
////			c2.start();
//			
////			ch1 = new ClientHandler(server, sockB);
////			ch2 = new ClientHandler(server, sockB);
////			System.out.println(server.getSocks());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	@Before
	public void setUp() throws Exception {
		
		
//		ch1 = server.getAllPlayers().get(0);
//		ch2 = server.getAllPlayers().get(0);
		int dim = 9;
//		p1 = new HumanPlayer("Piet", Mark.BB, ch1);
//		p2 = new HumanPlayer("Henk", Mark.WW, ch2);
		Strategy strategy = new RandomStrategy();
		p1 = new ComputerPlayer(Mark.BB, strategy);
		p2 = new ComputerPlayer(Mark.WW, strategy);
		game = new Game(p1, p2, dim);

	}

	@Test
	public void testLastMove() {
//		System.out.println(server.getAllPlayers().size());
		int[] lastMove ={1, 1};
		game.setLastMove(lastMove[0],lastMove[1]);
		int[] a = game.getLastMove();
		assertEquals(lastMove[0], a[0]);
		assertEquals(lastMove[1], a[1]);
	}
	
	@Test
	public void testLegalMove() {
		assertTrue(game.checkLegalMove(1));
		assertTrue(game.checkLegalMove(1,1));
		assertFalse(game.checkLegalMove(-2));
		assertFalse(game.checkLegalMove(-2,-2));
	}
	
	@Test 
	public void testRules() {
		Board board = new Board(9);
		board.setField(0, Mark.BB);
		board.setField(1,1, Mark.BB);
		board.setField(2, Mark.BB);
		System.out.println(board.toStringOnCommandLine());
		assertTrue(game.checkRulesMove(board, 9));
		assertTrue(game.checkRulesMove(board, 2));
	}

	@Test
	public void testGetBoard() {
		game.getCurrentBoard();
	}
}
