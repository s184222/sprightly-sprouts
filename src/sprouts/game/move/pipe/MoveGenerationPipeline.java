package sprouts.game.move.pipe;

import sprouts.game.model.Position;
import sprouts.game.move.IdMove;
import sprouts.game.move.Move;
import sprouts.game.move.MoveException;
import sprouts.game.move.MoveNotationException;

/**
 * 
 * To generate a line from a move request a sequence of transformations occurs.
 * The move string is parsed, the move is processed and finally the line which satisfies the move is generated.
 * MoveGenerationPipeline unites these 3 transformations.
 * 
 * @see MoveNotationParser
 * @see MovePreprocessor
 * @see MovePathGenerator
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class MoveGenerationPipeline {
	
	private MoveNotationParser parser;
	private MovePreprocessor preprocessor;
	private MovePathGenerator generator;
	
	public MoveGenerationPipeline(MoveNotationParser parser, MovePreprocessor preprocessor, MovePathGenerator generator) {
		this.parser = parser;
		this.preprocessor = preprocessor;
		this.generator = generator;
	}
	
	public MovePathResult process(String rawMove, Position position) throws MoveNotationException, MoveException {
		IdMove idMove = parser.parse(rawMove);
		Move move = preprocessor.process(idMove, position);
		MovePathResult result = generator.generate(move, position);
		return result;
	}
}
