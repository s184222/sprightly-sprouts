package sprouts.mvc.game.controller;

import sprouts.mvc.Controller;
import sprouts.mvc.game.model.representation.graphical.Edge;
import sprouts.mvc.game.model.representation.graphical.GameModel;
import sprouts.mvc.game.model.representation.graphical.GraphicalFacade;
import sprouts.mvc.game.model.representation.graphical.Region;
import sprouts.mvc.game.model.representation.graphical.Sprout;
import sprouts.mvc.game.view.GameView;

public class GameController extends Controller {
	
	public GameModel model;
	public GameView view;
	
	// @TODO: reimplement in Sprouts engine.
	
	/*
	public Vector2 mouse;
	
	public GameController() {
		mouse = new Vector2();
	}
	
	@Override
	public void update() {
		GraphicalFacade facade = model.graphicalFacade;
		
		mouse.x = Gdx.input.getX();
		mouse.y = Gdx.input.getY();
		
		if (Gdx.input.isKeyJustPressed(Keys.G)) view.triangles = facade.getTriangles();
		if (Gdx.input.isKeyJustPressed(Keys.R)) model.reset();
		if (Gdx.input.isKeyJustPressed(Keys.S)) {
			for (Region region : facade.regions) {
				System.out.printf("=== Region ===\n");
				System.out.printf("Outer:\n");
				
				facade.verboseTraverse(region.outer);
				
				System.out.printf("Inner (boundaries):\n");
				
				for (Edge inner : region.innerBoundaries) {
					facade.verboseTraverse(inner);
				}
				
				System.out.printf("Inner2 (solo sprouts):\n");
				for (Sprout sprout : region.innerSprouts) {
					System.out.printf("%d ", sprout.id);
				}
				System.out.printf("\n");
			}
			System.out.printf("\n\n\n\n\n");
		}

	}
	
	@Override
	public void touchDown(int screenX, int screenY, int button) {
		Vector3 world = view.viewport.unproject(new Vector3(screenX, screenY, 0));
		model.graphicalFacade.touchDown(world.x, world.y);
	}

	@Override
	public void touchDragged(int screenX, int screenY) {
		Vector3 world = view.viewport.unproject(new Vector3(screenX, screenY, 0));
		model.graphicalFacade.touchDragged(world.x, world.y);
	}
	
	@Override
	public void touchUp(int screenX, int screenY, int button) {
		Vector3 world = view.viewport.unproject(new Vector3(screenX, screenY, 0));
		model.graphicalFacade.touchUp(world.x, world.y);
	}
	*/
}
