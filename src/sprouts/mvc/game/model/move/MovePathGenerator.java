package sprouts.mvc.game.model.move;

import sprouts.mvc.game.model.Position;

public interface MovePathGenerator {
	public MovePathResult generate(Move move, Position position) throws MoveException;
}
