package sprouts.game.move.advanced;

import sprouts.game.move.pathfinder.PathFinder;
import sprouts.game.move.pipe.MoveGenerationPipeline;
import sprouts.game.move.triangles.TriangleGenerator;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class AdvanceMoveGenerationPipeline extends MoveGenerationPipeline {

	public AdvanceMoveGenerationPipeline(PathFinder pathFinder, TriangleGenerator triangleGenerator) {
		super(new AdvanceMoveNotationParser(), 
				new AdvancedPreprocessor(), 
				new AdvancedMoveGenerator(pathFinder, triangleGenerator));
	}
}
