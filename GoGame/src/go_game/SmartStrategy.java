/**
 * 
 */
package go_game;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author michiel.klitsie
 *
 */
public class SmartStrategy implements Strategy {
private String nameStrategy;
private int nextMove;
private Board board;
private int winningMove = 0;

	/**
	 * 
	 */
	public SmartStrategy() {
		// TODO Auto-generated constructor stub
		this.nameStrategy = "Smart";
		this.winningMove = 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.nameStrategy;
	}

	@Override
	public int determineMove(Board b, Mark m) {
		//Board boardTemp = b.deepCopy();
		// TODO Auto-generated method stub
		if (b.isEmptyField(4)) {
			nextMove = 4;
		} else if (checkDirectWin(b, m)) {
			nextMove = this.winningMove;
		} else if (checkDirectWin(b, m.other())) {
			nextMove = this.winningMove;
			System.out.println("jeah");
		} else {
		nextMove = (int) Math.round(Math.random() * 8);
		}
		
		return nextMove;
	}

	private boolean checkDirectWin(Board b, Mark m) {
//		for (int i = 0; i < 9; i++) {
//			Set<Integer> fields = new HashSet<Integer>(9);
//			Iterator<Integer> it = fields.iterator();
//			while (it.hasNext()) {
//				Integer value = it.next();
//				Board boardTemp = b.deepCopy();
//				boardTemp.setField(value, m);
//				if (
//				boardTemp.hasDiagonal(m) ||
//				boardTemp.hasRow(m) ||
//				boardTemp.hasColumn(m)) {
//				this.winningMove = value;
//					return true;
//				}
//			}
//		}
		return false;
	}

}
