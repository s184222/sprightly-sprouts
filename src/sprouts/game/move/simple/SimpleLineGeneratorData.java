package sprouts.game.move.simple;

import java.util.List;
import java.util.Map;

import sprouts.game.model.Vertex;
import sprouts.game.move.triangles.Triangle;

public class SimpleLineGeneratorData {
	
	public List<Triangle> triangles;
	public Map<Vertex, List<Vertex>> twoBoundaryGraph;

}
