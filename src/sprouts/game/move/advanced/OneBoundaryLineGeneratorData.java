package sprouts.game.move.advanced;

import java.util.List;
import java.util.Map;

import sprouts.game.move.triangles.Triangle;

/**
 * @author Rasmus Møller Larsen, s184190
 * 
 */
public class OneBoundaryLineGeneratorData {
	public Map<Triangle, List<Triangle>> oneBoundaryGraph;
	public List<Triangle> slither;
	public List<Triangle> wrapper;
	public List<Triangle> triangles;
}
