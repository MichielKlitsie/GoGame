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
public class PerfectStrategy implements Strategy {

	private int winningMove;
	private String nameStrategy;
	private int bestMove;
	private int bestQual;
	private int currentField;
	
	public enum QUAL{
        LOSING(0),
        NEUTRAL(1),
        WINNING(2);

        private int value;

        QUAL(int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }
    }

	/**
	 * 
	 */
	public PerfectStrategy() {
		// TODO Auto-generated constructor stub
		this.nameStrategy = "Perfect";
		this.winningMove = 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return nameStrategy;
	}

	@Override
	public int determineMove(Board b, Mark m) {
		// TODO Auto-generated method stub
		this.bestMove = 0; // Field
		this.bestQual = QUAL.LOSING.getValue(); // Quality
		
		bestMove = determineBestMove(b, m);
				
		return 0;
	}

	private int determineBestMove(Board b, Mark m) {
		// TODO Auto-generated method stub
		Board deepCopyBoard = b.deepCopy();
		
		if (bestQual == QUAL.WINNING.getValue()) {
			
			return this.bestMove;
		} else {
			if (checkDirectWin(b, m) == true) {
				this.bestQual = QUAL.WINNING.getValue();
				return this.currentField;
			} else if (checkDirectWin(b, m.other()) == true) {
				this.bestQual = QUAL.LOSING.getValue();
			} else {
				this.bestQual = QUAL.NEUTRAL.getValue();
			};
			
			this.currentField = this.currentField + 1; 
			b.setField(this.currentField, m);
			determineBestMove(b, m);
		}
		return 0;
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
