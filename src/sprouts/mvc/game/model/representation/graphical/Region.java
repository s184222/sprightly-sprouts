package sprouts.mvc.game.model.representation.graphical;

import java.util.ArrayList;

public class Region {
	public Edge outer;
	public ArrayList<Edge> innerBoundaries = new ArrayList<>();
	public ArrayList<Sprout> innerSprouts = new ArrayList<>();
}