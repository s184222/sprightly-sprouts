package sprouts.tests;

import sprouts.game.ai.AbstractFacade;
import sprouts.game.ai.player.Player;
import sprouts.game.ai.player.RandomPlayer;
import sprouts.game.model.GraphicalFacade;
import sprouts.game.model.Util;
import sprouts.game.model.move.RawMove;
import sprouts.game.model.move.generators.MovePathResult;

public class StressTest {
	
	public static void main(String[] args) {
		
		int iterations = 100;
		for (int i = 0; i < iterations; i++) {
			
			GraphicalFacade g = new GraphicalFacade();
			AbstractFacade a = new AbstractFacade();
			
			g.createFreshPosition(8);
			a.createFreshPosition(8);

			Player ai = new RandomPlayer(i);
			
			System.out.printf("===================\n");
			System.out.printf("  iteration: %d\n", i);
			System.out.printf("===================\n");

			while (!g.isGameOver()) {
				RawMove aiMove = ai.getMove(a.getPosition());
				System.out.printf("ai: %s\n", aiMove.toString());
				
				MovePathResult p = g.executeMoveWithResult(aiMove.toString());
				if (p != null)  {
					a.makeMove(aiMove.toString());
					break;
					
				}

				if (aiMove == null) Util.require(g.isGameOver());
			}
		}
	}
}

