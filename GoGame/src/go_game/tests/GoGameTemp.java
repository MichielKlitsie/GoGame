package go_game.tests;

import java.util.Date;
import java.util.List;

import go_game.Board;
import go_game.ComputerPlayer;
import go_game.CuttingStrategy;
import go_game.Game;
import go_game.HumanPlayer;
import go_game.Mark;
import go_game.Player;
import go_game.RandomStrategy;
import go_game.SmartStrategy;
import go_game.Strategy;
import go_game.server.GameTimer;

public class GoGameTemp {
	public static void main(String[] args) throws InterruptedException {

		// TEMPORARY 1		
//		Player p1;
//		Player p2;
//
//		// Check input
//
//		System.out.println("Default player names are used. Lazy bastard...");
//		p1 = new HumanPlayer("Pies (Black)", Mark.OO);
////		p2 = new HumanPlayer("Wimpie (White)", Mark.XX);
//		p2 = new ComputerPlayer(Mark.XX, new CuttingStrategy());
//
//		System.out.println("Starting a game...");
//		int dim = 9;
//		Game game = new Game(p1, p2, dim);
//		game.start();

		
		// TEMPORARY 2
//		Board boardSmall = new Board(9);
//		int dim = 9;
//		// Chain 1
//		boardSmall.setField(0, 2, Mark.OO);
//		boardSmall.setField(1, 2, Mark.OO);
//		boardSmall.setField(2, 2, Mark.OO);
//		boardSmall.setField(2, 1, Mark.OO);
//		boardSmall.setField(2, 0, Mark.OO);
//		
//		// Chain 2
//		boardSmall.setField(0, dim-3, Mark.OO);
//		boardSmall.setField(1, dim-3, Mark.OO);
//		boardSmall.setField(2, dim-3, Mark.OO);
//		boardSmall.setField(2, dim-2, Mark.OO);
//		boardSmall.setField(2, dim-1, Mark.OO);
//		
//		// Chain 3
//		for (int j = 0; j < dim; j++) {
//			boardSmall.setField(6, j, Mark.XX);
//		}
//
//				
//		System.out.println(boardSmall.toString());
//		
//		List<List<Integer>> chainsBlack = boardSmall.calculateChainsByStreams(Mark.OO);
//		List<List<Integer>> chainsWhite = boardSmall.calculateChainsByStreams(Mark.XX);
//		
//		System.out.println("Chains Black: " + chainsBlack);
//		System.out.println("Chains White: " + chainsWhite);
//		
//		List<List<Integer>> areas = boardSmall.calculateAreas(Mark.XX);
//		System.out.println("Areas: " + areas);
//		
//		boardSmall.calculateScore();
		
		// TEMPORARY 3
//		Strategy randomStrategy = new RandomStrategy();
//		Player p1 = new ComputerPlayer(Mark.OO, randomStrategy);
//		Player p2 = new ComputerPlayer(Mark.XX, randomStrategy);;
//		int dim = 9;
//		Game game = new Game(p1, p2, dim);
//		game.start();
		
		// TEMPORARY 4
//		Strategy smartStrategy = new SmartStrategy();
//		SmartStrategy smartStrategy2 = new SmartStrategy();
//		
//		int dimBig = 19;
//		Board boardTest = new Board(dimBig);
//		boardTest.setField(40, Mark.XX);
//		List<Integer> listEmptyFields = smartStrategy2.getEmptyFields(boardTest);
//		System.out.println("Empty fields" + listEmptyFields);
//		
//		// LINE 4
//		List<Integer> openStarPoints = smartStrategy2.checkOpenStarPoints(boardTest, listEmptyFields, 4);
//		System.out.println("Star points" + openStarPoints);
//		
//		for (Integer item : openStarPoints) {
//			boardTest.setField(item, Mark.XX);
//		}
//				
//		// LINE 3
//		List<Integer> openStarPoints2 = smartStrategy2.checkOpenStarPoints(boardTest, listEmptyFields, 3);
//		System.out.println("Star points" + openStarPoints2);
//		
//		for (Integer item : openStarPoints2) {
//			boardTest.setField(item, Mark.OO);
//		}
//		System.out.println(boardTest.toString());

		// TEMPORARY 5
//		Board boardSmall = new Board(9);
//		int dim = 9;
//		boardSmall.setField(0, dim - 1, Mark.OO);
//		boardSmall.setField(0, 0, Mark.OO);
//		boardSmall.setField(1, 0, Mark.OO);
//		boardSmall.setField(1, dim - 1, Mark.XX);
//		boardSmall.setField(2, dim - 1, Mark.OO);
////		boardSmall.checkLiberties(Mark.XX);
//		System.out.println("Split chain: \n" + boardSmall.toString());
//		
//		List<List<Integer>> chainsBlack = boardSmall.calculateChainsByStreams(Mark.OO);
//		List<List<Integer>> chainsWhite = boardSmall.calculateChainsByStreams(Mark.XX);
//		System.out.println("Chains Black: " + chainsBlack);
//		System.out.println("Chains White: " + chainsWhite);
		
		// TEMPORARY 6
		GameTimer gameTimer = new GameTimer(20);
//		System.out.println("Set up done: " + new Date() + "\n");
//		Thread.sleep(5000);
//		System.out.println("Waited some time and activate timer" + new Date() + "\n");
//		gameTimer.run();
		System.out.println("Main method finished" + new Date());
	}
}
