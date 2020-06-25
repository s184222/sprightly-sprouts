package com.sprouts.game.move.advanced;

import com.sprouts.game.move.pathfinder.PathFinder;
import com.sprouts.game.move.pipe.LineGenerationPipeline;
import com.sprouts.game.move.triangles.TriangleGenerator;

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
