package sprouts.game.model.move;

import java.util.List;

import sprouts.game.model.Edge;
import sprouts.game.model.Region;
import sprouts.game.model.Sprout;

public class Move {
	
	public Sprout from;
	public Sprout to;
	
	public List<Sprout> inners;
	
	public Edge fromEdge;
	public Edge toEdge;
	
	public Region region;

}
