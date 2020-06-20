package sprouts.tests;

import sprouts.ai.AIFacade;
import sprouts.ai.player.Player;
import sprouts.ai.player.RandomPlayer;
import sprouts.game.GraphicalFacade;
import sprouts.game.move.IdMove;
import sprouts.game.move.MovePipeLineException;

public class StressTest {
	
	public static void main(String[] args) {

		int iterations = 100;
		for (int i = 0; i < iterations; i++) {
			
			GraphicalFacade g = new GraphicalFacade();
			AIFacade a = new AIFacade();
			
			g.createFreshPosition(8);
			a.createFreshPosition(8);

			Player ai = new RandomPlayer(i);
			
			System.out.printf("===================\n");
			System.out.printf("  iteration: %d\n", i);
			System.out.printf("===================\n");

			while (!g.isGameOver()) {
				IdMove aiMove = ai.getMove(a.getPosition());
				if (aiMove == null) break;

				System.out.printf("ai: %s\n", aiMove.toString());
				
				try {
					String result = g.executeMove(aiMove.toString());
					if (result == null) break;

					a.makeMove(aiMove.toString());
				} catch (MovePipeLineException e) {
					break;
				}
			}
		}
	}
}

