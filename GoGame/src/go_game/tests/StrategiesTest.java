package go_game.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import go_game.Board;
import go_game.CuttingStrategy;
import go_game.Mark;
import go_game.MirrorStrategy;
import go_game.RandomStrategy;

public class StrategiesTest {

	private Board board;
	private Mark mark;
	private RandomStrategy randomStrategy;
	private CuttingStrategy cuttingStrategy;
	private MirrorStrategy mirrorStrategy;

	@Before
	public void setUp() throws Exception {
		board = new Board(9);
		mark = Mark.BB;
		randomStrategy = new RandomStrategy();
		cuttingStrategy = new CuttingStrategy();
		mirrorStrategy = new MirrorStrategy();
	}

	@Test
	public void test() {
		int a = randomStrategy.determineMove(board, mark);
		int b = cuttingStrategy.determineMove(board, mark);
		int c = mirrorStrategy.determineMove(board, mark);
		System.out.println("Moves:" + a + " " + b + " " + c);
	}

}
