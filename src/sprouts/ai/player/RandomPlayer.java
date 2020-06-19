package sprouts.ai.player;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import sprouts.ai.Position;
import sprouts.game.move.IdMove;

public class RandomPlayer implements Player {
	
	private Random random;
	
	public RandomPlayer() {
		this(6);	//444
	}
	
	public RandomPlayer(int seed) {
		random = new Random(seed);
	}

	@Override
	public IdMove getMove(Position position) {
		List<IdMove> moves = position.getAllMoves();
		
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
		
		IdMove move = moves.get(0);
		
		return move;
	}
}
