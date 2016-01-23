/**
 * 
 */
package go_game;

import java.util.HashSet;
import java.util.Set;

/**
 * @author michiel.klitsie
 *
 */
public class NaiveStrategy implements Strategy {
	private String nameStrategy;
	private int nextMove;
	/**
	 * 
	 */
	public NaiveStrategy() {
		// TODO Auto-generated constructor stub
		nameStrategy = "Naive";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.nameStrategy;
	}

	@Override
	public int determineMove(Board b, Mark m) {
		// TODO Auto-generated method stub
		//Set<Integer> emptyFields = new HashSet<Integer>();
		nextMove = (int) Math.round(Math.random() * 8);
		return nextMove;
	}

}
