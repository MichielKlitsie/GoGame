package go_game;

import go_game.protocol.Constants4;

// TODO: Auto-generated Javadoc
/**
 * Represents a mark in the Go game. There three possible values:
 * Mark.XX, Mark.OO and Mark.EMPTY.
 * 
 * @author Michiel Klitsie
 * @version $Revision: 1.1 $
 */
public enum Mark implements Constants4 {
    
	/** The empty. */
	// 
    EMPTY, 
 /** The ww. */
 WW, 
 /** The bb. */
 BB, 
 /** The hh. */
 HH, 
 /** The hh. */
 hh;

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
        if (this == WW) {
            return BB;
        } else if (this == BB) {
            return WW;
        } else {
            return EMPTY;
        }
    }
    
    /**
     * To string nice incl hint.
     *
     * @return the string
     */
    public String toStringNiceInclHint() {
    	if (this == WW) {
            return "W";
        } else if (this == BB) {
            return "B";
        } else if (this == HH) {
        	return "H";
        } else if (this == hh) {
        	return "h";
        } else {
            return "|";
        }
    }
    
    /**
     * To string for protocol.
     *
     * @return the string
     */
    public String toStringForProtocol() {
    	if (this == WW) {
            return W;
        } else if (this == BB) {
            return B;
        } else {
            return E;
        }
    }
    
    /**
     * To string for protocol full.
     *
     * @return the string
     */
    public String toStringForProtocolFull() {
    	if (this == WW) {
            return WHITE;
        } else {
            return BLACK;
        }
    }
}
