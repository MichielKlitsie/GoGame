/**
 * 
 */
package go_game;

/**
 * An interface to determine the next move (strategy) for the tic-tac-toe game.
 * @author michiel.klitsie
 *
 */
public interface Strategy {
	/**
	 * public String getName().
	 * @return String name;
	 */
	/*
	 *@ requires b!= null && m != null;
	 *@ ensures \result = String name;
	 */
	public String getName();
	
	/**
	 * public int determineMove().
	 * @return int nextMove;
	 */
	/*
	 *@ requires b!= null && m != null;
	 *@ ensures \result = String name;
	 */
	public int determineMove(Board b, Mark m);
	
}
