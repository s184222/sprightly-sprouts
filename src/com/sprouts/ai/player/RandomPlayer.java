package com.sprouts.ai.player;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.sprouts.ai.AllMoveGenerator;
import com.sprouts.ai.Position;
import com.sprouts.game.move.IdMove;

/**
 * 
 * @author Rasmus M�ller Larsen
 *
 */

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

		Collections.shuffle(moves, random);
		
		IdMove move = moves.get(0);
		if (move.equals(previousMove)) return null;
		
		return move;
	}
}
