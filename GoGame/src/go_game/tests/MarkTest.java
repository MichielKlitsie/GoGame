package go_game.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import go_game.Mark;
import go_game.protocol.Constants4;

public class MarkTest implements Constants4{

	@Before
	public void setUp() throws Exception {
		Mark blackMark = Mark.BB;
		Mark whiteMark = Mark.WW;
		Mark bigHintMark = Mark.HH;
		Mark smallHintMark = Mark.hh;
		Mark emptyMark = Mark.EMPTY;
	}

	@Test
	public void testOther() {
		assertEquals(Mark.BB, Mark.WW.other());
		assertEquals(Mark.WW, Mark.BB.other());
		assertEquals(Mark.EMPTY, Mark.EMPTY.other());
	}
	
	@Test
	public void testStrings1() {
		String a = Mark.WW.toStringNiceInclHint();
		String b = Mark.WW.toStringForProtocol();
		String c = Mark.WW.toStringForProtocolFull();
		assertEquals("W", a);
		assertEquals(W, b);
		assertEquals(WHITE, c);
}
	
	@Test
	public void testStrings2() {
		String a1 = Mark.BB.toStringNiceInclHint();
		String b1 = Mark.BB.toStringForProtocol();
		String c1 = Mark.BB.toStringForProtocolFull();
		assertEquals("B", a1);
		assertEquals(B, b1);
		assertEquals(BLACK, c1);
}
	
	@Test
	public void testStrings3() {
		String a2 = Mark.EMPTY.toStringNiceInclHint();
		String b2 = Mark.EMPTY.toStringForProtocol();
//		String c2 = Mark.EMPTY.toStringForProtocolFull();
		assertEquals("|", a2);
		assertEquals(E, b2);
//		assertEquals(WHITE, c2);
}
	
	@Test
	public void testStrings4() {
		String a3 = Mark.HH.toStringNiceInclHint();
		String a4 = Mark.hh.toStringNiceInclHint();
//		String b3 = Mark.HH.toStringForProtocol();
//		String c3 = Mark.HH.toStringForProtocolFull();
		assertEquals("H", a3);
		assertEquals("h", a4);
//		assertEquals(W, b3);
//		assertEquals(WHITE, c3);
	}

}
