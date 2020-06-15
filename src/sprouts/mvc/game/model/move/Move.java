package sprouts.mvc.game.model.move;

import java.util.List;

import sprouts.mvc.game.model.Edge;
import sprouts.mvc.game.model.Region;
import sprouts.mvc.game.model.Sprout;

public class Move {
	
	public Sprout from;
	public Sprout to;
	
	public List<Sprout> inners;
	
	public Edge fromEdge;
	public Edge toEdge;
	
	public Region region;

}
