package com.sprouts.tests;

import com.sprouts.ai.AIFacade;
import com.sprouts.ai.player.Player;
import com.sprouts.ai.player.RandomPlayer;
import com.sprouts.game.GraphicalFacade;
import com.sprouts.game.move.IdMove;
import com.sprouts.game.move.MovePipeLineException;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */

public class StressTest {
	
	public static void main(String[] args) {

		int iterations = 100;
		for (int i = 0; i < iterations; i++) {
			
			GraphicalFacade g = new GraphicalFacade();
			AIFacade a = new AIFacade();
			
			g.createFreshPosition(8);
			a.createFreshPosition(8);

			Player ai = new RandomPlayer(i);
			
			System.out.printf("iteration: %d\n", i);

			while (!g.isGameOver()) {
				IdMove aiMove = ai.getMove(a.getPosition());
				if (aiMove == null) break;

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

