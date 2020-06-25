package com.sprouts.ai.player;

import com.sprouts.ai.Position;
import com.sprouts.game.move.IdMove;

/**
 * 
 * @author Rasmus Møller Larsen
 *
 */

public interface Player {
	public IdMove getMove(Position position);
}
