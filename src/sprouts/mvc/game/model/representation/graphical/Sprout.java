package sprouts.mvc.game.model.representation.graphical;

import java.util.ArrayList;

public class Sprout {
	public int id;
	public Vertex position;
	public ArrayList<Edge> neighbours = new ArrayList<>(3);
}