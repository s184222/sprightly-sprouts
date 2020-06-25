package com.sprouts.game.move.pipe;

import com.sprouts.game.model.Position;
import com.sprouts.game.move.Move;
import com.sprouts.game.move.MoveException;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public interface LineGenerator {
	
	/**
	 * Generates a line which satisfies the {@code move}
	 * 
	 * @param move - request
	 * @param position - the current position of the game
	 * @return the line and some optional custom data
	 * @throws MoveException if no line satisfies the move
	 */
	public LinePathResult generate(Move move, Position position) throws MoveException;
}
