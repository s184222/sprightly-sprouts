package sprouts.game.move.advanced;

import sprouts.game.move.pathfinder.PathFinder;
import sprouts.game.move.pipe.MoveGenerationPipeline;
import sprouts.game.move.triangles.TriangleGenerator;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class AdvancedMoveGenerationPipeline extends MoveGenerationPipeline {

	public AdvancedMoveGenerationPipeline(PathFinder pathFinder, TriangleGenerator triangleGenerator) {
		super(new AdvancedMoveNotationParser(), 
				new AdvancedPreprocessor(), 
				new AdvancedMoveGenerator(pathFinder, triangleGenerator));
	}
}
