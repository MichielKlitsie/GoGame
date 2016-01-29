package go_game.tests;

import static org.junit.Assert.*;

import java.util.TimerTask;

import org.junit.Before;
import org.junit.Test;

import go_game.server.GameTimer;

public class GameTimerTest {

//	private TimerTask gameTimerTask;
	private GameTimer gameTimer;
	@Before
	public void setUp() throws Exception {
//		gameTimerTask = new GameTimer();
		
		gameTimer = new GameTimer(10, null);
		System.out.println("Set up done");
		
	}

////	@Test
//	public void testTimerOff() {
//	System.out.println("Timer not set");
//		assertFalse("Timer not set and false", gameTimer.getTimerStatus());
//	}
//	
	@Test
	public void testTimerOn() {
		System.out.println("Timer test set");
		gameTimer.run();
		System.out.println("Timer test finished");
	}
////	
////	@Test
////	public void testCancelTimer() {
////		fail("Not yet implemented");
////	}

}
