package com.sprouts;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;

import com.sprouts.graphic.Display;
import com.sprouts.graphic.DisplaySize;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.font.Font;
import com.sprouts.graphic.font.FontData;
import com.sprouts.graphic.font.FontLoader;
import com.sprouts.graphic.font.TextBounds;
import com.sprouts.graphic.tessellator2d.BatchedTessellator2D;
import com.sprouts.graphic.tessellator2d.color.LinearColorGradient2D;
import com.sprouts.graphic.tessellator2d.shader.BasicTessellator2DShader;
import com.sprouts.graphic.tessellator2d.shader.Tessellator2DShader;
import com.sprouts.graphic.texture.Texture;
import com.sprouts.graphic.texture.TextureLoader;
import com.sprouts.input.Keyboard;
import com.sprouts.input.Mouse;
import com.sprouts.math.Vec2;
import com.sprouts.util.LibUtil;

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
	
	private Tessellator2DShader tessellator2DShader;
	private BatchedTessellator2D batchedTessellator2D;
	private Texture spongeBobTexture;
	private Font arialFont;

	public SproutsMain() {
		display = new Display();
		mouse = new Mouse(display);
		keyboard = new Keyboard(display);
	}
	
	public void run() {
		System.out.println("Using LWJGL version " + Version.getVersion());

		init();
		loop();
		
		display.dispose();
		
		tessellator2DShader.dispose();
		batchedTessellator2D.dispose();
		spongeBobTexture.dispose();
		arialFont.dispose();
	}

	private void loadResources() throws Exception {
		tessellator2DShader = new BasicTessellator2DShader();
		batchedTessellator2D = new BatchedTessellator2D(tessellator2DShader);
		spongeBobTexture = TextureLoader.loadTexture("/textures/spongebob.png");

		FontData arialData = FontLoader.loadFont("/fonts/arial.ttf");
		arialFont = arialData.createFont(200);
	}
	
	private void init() {
		display.initDisplay(WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT);
		display.addDisplayListener(this::onViewportChanged);
		
		mouse.init();
		keyboard.init();

		try {
			loadResources();
		} catch (Exception e) {
			e.printStackTrace();
			
			// TODO: do something else here
			System.exit(1);
		}
	}

	private void onViewportChanged(DisplaySize size) {
		onViewportChanged(size.width, size.height);
	}

	private void onViewportChanged(int width, int height) {
		GL11.glViewport(0, 0, width, height);
		
		batchedTessellator2D.setViewport(0, 0, width, height);
	}
	
	private void loop() {
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		onViewportChanged(display.getDisplaySize());

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		while (!display.isCloseRequested()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			render();
			
			checkGLErrors();
			
			display.update();
		}
	}
	
	private void render() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		batchedTessellator2D.beginBatch();
		batchedTessellator2D.setColor(VertexColor.WHITE);
		TextBounds textBounds = arialFont.getTextBounds("the brown fox");
		
		batchedTessellator2D.setColor(VertexColor.ORANGE);
		batchedTessellator2D.drawQuad(0, 0, textBounds.width, textBounds.height);
		
		arialFont.drawString(batchedTessellator2D, textBounds.x, textBounds.y, "the brown fox");
		
		batchedTessellator2D.translate(200.0f, 200.0f);
		batchedTessellator2D.setColorGradient(new LinearColorGradient2D(new Vec2(0.0f, 0.0f), VertexColor.WHITE, new Vec2(0.0f, 400.0f), VertexColor.PURPLE));
		batchedTessellator2D.setTextureRegion(spongeBobTexture);
		batchedTessellator2D.drawQuad(0.0f, 0.0f, 400.0f, 400.0f);
		batchedTessellator2D.translate(-200.0f, -200.0f);
		
		batchedTessellator2D.clearMaterial();
		batchedTessellator2D.setColor(VertexColor.BLUE_VIOLET);
		batchedTessellator2D.drawLine(50.0f, 150.0f, 150.0f, 250.0f, 2.0f);
		batchedTessellator2D.setColor(VertexColor.CHOCOLATE);
		batchedTessellator2D.drawLine(150.0f, 150.0f, 50.0f, 250.0f, 2.0f);
		
		batchedTessellator2D.endBatch();
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