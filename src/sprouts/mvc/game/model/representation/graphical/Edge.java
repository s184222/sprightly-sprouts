package sprouts.mvc.game.model.representation.graphical;

public class Edge {
	
	public Sprout origin;

	public Region region;

	public Edge next;
	public Edge prev;
	
	public Edge representative;
	public Edge twin;
	
	public int id;
	
	public Line line;
}