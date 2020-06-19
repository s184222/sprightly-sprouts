package sprouts.game.move.simple;

import sprouts.game.move.pathfinder.PathFinder;
import sprouts.game.move.pipe.MoveGenerationPipeline;
import sprouts.game.move.triangles.TriangleGenerator;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class SimpleMoveGenerationPipeline extends MoveGenerationPipeline {
	
	public SimpleMoveGenerationPipeline(PathFinder pathFinder, TriangleGenerator triangleGenerator) {
		super(new SimpleMoveNotationParser(), 
				new SimpleMovePreprocessor(), 
				new SimpleMoveGenerator(pathFinder, triangleGenerator));
	}
}
