package sprouts.game.model.move.generators;

import sprouts.game.model.Position;
import sprouts.game.model.move.Move;
import sprouts.game.model.move.MoveException;

public interface MovePathGenerator {
	public MovePathResult generate(Move move, Position position) throws MoveException;
}
