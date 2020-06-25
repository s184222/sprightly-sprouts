package com.sprouts.game.move.simple;

import com.sprouts.game.move.pathfinder.PathFinder;
import com.sprouts.game.move.pipe.LineGenerationPipeline;
import com.sprouts.game.move.triangles.TriangleGenerator;

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
