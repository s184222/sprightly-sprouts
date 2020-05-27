package sprouts.mvc.game.model.representation.graphical;

import sprouts.mvc.Model;

public class GameModel extends Model {
	
	public GraphicalFacade graphicalFacade;
	
	public void create () {
		reset();
	}
	
	public void enter () {}
	public void update (double dt) {}

	public void reset() {
		graphicalFacade = new GraphicalFacade();
	}
}
