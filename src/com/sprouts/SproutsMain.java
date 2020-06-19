package com.sprouts;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glViewport;

import java.io.IOException;

import org.lwjgl.Version;

import com.sprouts.composition.CompositionContext;
import com.sprouts.graphic.Display;
import com.sprouts.graphic.DisplaySize;
import com.sprouts.graphic.buffer.FrameBuffer;
import com.sprouts.graphic.buffer.FrameBufferType;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.post.PostManager;
import com.sprouts.graphic.tessellator2d.BatchedTessellator2D;
import com.sprouts.graphic.tessellator2d.shader.BasicTessellator2DShader;
import com.sprouts.graphic.tessellator2d.shader.Tessellator2DShader;
import com.sprouts.graphic.texture.ITextureRegion;
import com.sprouts.graphic.texture.Texture;
import com.sprouts.graphic.texture.TextureLoader;
import com.sprouts.input.Keyboard;
import com.sprouts.input.Mouse;
import com.sprouts.menu.MainSproutsMenu;
import com.sprouts.menu.SproutsMenu;
import com.sprouts.util.LibUtil;

public class SproutsMain {

	static {
		LibUtil.loadNatives();
	}
	
	private static final String SPROUTS_TITLE = "Sprightly Sprouts";
	
	private static final String WINDOW_TITLE = SPROUTS_TITLE;
	private static final int WINDOW_WIDTH  = 500;
	private static final int WINDOW_HEIGHT = 500;

	private final Display display;
	private final Mouse mouse;
	private final Keyboard keyboard;
	
	private Texture menuBackground;
	
	private FrameBuffer frameBuffer;
	private PostManager postManager;
	
	private Tessellator2DShader shader;
	private BatchedTessellator2D tessellator;
	
	private SproutsMenu menu;
	
	private boolean running;
	
	public SproutsMain() {
		display = new Display();
		mouse = new Mouse(display);
		keyboard = new Keyboard(display);
	}
	
	public void run() {
		System.out.println("Using LWJGL version " + Version.getVersion());

		init();
		loop();
		
		menuBackground.dispose();

		frameBuffer.dispose();
		postManager.dispose();

		shader.dispose();
		tessellator.dispose();
		
		display.dispose();
	}

	private void init() {
		display.initDisplay(WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT);
		display.addDisplayListener(this::onViewportChanged);
		
		mouse.init();
		keyboard.init();

		try {
			loadResources();
		} catch (IOException e) {
			e.printStackTrace();

			System.exit(-1);
		}

		CompositionContext.init(display, keyboard, mouse);
		
		setMenu(new MainSproutsMenu(this));
	}

	private void loadResources() throws IOException {
		menuBackground = TextureLoader.loadTexture("/textures/forest_background.png");
		
		frameBuffer = new FrameBuffer(FrameBufferType.DEPTH_AND_TEXTURE, 0, 0);
		postManager = new PostManager();
		
		shader = new BasicTessellator2DShader();
		tessellator = new BatchedTessellator2D(shader);
	}
	
	public void setMenu(SproutsMenu menu) {
		CompositionContext.setContent(menu);
	
		this.menu = menu;
	}

	private void onViewportChanged(DisplaySize size) {
		onViewportChanged(size.width, size.height);
	}

	private void onViewportChanged(int width, int height) {
		frameBuffer.setSize(width, height);
		postManager.setSize(width, height);
		
		tessellator.setViewport(0.0f, 0.0f, width, height);
	}
	
	private void loop() {
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		onViewportChanged(display.getDisplaySize());

		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glEnable(GL_DEPTH_TEST);
		
		running = true;
		
		while (running && !display.isCloseRequested()) {
			if (menu != null)
				menu.update();
			
			render();
			
			CompositionContext.draw();
			
			checkGLErrors();
			
			display.update();
		}
	}
	
	private void render() {
		frameBuffer.bind();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		draw();
		
		frameBuffer.unbind();
		postManager.process(frameBuffer);
		
		DisplaySize size = display.getDisplaySize();
		glViewport(0, 0, size.width, size.height);
		
		tessellator.beginBatch();
		tessellator.setColor(VertexColor.WHITE);
		tessellator.setTextureRegion(frameBuffer.getColorTexture());
		tessellator.drawQuad(0, 0, size.width, size.height);
		tessellator.endBatch();
	}
	
	private void draw() {
		DisplaySize size = display.getDisplaySize();
		if (size.height > 0) {
			float aspect = (float)size.width / size.height;

			tessellator.beginBatch();
			
			ITextureRegion region = menuBackground.getWithAspect(aspect);
			
			tessellator.setColor(VertexColor.WHITE);
			tessellator.setTextureRegion(region);
			tessellator.drawQuad(0.0f, 0.0f, size.width, size.height);
			
			tessellator.endBatch();
		}
	}
	
	private void checkGLErrors() {
		int err;
		while ((err = glGetError()) != GL_NO_ERROR) {
			System.err.println("OpenGL error: " + err);
		}
	}
	
	public Mouse getMouse() {
		return mouse;
	}

	public Keyboard getKeyboard() {
		return keyboard;
	}
	
	public Texture getPostTexture() {
		return postManager.getOutputTexture();
	}

	public void stop() {
		running = false;
	}
	
	public static void main(String[] args) {
		new SproutsMain().run();
	}
}