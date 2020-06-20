package sprouts.tests;

import java.io.IOException;

import com.sprouts.util.SproutUtil;
import com.sprouts.util.SproutUtil.SproutMoves;

import sprouts.game.GraphicalFacade;

public class SimpleTest {
	
	public static void main(String[] args) throws IOException {
		runTest("/tests/sproutes01.txt");
		runTest("/tests/sproutes02.txt");
		runTest("/tests/sproutes03.txt");
		runTest("/tests/sproutes04.txt");
		runTest("/tests/sproutes05.txt");
		runTest("/tests/sproutes06.txt");
		runTest("/tests/sproutes07.txt");
	}
	
	private static void runTest(String path) throws IOException {
		SproutMoves moves = SproutUtil.loadMovesFromFile(SimpleTest.class.getResourceAsStream(path));
		
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(moves.initialSproutCount);
		facade.executeMoves(moves.rawMoves);
	}
}
