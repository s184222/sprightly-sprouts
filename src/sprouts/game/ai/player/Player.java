package sprouts.game.ai.player;

import sprouts.game.ai.Position;
import sprouts.game.model.move.RawMove;

public interface Player {
	public RawMove getMove(Position position);
}
