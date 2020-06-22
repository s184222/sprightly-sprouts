package sprouts.game.move.advanced;

import sprouts.game.move.pathfinder.PathFinder;
import sprouts.game.move.pipe.LineGenerationPipeline;
import sprouts.game.move.triangles.TriangleGenerator;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class AdvancedLineGenerationPipeline extends LineGenerationPipeline {

	public AdvancedLineGenerationPipeline(PathFinder pathFinder, TriangleGenerator triangleGenerator) {
		super(new AdvancedMoveNotationParser(), 
				new AdvancedPreprocessor(), 
				new AdvancedLineGenerator(pathFinder, triangleGenerator));
	}
}
