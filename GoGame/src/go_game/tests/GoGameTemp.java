package go_game.tests;

import java.util.List;

import go_game.Board;
import go_game.ComputerPlayer;
import go_game.CuttingStrategy;
import go_game.Game;
import go_game.HumanPlayer;
import go_game.Mark;
import go_game.Player;

public class GoGameTemp {
	public static void main(String[] args) {
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
		
		Board boardSmall = new Board(19);
		int dim = 19;
		// Chain 1
		boardSmall.setField(0, 2, Mark.OO);
		boardSmall.setField(1, 2, Mark.OO);
		boardSmall.setField(2, 2, Mark.OO);
		boardSmall.setField(2, 1, Mark.OO);
		boardSmall.setField(2, 0, Mark.OO);
		
		// Chain 2
		boardSmall.setField(0, dim-3, Mark.OO);
		boardSmall.setField(1, dim-3, Mark.OO);
		boardSmall.setField(2, dim-3, Mark.OO);
		boardSmall.setField(2, dim-2, Mark.OO);
		boardSmall.setField(2, dim-1, Mark.OO);
		
		// Chain 3
		for (int j = 0; j < dim; j++) {
			boardSmall.setField(6, j, Mark.XX);
		}

				
		System.out.println(boardSmall.toString());
		
		List<List<Integer>> chainsBlack = boardSmall.calculateChainsByStreams(Mark.OO);
		List<List<Integer>> chainsWhite = boardSmall.calculateChainsByStreams(Mark.XX);
		
		System.out.println("Chains Black: " + chainsBlack);
		System.out.println("Chains White: " + chainsWhite);
		
		List<List<Integer>> areas = boardSmall.calculateAreas(Mark.XX);
		System.out.println("Areas: " + areas);
		
		boardSmall.calculateScore();
		
		
	}
}
