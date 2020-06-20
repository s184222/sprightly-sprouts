package sprouts.game.move.advanced;

import java.util.List;
import java.util.Map;

import sprouts.game.move.triangles.Triangle;

public class OneBoundaryMoveGeneratorData {
	public Map<Triangle, List<Triangle>> oneBoundaryGraph;
	public List<Triangle> slither;
	public List<Triangle> wrapper;
	public List<Triangle> triangles;
	public List<Triangle> condense;
}