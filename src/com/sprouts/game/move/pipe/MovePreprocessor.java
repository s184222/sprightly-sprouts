package com.sprouts.game.move.pipe;

import com.sprouts.game.model.Position;
import com.sprouts.game.move.IdMove;
import com.sprouts.game.move.Move;
import com.sprouts.game.move.MoveException;

/**
 * 
 * @author Rasmus M�ller Larsen, s184190
 *
 */
public interface MovePreprocessor {
	
	/**
	 * 
	 * Converts an {@link IdMove} into a {@link Move}.
	 * 
	 * @param idMove
	 * @param position - current position of the game
	 * @return processed move
	 * @throws MoveException if the move is not possible to make
	 */
	public Move process(IdMove idMove, Position position) throws MoveException;
}
