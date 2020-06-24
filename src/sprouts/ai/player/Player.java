package sprouts.ai.player;

import sprouts.ai.Position;
import sprouts.game.move.IdMove;

/**
 * 
 * @author Rasmus Møller Larsen
 *
 */

public interface Player {
	public IdMove getMove(Position position);
}
