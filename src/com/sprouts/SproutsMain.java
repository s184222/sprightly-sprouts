package com.sprouts;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;

import com.sprouts.graphic.Display;
import com.sprouts.graphic.DisplaySize;
import com.sprouts.input.Keyboard;
import com.sprouts.input.Mouse;
import com.sprouts.util.LibUtil;

import sprouts.mvc.ContextManager;
import sprouts.mvc.MVCContext;
import sprouts.mvc.game.controller.GameController;
import sprouts.mvc.game.model.representation.graphical.GameModel;
import sprouts.mvc.game.view.GameView;

public class SproutsMain {

	static {
		LibUtil.loadNatives();
	}
	
	private static final String WINDOW_TITLE = "Sprightly Sprouts";
	private static final int WINDOW_WIDTH  = 500;
	private static final int WINDOW_HEIGHT = 500;

	private final Display display;
	private final Mouse mouse;
	private final Keyboard keyboard;
	
	private ContextManager contextManager;
	
	public SproutsMain() {
		display = new Display();
		mouse = new Mouse(display);
		keyboard = new Keyboard(display);
		
		// @merge
		contextManager = new ContextManager();
		
		GameModel gameModel = new GameModel();
		GameView gameView = new GameView();
		GameController gameController = new GameController();
		
		gameController.model = gameModel;
		gameController.view = gameView;
		
		gameView.controller = gameController;
		gameView.model = gameModel;
		
		contextManager.addMVCContext("game", gameModel, gameView, gameController);
		contextManager.setActiveContext("game");
	}
	
	public void run() {
		System.out.println("Using LWJGL version " + Version.getVersion());

		init();
		loop();
		
		display.dispose();
	}

	private void loadResources() {
	}
	
	private void init() {
		display.initDisplay(WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT);
		display.addDisplayListener(this::onViewportChanged);
		
		mouse.init();
		keyboard.init();

		// @merge
		/*
		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			MVCContext context = contextManager.getActiveContext();
			context.controller.touchDown(screenX, screenY, button);
		}
		
		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			MVCContext context = contextManager.getActiveContext();
			context.controller.touchUp(screenX, screenY, button);
		}
		
		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			MVCContext context = contextManager.getActiveContext();
			context.controller.touchDragged(screenX, screenY);
		}
		*/
		
		loadResources();
	
	}

	private void onViewportChanged(DisplaySize size) {
		onViewportChanged(size.width, size.height);
	}

	private void onViewportChanged(int width, int height) {
		GL11.glViewport(0, 0, width, height);
		
		// @merge
		MVCContext context = contextManager.getActiveContext();
		context.view.resize(width, height);
	}
	
	private void loop() {
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		onViewportChanged(display.getDisplaySize());

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		while (!display.isCloseRequested()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			// @merge
			MVCContext context = contextManager.getActiveContext();
			context.controller.update();
			context.view.draw();

			checkGLErrors();
			
			display.update();
		}
	}
	
	private void checkGLErrors() {
		int err;
		while ((err = GL11.glGetError()) != GL11.GL_NO_ERROR) {
			System.err.println("OpenGL error: " + err);
		}
	}
	
	public Mouse getMouse() {
		return mouse;
	}

	public Keyboard getKeyboard() {
		return keyboard;
	}

	public static void main(String[] args) {
		new SproutsMain().run();
	}
}