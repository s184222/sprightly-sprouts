package sprouts.game.ai.player;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import sprouts.game.ai.Position;
import sprouts.game.model.move.RawMove;

public class RandomPlayer implements Player {
	
	private Random random;
	
	public RandomPlayer() {
		this(6);	//444
	}
	
	public RandomPlayer(int seed) {
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
		
		
		if (moves.size() == 0) return null;

		Collections.shuffle(moves, random);
		
		RawMove move = moves.get(0);
		
		return move;
	}
}
