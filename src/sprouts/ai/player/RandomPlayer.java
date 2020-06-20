package sprouts.ai.player;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import sprouts.ai.AllMoveGenerator;
import sprouts.ai.Position;
import sprouts.game.move.IdMove;

public class RandomPlayer implements Player {
	
	private Random random;
	private AllMoveGenerator generator;
	
	private IdMove previousMove;
	
	public RandomPlayer() {
		this(6);
	}
	
	public RandomPlayer(int seed) {
		random = new Random(seed);
	}

	@Override
	public IdMove getMove(Position position) {
		List<IdMove> moves = generator.getAllMoves(position);
		
		if (moves.size() == 0) return null;

		Collections.shuffle(moves, random);
		
		IdMove move = moves.get(0);
		if (move.equals(previousMove)) return null;
		
		return move;
	}
}
