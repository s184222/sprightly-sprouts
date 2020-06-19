package sprouts.game.model.move.notation;

import sprouts.game.model.move.MoveNotationException;
import sprouts.game.model.move.RawMove;

public interface MoveNotationParser {
	public RawMove parse(String rawMove) throws MoveNotationException;
}
