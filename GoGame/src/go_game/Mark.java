package go_game;

/**
 * Represents a mark in the Go game. There three possible values:
 * Mark.XX, Mark.OO and Mark.EMPTY.
 * 
 * @author Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public enum Mark {
    
    EMPTY, XX, OO;

    /*@
       ensures this == Mark.XX ==> \result == Mark.OO;
       ensures this == Mark.OO ==> \result == Mark.XX;
       ensures this == Mark.EMPTY ==> \result == Mark.EMPTY;
     */
    /**
     * Returns the other mark.
     * 
     * @return the other mark is this mark is not EMPTY or EMPTY
     */
    public Mark other() {
        if (this == XX) {
            return OO;
        } else if (this == OO) {
            return XX;
        } else {
            return EMPTY;
        }
    }
    
    public String toString() {
    	if (this == XX) {
            return "X";
        } else if (this == OO) {
            return "O";
        } else {
            return "|";
        }
    }
}
