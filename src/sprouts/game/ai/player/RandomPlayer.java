package sprouts.game.ai.player;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import sprouts.game.ai.Position;
import sprouts.game.model.move.RawMove;

public class RandomPlayer implements Player {
	
	private Random random;
	private int seed;
	
	public RandomPlayer() {
		seed = 444;
		random = new Random(seed);
	}

	@Override
	public RawMove getMove(Position position) {
		List<RawMove> moves = position.getAllMoves();
		
		System.out.printf("moves: %d\n", moves.size());

		/*
		System.out.printf("=== moves ===\n");
		
		for (RawMove move : moves) {
			System.out.printf("%s\n", move);
		}
		System.out.printf("\n");
		*/
		
		
		Collections.shuffle(moves, random);
		
		RawMove move = moves.get(0);
		
		return move;
	}
}
