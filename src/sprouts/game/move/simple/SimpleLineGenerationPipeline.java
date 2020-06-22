package sprouts.game.move.simple;

import sprouts.game.move.pathfinder.PathFinder;
import sprouts.game.move.pipe.LineGenerationPipeline;
import sprouts.game.move.triangles.TriangleGenerator;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class SimpleLineGenerationPipeline extends LineGenerationPipeline {
	
	public SimpleLineGenerationPipeline(PathFinder pathFinder, TriangleGenerator triangleGenerator) {
		super(new SimpleMoveNotationParser(), 
				new SimpleMovePreprocessor(), 
				new SimpleLineGenerator(pathFinder, triangleGenerator));
	}
}
