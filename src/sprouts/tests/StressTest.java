package sprouts.tests;

import sprouts.ai.AbstractFacade;
import sprouts.ai.player.Player;
import sprouts.ai.player.RandomPlayer;
import sprouts.game.GraphicalFacade;
import sprouts.game.move.IdMove;
import sprouts.game.util.Assert;

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
				IdMove aiMove = ai.getMove(a.getPosition());
				System.out.printf("ai: %s\n", aiMove.toString());
				
				String result = g.executeMove(aiMove.toString());
				if (result != null)  {
					a.makeMove(aiMove.toString());
					
					// 4<,4<,[0,1,7]
				} else {
					break;
					
				}

				if (aiMove == null) Assert.that(g.isGameOver());
			}
		}
	}
}

