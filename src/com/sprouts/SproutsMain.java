package com.sprouts;

import static org.lwjgl.opengl.GL11.GL_BACK;
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
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.lwjgl.Version;

import com.sprouts.composition.CompositionContext;
import com.sprouts.graphic.Display;
import com.sprouts.graphic.DisplaySize;
import com.sprouts.graphic.buffer.FrameBuffer;
import com.sprouts.graphic.buffer.FrameBufferType;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.obj.ObjData;
import com.sprouts.graphic.obj.ObjLoader;
import com.sprouts.graphic.obj.shader.BasicObjShader;
import com.sprouts.graphic.obj.shader.ObjShader;
import com.sprouts.graphic.post.PostManager;
import com.sprouts.graphic.tessellator2d.BatchedTessellator2D;
import com.sprouts.graphic.tessellator2d.shader.BasicTessellator2DShader;
import com.sprouts.graphic.tessellator2d.shader.Tessellator2DShader;
import com.sprouts.graphic.texture.ITextureRegion;
import com.sprouts.graphic.texture.Texture;
import com.sprouts.graphic.texture.TextureLoader;
import com.sprouts.input.Keyboard;
import com.sprouts.input.Mouse;
import com.sprouts.math.LinMath;
import com.sprouts.math.Mat4;
import com.sprouts.menu.MainSproutsMenu;
import com.sprouts.menu.SproutsMenu;
import com.sprouts.util.LibUtil;

public class SproutsMain {

	static {
		LibUtil.loadNatives();
	}
	
	private static final String SPROUTS_TITLE = "Sprightly Sprouts";
	
	private static final String WINDOW_TITLE = SPROUTS_TITLE;
	private static final int WINDOW_WIDTH  = 900;
	private static final int WINDOW_HEIGHT = 600;

	private static final float PI2 = (float)(2.0 * Math.PI);
	private static final float OBJ_FOV = 40.0f * PI2 / 360.0f;
	private static final int NUM_SPROUTS = 400;
	
	private static final float VIEW_RANGE = 0.25f * (float)Math.PI;
	
	private final Display display;
	private final Mouse mouse;
	private final Keyboard keyboard;
	
	private Texture menuBackground;
	private Texture flowerTextureAtlas;
	private ITextureRegion[] flowerTextures;
	
	private FrameBuffer targetFrameBuffer;
	private FrameBuffer resolvedFrameBuffer;
	private PostManager postManager;
	
	private Tessellator2DShader shader;
	private BatchedTessellator2D tessellator;
	
	private ObjShader objShader;
	private ObjData groundObj;
	private ObjData grassObj;
	private float groundRot;
	private List<ObjData> sprouts;
	
	private final float[] radOffsetX = new float[NUM_SPROUTS];
	private final float[] radOffsetY = new float[NUM_SPROUTS];
	private final float[] xOffset = new float[NUM_SPROUTS];
	
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
		flowerTextureAtlas.dispose();
		
		targetFrameBuffer.dispose();
		resolvedFrameBuffer.dispose();
		postManager.dispose();

		shader.dispose();
		tessellator.dispose();
		
		objShader.dispose();
		
		for (ObjData sprout : sprouts) {
			sprout.getTexture().dispose();
			sprout.dispose();
		}
		
		groundObj.getTexture().dispose();
		groundObj.dispose();
		
		grassObj.getTexture().dispose();
		grassObj.dispose();
		
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
		menuBackground = TextureLoader.loadTexture("/textures/background.png");
		flowerTextureAtlas = TextureLoader.loadTexture("/textures/flowers.png");
		flowerTextures = new ITextureRegion[4];
		for (int i = 0; i < 4; i++) {
			float u = 0.25f * i;
			flowerTextures[i] = flowerTextureAtlas.getRegion(u, 0.0f, u + 0.25f, 1.0f);
		}
		
		targetFrameBuffer = new FrameBuffer(FrameBufferType.MULTISAMPLED_DEPTH_AND_COLOR, 0, 0);
		resolvedFrameBuffer = new FrameBuffer(FrameBufferType.DEPTH_AND_COLOR, 0, 0);
		postManager = new PostManager();
		
		shader = new BasicTessellator2DShader();
		tessellator = new BatchedTessellator2D(shader);
		
		objShader = new BasicObjShader();
		objShader.enable();
		objShader.setTextureSampler(0);
		
		sprouts = new ArrayList<ObjData>();
		
		for (int i = 0; i < 4; i++) {
			String objPath = String.format(Locale.ENGLISH, "/models/sproutStage%d.obj", i);
			String texPath = String.format(Locale.ENGLISH, "/textures/sproutStage%dTexture.png", i);
			
			ObjData sproutStage = ObjLoader.loadObj(objPath);
			sproutStage.initBuffers(objShader);
			Texture sproutStageTexture = TextureLoader.loadTexture(texPath);
			sproutStage.setTexture(sproutStageTexture);
			sprouts.add(sproutStage);
		}
		
		Random random = new Random();
		
		for(int i = 0; i < NUM_SPROUTS; i++) {
			radOffsetX[i] = random.nextFloat() * ((float) (Math.PI) * 2);
			radOffsetY[i] = random.nextFloat() * ((float) (Math.PI) * 2);
			xOffset[i] = random.nextFloat() * 35.0f - 17.5f;
		}
		
		groundObj = ObjLoader.loadObj("/models/ground.obj");
		groundObj.initBuffers(objShader);
		groundObj.setTexture(TextureLoader.loadTexture("/textures/groundTexture.png"));
		
		grassObj = ObjLoader.loadObj("/models/grass.obj");
		grassObj.initBuffers(objShader);
		grassObj.setTexture(TextureLoader.loadTexture("/textures/grassTexture.png"));
	}
	
	public void setMenu(SproutsMenu menu) {
		CompositionContext.setContent(menu);
	
		this.menu = menu;
	}

	private void onViewportChanged(DisplaySize size) {
		onViewportChanged(size.width, size.height);
	}

	private void onViewportChanged(int width, int height) {
		targetFrameBuffer.setSize(width, height);
		resolvedFrameBuffer.setSize(width, height);
		postManager.setSize(width, height);
		
		tessellator.setViewport(0.0f, 0.0f, width, height);
		
		objShader.enable();
		objShader.setProjMat(new Mat4().toPerspective(OBJ_FOV, (float)width / height, 0.01f, 100.0f));
	}
	
	private void loop() {
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		onViewportChanged(display.getDisplaySize());

		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_MULTISAMPLE);
		
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
		targetFrameBuffer.bind();
		glClear(GL_DEPTH_BUFFER_BIT);

		draw();
		
		targetFrameBuffer.unbind();
		targetFrameBuffer.resolve(resolvedFrameBuffer);
		
		postManager.process(resolvedFrameBuffer);
		
		DisplaySize size = display.getDisplaySize();
		glViewport(0, 0, size.width, size.height);
		
		tessellator.beginBatch();
		tessellator.setColor(VertexColor.WHITE);
		tessellator.setTextureRegion(resolvedFrameBuffer.getColorTexture());
		tessellator.drawQuad(0, 0, size.width, size.height);
		tessellator.endBatch();
	}
	
	private void draw() {
		if (menu != null && menu.isSimpleBackground()) {
			drawSimpleBackground();
		} else {
			drawComplexBackground();
		}
		
		if (menu != null)
			menu.drawBackground(tessellator);
	}
	
	private void drawSimpleBackground() {
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
	
	private void drawComplexBackground() {
		objShader.enable();

		groundRot += 0.0005f;
		if (groundRot >= PI2)
			groundRot %= PI2;

		Mat4 viewMat = new Mat4();
		Mat4 modlMat = new Mat4();

		viewMat.translate(0f, -16.0f, -25.0f);
		viewMat.rotateX(groundRot);

		objShader.setViewMat(viewMat);
		objShader.setModlMat(modlMat);
		
		groundObj.drawBuffer();
		
		for (int i = 0; i < NUM_SPROUTS; i++) {
			float dr = (groundRot + radOffsetX[i] + 0.87f * PI2 + 0.5f * VIEW_RANGE) % PI2;
			if (dr < VIEW_RANGE) {
				modlMat.toIdentity();
				modlMat.rotateX(radOffsetX[i]);
				modlMat.translate(xOffset[i], 24.0f, 0);
				modlMat.rotateY(radOffsetY[i]);
				
				objShader.setModlMat(modlMat);
				
				sprouts.get(i % 4).drawBuffer();
				grassObj.drawBuffer();					
			}
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

	public ITextureRegion getFlowerTexture(int flowerIndex) {
		return flowerTextures[LinMath.clamp(flowerIndex, 0, flowerTextures.length - 1)];
	}
	
	public void stop() {
		running = false;
	}
	
	public static void main(String[] args) {
		new SproutsMain().run();
	}
}