package sprouts.game.move.pipe;

import sprouts.game.model.Line;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class LinePathResult {
	
	public Line line;
	
	// Optional fields, which the move generator can use, e.g. for visualizing the move generation algorithm.
	public String generatorType;
	public Object customData;
	
}