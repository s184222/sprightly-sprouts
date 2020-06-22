package com.sprouts;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;

import com.sprouts.graphic.Display;
import com.sprouts.graphic.DisplaySize;
import com.sprouts.graphic.obj.ObjData;
import com.sprouts.graphic.obj.ObjLoader;
import com.sprouts.graphic.obj.shader.BasicObjShader;
import com.sprouts.graphic.obj.shader.ObjShader;
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

	private static final float PI2 = (float)(2.0 * Math.PI);
	private static final float OBJ_FOV = 70.0f * PI2 / 360.0f;
	private static final int NUM_SPROUTS = 400;
	
	private final Display display;
	private final Mouse mouse;
	private final Keyboard keyboard;
	
	private ObjShader objShader;
	private ObjData groundObj;
	private float groundRot;
	private List<ObjData> sprouts;
	
	private final float[] radOffsetX = new float[NUM_SPROUTS];
	private final float[] radOffsetY = new float[NUM_SPROUTS];
	private final float[] xOffset = new float[NUM_SPROUTS];
	
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
		
		objShader.dispose();
		
		for (ObjData sprout : sprouts) {
			sprout.getTexture().dispose();
			sprout.dispose();
		}
		
		groundObj.getTexture().dispose();
		groundObj.dispose();
	}

	private void loadResources() throws Exception {
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
		
		objShader.enable();
		objShader.setProjMat(new Mat4().toPerspective(OBJ_FOV, (float)width / height, 0.01f, 100.0f));
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
		objShader.enable();

		groundRot += 0.001f;
		if (groundRot >= PI2)
			groundRot %= PI2;

		Mat4 viewMat = new Mat4();
		Mat4 modlMat = new Mat4();

		viewMat.translate(0f, 0f, -38.0f);
		viewMat.rotateX(groundRot);

		objShader.setViewMat(viewMat);
		objShader.setModlMat(modlMat);
		
		groundObj.drawBuffer();
		
		for (int i = 0; i < NUM_SPROUTS; i++) {
			float dr = (groundRot + radOffsetX[i] + 0.75f * PI2 + 0.5f * OBJ_FOV) % PI2;
			if (dr < OBJ_FOV) {
				modlMat.toIdentity();
				modlMat.rotateX(radOffsetX[i]);
				modlMat.translate(xOffset[i], 24.0f, 0);
				modlMat.rotateY(radOffsetY[i]);
				
				objShader.setModlMat(modlMat);
				
				sprouts.get(i % 4).drawBuffer();
			}
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