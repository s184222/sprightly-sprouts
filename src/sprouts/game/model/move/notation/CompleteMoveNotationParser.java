package sprouts.game.model.move.notation;

import java.util.ArrayList;
import java.util.List;

import sprouts.game.model.move.MoveNotationException;
import sprouts.game.model.move.RawMove;

public class CompleteMoveNotationParser implements MoveNotationParser {
	
	private List<MoveNotationParser> parsers;
	
	public CompleteMoveNotationParser() {
		parsers = new ArrayList<>();
		parsers.add(new SimpleMoveNotationParser());
		parsers.add(new AdvancedMoveNotationParser());
	}

	@Override
	public RawMove parse(String rawMove) throws MoveNotationException {
		
		for (MoveNotationParser parser : parsers) {
			try {
				RawMove move = parser.parse(rawMove);
				return move;
			} catch (MoveNotationException e) {
			}
		}
		
		throw new MoveNotationException("could not parse notation");
	}
}