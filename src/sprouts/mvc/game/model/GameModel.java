package sprouts.mvc.game.model;

import sprouts.mvc.Model;

public class GameModel extends Model {
	
	public GraphicalFacade graphicalFacade;
	
	public void create () {
		reset();
	}
	
	public void enter () {}
	public void update (double dt) {}

	public void reset() {
		DebugIdGenerators.reset();
		graphicalFacade = new GraphicalFacade();
	}
}
