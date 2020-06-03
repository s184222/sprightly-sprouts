package com.sprouts;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.io.IOException;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.sprouts.graphic.Display;
import com.sprouts.graphic.DisplaySize;
import com.sprouts.graphic.buffer.VertexArray;
import com.sprouts.graphic.shader.TestShader;
import com.sprouts.graphic.tessellator.BasicTessellator;
import com.sprouts.graphic.texture.Texture;
import com.sprouts.graphic.texture.TextureLoader;
import com.sprouts.input.Keyboard;
import com.sprouts.input.Mouse;
import com.sprouts.math.Mat4;
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

	private static final String SPONGE_BOB_PATH = "/textures/spongebob.png";
	
	private final Display display;
	private final Mouse mouse;
	private final Keyboard keyboard;
	
	private TestShader shader;
	private Texture texture;
	private VertexArray vertexArray;
	
	private Mat4 projMat;
	private Mat4 viewMat;
	private Mat4 modlMat;
	
	private float rot;
	
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
		
		shader.dispose();
		vertexArray.dispose();
		display.dispose();
	}

	private void loadResources() {
		try {
			texture = TextureLoader.loadTexture(SPONGE_BOB_PATH);
		} catch (IOException e) {
			e.printStackTrace();
			
			// TODO: Do something else here.
			System.exit(0);
		}
		
		shader = new TestShader();
		shader.enable();
		shader.setTextureUnit(0);
		shader.disable();
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
	
		vertexArray = new VertexArray();
		
		try (BasicTessellator t = new BasicTessellator()) {
			// FRONT
			t.position( 1.0f, -0.0f,  1.0f).color( 1.0f, -0.0f,  1.0f).texCoord( 1.0f, -0.0f).next();
			t.position(-0.0f,  1.0f,  1.0f).color(-0.0f,  1.0f,  1.0f).texCoord(-0.0f,  1.0f).next();
			t.position(-0.0f, -0.0f,  1.0f).color(-0.0f, -0.0f,  1.0f).texCoord(-0.0f, -0.0f).next();

			t.position( 1.0f, -0.0f,  1.0f).color( 1.0f, -0.0f,  1.0f).texCoord( 1.0f, -0.0f).next();
			t.position( 1.0f,  1.0f,  1.0f).color( 1.0f,  1.0f,  1.0f).texCoord( 1.0f,  1.0f).next();
			t.position(-0.0f,  1.0f,  1.0f).color(-0.0f,  1.0f,  1.0f).texCoord(-0.0f,  1.0f).next();

			// BACK
			t.position(-0.0f, -0.0f, -0.0f).color(-0.0f, -0.0f, -0.0f).texCoord(-0.0f, -0.0f).next();
			t.position( 1.0f,  1.0f, -0.0f).color( 1.0f,  1.0f, -0.0f).texCoord( 1.0f,  1.0f).next();
			t.position( 1.0f, -0.0f, -0.0f).color( 1.0f, -0.0f, -0.0f).texCoord( 1.0f, -0.0f).next();

			t.position(-0.0f, -0.0f, -0.0f).color(-0.0f, -0.0f, -0.0f).texCoord(-0.0f, -0.0f).next();
			t.position(-0.0f,  1.0f, -0.0f).color(-0.0f,  1.0f, -0.0f).texCoord(-0.0f,  1.0f).next();
			t.position( 1.0f,  1.0f, -0.0f).color( 1.0f,  1.0f, -0.0f).texCoord( 1.0f,  1.0f).next();

			// BOTTOM
			t.position(-0.0f, -0.0f,  1.0f).color(-0.0f, -0.0f,  1.0f).texCoord(-0.0f,  1.0f).next();
			t.position( 1.0f, -0.0f, -0.0f).color( 1.0f, -0.0f, -0.0f).texCoord( 1.0f, -0.0f).next();
			t.position( 1.0f, -0.0f,  1.0f).color( 1.0f, -0.0f,  1.0f).texCoord( 1.0f,  1.0f).next();

			t.position(-0.0f, -0.0f,  1.0f).color(-0.0f, -0.0f,  1.0f).texCoord(-0.0f,  1.0f).next();
			t.position(-0.0f, -0.0f, -0.0f).color(-0.0f, -0.0f, -0.0f).texCoord(-0.0f, -0.0f).next();
			t.position( 1.0f, -0.0f, -0.0f).color( 1.0f, -0.0f, -0.0f).texCoord( 1.0f, -0.0f).next();

			// TOP
			t.position(-0.0f,  1.0f, -0.0f).color(-0.0f,  1.0f, -0.0f).texCoord(-0.0f, -0.0f).next();
			t.position( 1.0f,  1.0f,  1.0f).color( 1.0f,  1.0f,  1.0f).texCoord( 1.0f,  1.0f).next();
			t.position( 1.0f,  1.0f, -0.0f).color( 1.0f,  1.0f, -0.0f).texCoord( 1.0f, -0.0f).next();

			t.position(-0.0f,  1.0f, -0.0f).color(-0.0f,  1.0f, -0.0f).texCoord(-0.0f, -0.0f).next();
			t.position(-0.0f,  1.0f,  1.0f).color(-0.0f,  1.0f,  1.0f).texCoord(-0.0f,  1.0f).next();
			t.position( 1.0f,  1.0f,  1.0f).color( 1.0f,  1.0f,  1.0f).texCoord( 1.0f,  1.0f).next();

			// LEFT
			t.position(-0.0f, -0.0f,  1.0f).color(-0.0f, -0.0f,  1.0f).texCoord(-0.0f,  1.0f).next();
			t.position(-0.0f,  1.0f, -0.0f).color(-0.0f,  1.0f, -0.0f).texCoord( 1.0f, -0.0f).next();
			t.position(-0.0f, -0.0f, -0.0f).color(-0.0f, -0.0f, -0.0f).texCoord(-0.0f, -0.0f).next();

			t.position(-0.0f, -0.0f,  1.0f).color(-0.0f, -0.0f,  1.0f).texCoord(-0.0f,  1.0f).next();
			t.position(-0.0f,  1.0f,  1.0f).color(-0.0f,  1.0f,  1.0f).texCoord( 1.0f,  1.0f).next();
			t.position(-0.0f,  1.0f, -0.0f).color(-0.0f,  1.0f, -0.0f).texCoord( 1.0f, -0.0f).next();

			// RIGHT
			t.position( 1.0f, -0.0f, -0.0f).color( 1.0f, -0.0f, -0.0f).texCoord(-0.0f, -0.0f).next();
			t.position( 1.0f,  1.0f,  1.0f).color( 1.0f,  1.0f,  1.0f).texCoord( 1.0f,  1.0f).next();
			t.position( 1.0f, -0.0f,  1.0f).color( 1.0f, -0.0f,  1.0f).texCoord(-0.0f,  1.0f).next();

			t.position( 1.0f, -0.0f, -0.0f).color( 1.0f, -0.0f, -0.0f).texCoord(-0.0f, -0.0f).next();
			t.position( 1.0f,  1.0f, -0.0f).color( 1.0f,  1.0f, -0.0f).texCoord( 1.0f, -0.0f).next();
			t.position( 1.0f,  1.0f,  1.0f).color( 1.0f,  1.0f,  1.0f).texCoord( 1.0f,  1.0f).next();
			
			vertexArray.storeAttributeBuffer(TestShader.POSITION_ATTRIB_INDEX, t.writePositionBuffer());
			vertexArray.storeAttributeBuffer(TestShader.COLOR_ATTRIB_INDEX, t.writeColorBuffer());
			vertexArray.storeAttributeBuffer(TestShader.TEX_COORD_ATTRIB_INDEX, t.writeTexCoordBuffer());
		}

		projMat = new Mat4();
		viewMat = new Mat4();
		modlMat = new Mat4();
	}

	private void onViewportChanged(DisplaySize size) {
		onViewportChanged(size.width, size.height);
	}

	private void onViewportChanged(int width, int height) {
		GL11.glViewport(0, 0, width, height);
		
		// @merge
		MVCContext context = contextManager.getActiveContext();
		context.view.resize(width, height);

		
		projMat.toPerspective(70.0f, (float)width / height, 0.01f, 1000.0f);
		
		shader.enable();
		shader.setProjMat(projMat);
		shader.disable();
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

			
			shader.enable();
			
			viewMat.toIdentity().translate(0.0f, 0.0f, -2.0f);
			shader.setViewMat(viewMat);
			
			rot += 0.1f;
			
			modlMat.toIdentity().rotateX(rot).rotateY(rot).translate(-0.5f, -0.5f, -0.5f);
			shader.setModlMat(modlMat);

			vertexArray.bind();
			GL30.glActiveTexture(GL30.GL_TEXTURE0);
			texture.bind();
			GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 3 * 2 * 6);
			texture.unbind();
			vertexArray.unbind();

			shader.disable();
			
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