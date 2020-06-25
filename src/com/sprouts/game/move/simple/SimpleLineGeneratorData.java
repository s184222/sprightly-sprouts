package com.sprouts.game.move.simple;

import java.util.List;
import java.util.Map;

import com.sprouts.game.model.Vertex;
import com.sprouts.game.move.triangles.Triangle;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */

public class SimpleLineGeneratorData {
	
	public List<Triangle> triangles;
	public Map<Vertex, List<Vertex>> twoBoundaryGraph;

}
