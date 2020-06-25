package com.sprouts.game.move.pipe;

import com.sprouts.game.model.Position;
import com.sprouts.game.move.IdMove;
import com.sprouts.game.move.Move;
import com.sprouts.game.move.MoveException;
import com.sprouts.game.move.MoveNotationException;

/**
 * 
 * To generate a line from a move request a sequence of transformations occurs.
 * The move string is parsed, the move is processed and finally the line which satisfies the move is generated.
 * MoveGenerationPipeline unites these 3 transformations.
 * 
 * @see MoveNotationParser
 * @see MovePreprocessor
 * @see LineGenerator
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class LineGenerationPipeline {
	
	private MoveNotationParser parser;
	private MovePreprocessor preprocessor;
	private LineGenerator generator;
	
	public LineGenerationPipeline(MoveNotationParser parser, MovePreprocessor preprocessor, LineGenerator generator) {
		this.parser = parser;
		this.preprocessor = preprocessor;
		this.generator = generator;
	}
	
	public LinePathResult process(String rawMove, Position position) throws MoveNotationException, MoveException {
		IdMove idMove = parser.parse(rawMove);
		Move move = preprocessor.process(idMove, position);
		LinePathResult result = generator.generate(move, position);
		return result;
	}
}
