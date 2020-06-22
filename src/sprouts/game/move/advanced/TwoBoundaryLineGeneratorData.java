package sprouts.game.move.advanced;

import java.util.List;
import java.util.Map;

import sprouts.game.model.Vertex;
import sprouts.game.move.triangles.Triangle;

/**
 * @author Rasmus Møller Larsen, s184190
 * 
 */
public class TwoBoundaryLineGeneratorData {
	
	public List<Triangle> triangles;
	public Map<Vertex, List<Vertex>> twoBoundaryGraph;

}
