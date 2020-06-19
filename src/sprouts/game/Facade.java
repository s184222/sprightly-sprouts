package sprouts.game;

import sprouts.ai.AbstractFacade;

public class Facade {
	
	private AbstractFacade abstractFacade;
	private GraphicalFacade graphicalFacade;
	
	public Facade() {
		abstractFacade = new AbstractFacade();
		graphicalFacade = new GraphicalFacade();
	}
	
	public void createGame(int numberOfSprouts) {
		abstractFacade.createFreshPosition(numberOfSprouts);
		graphicalFacade.createFreshPosition(numberOfSprouts);
	}

}
