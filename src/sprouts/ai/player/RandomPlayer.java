package sprouts.ai.player;

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
		generator = new AllMoveGenerator();
	}

	@Override
	public IdMove getMove(Position position) {
		List<IdMove> moves = generator.getAllMoves(position);
		
		if (moves.size() == 0) return null;

		//Collections.shuffle(moves, random);
		
		IdMove move = moves.get(1);
		if (move.equals(previousMove)) return null;
		
		return move;
	}
}
