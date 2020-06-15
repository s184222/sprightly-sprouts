package sprouts.mvc.game.model.move.two;

import java.util.List;
import java.util.Map;

import sprouts.mvc.game.model.Vertex;
import sprouts.mvc.game.model.move.Triangle;

public class TwoBoundaryMoveGeneratorData {
	
	public List<Triangle> triangles;
	public Map<Vertex, List<Vertex>> twoBoundaryGraph;

}
