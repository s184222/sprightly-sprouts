package com.sprouts;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;

import com.sprouts.graphic.Display;
import com.sprouts.graphic.DisplaySize;
import com.sprouts.graphic.font.Font;
import com.sprouts.graphic.font.FontData;
import com.sprouts.graphic.font.FontLoader;
import com.sprouts.graphic.obj.ObjData;
import com.sprouts.graphic.obj.ObjLoader;
import com.sprouts.graphic.obj.shader.BasicObjShader;
import com.sprouts.graphic.obj.shader.ObjShader;
import com.sprouts.graphic.tessellator2d.BatchedTessellator2D;
import com.sprouts.graphic.tessellator2d.shader.BasicTessellator2DShader;
import com.sprouts.graphic.tessellator2d.shader.Tessellator2DShader;
import com.sprouts.graphic.texture.Texture;
import com.sprouts.graphic.texture.TextureLoader;
import com.sprouts.input.Keyboard;
import com.sprouts.input.Mouse;
import com.sprouts.math.Mat4;
import com.sprouts.util.LibUtil;

public class SproutsMain3 {

	static {
		LibUtil.loadNatives();
	}
	
	private static final String WINDOW_TITLE = "Sprightly Sprouts";
	private static final int WINDOW_WIDTH  = 500;
	private static final int WINDOW_HEIGHT = 500;

	private final Display display;
	private final Mouse mouse;
	private final Keyboard keyboard;
	
	private ObjShader objShader;
	private ObjData sproutStage0;
	private Texture sproutStage0Texture;
	private ObjData sproutStage1;
	private Texture sproutStage1Texture;
	private ObjData sproutStage2;
	private Texture sproutStage2Texture;
	private ObjData sproutStage3;
	private Texture sproutStage3Texture;
	private ObjData groundObj;
	private Texture groundTexture;
	private List<ObjData> sprouts;
	private Tessellator2DShader tessellator2DShader;
	private BatchedTessellator2D batchedTessellator2D;
	private Texture spongeBobTexture;
	private Font arialFont;

	float[] radOffsetX = new float[400];
	float[] radOffsetY = new float[400];
	float[] xOffset = new float[400];
	
	public SproutsMain3() {
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
		arialFont = arialData.createFont(12);
		
		objShader = new BasicObjShader();
		objShader.enable();
		objShader.setTextureSampler(0);
		
		sprouts = new ArrayList<ObjData>();
		
		sproutStage0 = ObjLoader.loadObj("/models/sproutStage0.obj");
		sproutStage0.initBuffers(objShader);
		sproutStage0Texture = TextureLoader.loadTexture("/textures/sproutStage0Texture.png");
		sproutStage0.setTexture(sproutStage0Texture);
		sprouts.add(sproutStage0);
		
		sproutStage1 = ObjLoader.loadObj("/models/sproutStage1.obj");
		sproutStage1.initBuffers(objShader);
		sproutStage1Texture = TextureLoader.loadTexture("/textures/sproutStage1Texture.png");
		sproutStage1.setTexture(sproutStage1Texture);
		sprouts.add(sproutStage1);
		
		sproutStage2 = ObjLoader.loadObj("/models/sproutStage2.obj");
		sproutStage2.initBuffers(objShader);
		sproutStage2Texture = TextureLoader.loadTexture("/textures/sproutStage2Texture.png");
		sproutStage2.setTexture(sproutStage2Texture);
		sprouts.add(sproutStage2);
		
		sproutStage3 = ObjLoader.loadObj("/models/sproutStage3.obj");
		sproutStage3.initBuffers(objShader);
		sproutStage3Texture = TextureLoader.loadTexture("/textures/sproutStage3Texture.png");
		sproutStage3.setTexture(sproutStage3Texture);
		sprouts.add(sproutStage3);
		
		Random random = new Random();
		
		for(int i = 0; i < 400; i++) {
			radOffsetX[i] = random.nextFloat() * ((float) (Math.PI) * 2);
			radOffsetY[i] = random.nextFloat() * ((float) (Math.PI) * 2);
			xOffset[i] = random.nextFloat() * 50f - 25f;
		}
		
		groundObj = ObjLoader.loadObj("/models/ground.obj");
		groundObj.initBuffers(objShader);
		groundTexture = TextureLoader.loadTexture("/textures/groundTexture.png");
		groundObj.setTexture(groundTexture);
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
	
		objShader.enable();
		objShader.setProjMat(new Mat4().toPerspective((float)Math.toRadians(70.0), (float)width / height, 0.01f, 100.0f));
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
	
	private float rads;
	
	private void render() {
		objShader.enable();

		Mat4 viewMat = new Mat4();
		viewMat.translate(0f, 0f, -38.0f);
		viewMat.rotateX(rads += 0.001f);


		objShader.setViewMat(viewMat);
		
		groundObj.drawBuffer();

		Mat4 modlMat = new Mat4();
		
		for (int i = 0; i < 400; i++) {
			modlMat.rotateX(radOffsetX[i]);
			modlMat.translate(xOffset[i], 24.0f, 0);
			modlMat.rotateY(radOffsetY[i]);
			
			objShader.setModlMat(modlMat);
			
			sprouts.get(i%4).drawBuffer();
			
			modlMat.rotateY(-radOffsetY[i]);
			modlMat.translate(-xOffset[i], -24.0f, 0);
			modlMat.rotateX(-radOffsetX[i]);
			
			objShader.setModlMat(modlMat);
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
		new SproutsMain3().run();
	}
}