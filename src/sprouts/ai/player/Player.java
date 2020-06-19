package sprouts.ai.player;

import sprouts.ai.Position;
import sprouts.game.move.IdMove;

public interface Player {
	public IdMove getMove(Position position);
}
