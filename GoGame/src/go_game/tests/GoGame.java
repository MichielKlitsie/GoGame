package go_game.tests;

import go_game.ComputerPlayer;
import go_game.Game;
import go_game.HumanPlayer;
import go_game.Mark;
import go_game.NaiveStrategy;
import go_game.Player;
import go_game.SmartStrategy;

//import ss.week5.ComputerPlayer;
//import ss.week5.Mark;

/**
 * Executable class for the game Go, played on one command line on one computer.
 * Equivalent to the GoGameServer, but using just one input and output!
 * 
 * @author Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public class GoGame {
	

	public static void main(String[] args) {
		Player p1;
		Player p2;
		
		// Check input
		if (args.length == 0) {
			System.out.println("Default player names are used. Lazy bastard...");
			 p1 = new HumanPlayer("Pies", Mark.OO);
			 p2 = new HumanPlayer("Wimpie", Mark.XX);
			 int dim = 9;
			Game game = new Game(p1, p2, 9);
			game.start();
		} else if (args.length == 2) {
			// Naive Computer first
			if (args[0].equals("-N") && !args[1].equals("-N")) {
				p1 = new ComputerPlayer(Mark.OO, new NaiveStrategy());
				p2 = new HumanPlayer(args[1], Mark.XX);
			// Naive Computer second	
			} else if (!args[0].equals("-N") && args[1].equals("-N")) {
				p1 = new HumanPlayer(args[0], Mark.XX);
				p2 = new ComputerPlayer(Mark.OO, new NaiveStrategy());
			// Two Naive computer players against each other	
			} else if (args[0].equals("-N") && args[1].equals("-N")) {
				p1 = new ComputerPlayer(Mark.XX, new NaiveStrategy());
				p2 = new ComputerPlayer(Mark.OO, new NaiveStrategy());
			// 	Smart computer player first
			} else if (args[0].equals("-S") && (!args[1].equals("-N") && !args[1].equals("-S"))) {
				p1 = new ComputerPlayer(Mark.XX, new SmartStrategy());
				p2 = new HumanPlayer(args[1], Mark.OO);
			// Smart computer player second	
			} else if (!args[0].equals("-S") && args[1].equals("-S")) {
				p1 = new HumanPlayer(args[0], Mark.XX);
				p2 = new ComputerPlayer(Mark.OO, new SmartStrategy());
			// Two human players	
			} else {
				p1 = new HumanPlayer(args[0], Mark.OO);
				p2 = new HumanPlayer(args[1], Mark.XX);
			}
			System.out.println("Get ready for the epic match: " + p1.getName() + " vs. " + p2.getName());
			int dim = 9;
			Game game = new Game(p1, p2, dim);
			game.start();
		} else {
			System.out.println("How many players does it take to play Go? Try again");
		}

		// TODO: implement, see P-4.21
	}



}