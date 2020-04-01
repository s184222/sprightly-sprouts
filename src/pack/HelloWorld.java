package pack;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.sprouts.graphic.Display;
import com.sprouts.graphic.DisplaySize;
import com.sprouts.graphic.buffer.VertexArray;
import com.sprouts.graphic.shader.TestShader;
import com.sprouts.graphic.tessellator.BasicTessellator;
import com.sprouts.math.Mat4;
import com.sprouts.os.LibUtil;

public class HelloWorld {

	static {
		LibUtil.loadNatives();
	}
	
	private static final String WINDOW_TITLE = "Sprightly Sprouts";
	private static final int WINDOW_WIDTH  = 500;
	private static final int WINDOW_HEIGHT = 500;
	
	private final Display display;

	private TestShader shader;
	private VertexArray vertexArray;
	
	private Mat4 projMat;
	private Mat4 viewMat;
	private Mat4 modlMat;
	
	private float rot;
	
	public HelloWorld() {
		this.display = new Display();
	}
	
	public void run() {
		System.out.println("Using LWJGL version " + Version.getVersion());

		init();
		loop();
		
		shader.dispose();
		vertexArray.dispose();
		display.dispose();
	}

	private void init() {
		display.initDisplay(WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT);
		display.addDisplayListener(this::onViewportChanged);
	
		shader = new TestShader();
		vertexArray = new VertexArray();
		
		try (BasicTessellator t = new BasicTessellator()) {
			// FRONT
			t.position( 1.0f, -0.0f,  1.0f).color( 1.0f, -0.0f,  1.0f).next();
			t.position(-0.0f,  1.0f,  1.0f).color(-0.0f,  1.0f,  1.0f).next();
			t.position(-0.0f, -0.0f,  1.0f).color(-0.0f, -0.0f,  1.0f).next();

			t.position( 1.0f, -0.0f,  1.0f).color( 1.0f, -0.0f,  1.0f).next();
			t.position( 1.0f,  1.0f,  1.0f).color( 1.0f,  1.0f,  1.0f).next();
			t.position(-0.0f,  1.0f,  1.0f).color(-0.0f,  1.0f,  1.0f).next();

			// BACK
			t.position(-0.0f, -0.0f, -0.0f).color(-0.0f, -0.0f, -0.0f).next();
			t.position( 1.0f,  1.0f, -0.0f).color( 1.0f,  1.0f, -0.0f).next();
			t.position( 1.0f, -0.0f, -0.0f).color( 1.0f, -0.0f, -0.0f).next();

			t.position(-0.0f, -0.0f, -0.0f).color(-0.0f, -0.0f, -0.0f).next();
			t.position(-0.0f,  1.0f, -0.0f).color(-0.0f,  1.0f, -0.0f).next();
			t.position( 1.0f,  1.0f, -0.0f).color( 1.0f,  1.0f, -0.0f).next();

			// BOTTOM
			t.position(-0.0f, -0.0f,  1.0f).color(-0.0f, -0.0f,  1.0f).next();
			t.position( 1.0f, -0.0f, -0.0f).color( 1.0f, -0.0f, -0.0f).next();
			t.position( 1.0f, -0.0f,  1.0f).color( 1.0f, -0.0f,  1.0f).next();

			t.position(-0.0f, -0.0f,  1.0f).color(-0.0f, -0.0f,  1.0f).next();
			t.position(-0.0f, -0.0f, -0.0f).color(-0.0f, -0.0f, -0.0f).next();
			t.position( 1.0f, -0.0f, -0.0f).color( 1.0f, -0.0f, -0.0f).next();

			// TOP
			t.position(-0.0f,  1.0f, -0.0f).color(-0.0f,  1.0f, -0.0f).next();
			t.position( 1.0f,  1.0f,  1.0f).color( 1.0f,  1.0f,  1.0f).next();
			t.position( 1.0f,  1.0f, -0.0f).color( 1.0f,  1.0f, -0.0f).next();

			t.position(-0.0f,  1.0f, -0.0f).color(-0.0f,  1.0f, -0.0f).next();
			t.position(-0.0f,  1.0f,  1.0f).color(-0.0f,  1.0f,  1.0f).next();
			t.position( 1.0f,  1.0f,  1.0f).color( 1.0f,  1.0f,  1.0f).next();

			// LEFT
			t.position(-0.0f, -0.0f,  1.0f).color(-0.0f, -0.0f,  1.0f).next();
			t.position(-0.0f,  1.0f, -0.0f).color(-0.0f,  1.0f, -0.0f).next();
			t.position(-0.0f, -0.0f, -0.0f).color(-0.0f, -0.0f, -0.0f).next();

			t.position(-0.0f, -0.0f,  1.0f).color(-0.0f, -0.0f,  1.0f).next();
			t.position(-0.0f,  1.0f,  1.0f).color(-0.0f,  1.0f,  1.0f).next();
			t.position(-0.0f,  1.0f, -0.0f).color(-0.0f,  1.0f, -0.0f).next();

			// RIGHT
			t.position( 1.0f, -0.0f, -0.0f).color( 1.0f, -0.0f, -0.0f).next();
			t.position( 1.0f,  1.0f,  1.0f).color( 1.0f,  1.0f,  1.0f).next();
			t.position( 1.0f, -0.0f,  1.0f).color( 1.0f, -0.0f,  1.0f).next();

			t.position( 1.0f, -0.0f, -0.0f).color( 1.0f, -0.0f, -0.0f).next();
			t.position( 1.0f,  1.0f, -0.0f).color( 1.0f,  1.0f, -0.0f).next();
			t.position( 1.0f,  1.0f,  1.0f).color( 1.0f,  1.0f,  1.0f).next();
			
			vertexArray.storeAttributeBuffer(TestShader.POSITION_ATTRIB_INDEX, t.writePositionBuffer());
			vertexArray.storeAttributeBuffer(TestShader.COLOR_ATTRIB_INDEX, t.writeColorBuffer());
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

			shader.enable();
			
			viewMat.toIdentity().translate(0.0f, 0.0f, -2.0f);
			shader.setViewMat(viewMat);
			
			rot += 0.1f;
			
			modlMat.toIdentity().rotateX(rot).rotateY(rot).translate(-0.5f, -0.5f, -0.5f);
			shader.setModlMat(modlMat);

			vertexArray.bind();
			GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 3 * 2 * 6);
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

	public static void main(String[] args) {
		new HelloWorld().run();
	}
}