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
import com.sprouts.graphic.buffer.VertexBuffer;
import com.sprouts.graphic.shader.TestShader;
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
		
		VertexBuffer posBuffer = new VertexBuffer(new float[] {
			 0.00f,  0.75f, 0.0f,
			 0.75f, -0.75f, 0.0f,
			-0.75f, -0.75f, 0.0f
		}, 3);
		vertexArray.storeAttributeBuffer(TestShader.POSITION_ATTRIB_INDEX, posBuffer);
	}

	private void onViewportChanged(DisplaySize size) {
		onViewportChanged(size.width, size.height);
	}

	private void onViewportChanged(int width, int height) {
		GL11.glViewport(0, 0, width, height);
	}
	
	private void loop() {
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		onViewportChanged(display.getDisplaySize());

		while (!display.isCloseRequested()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			shader.enable();
			vertexArray.bind();
			
			// Draw 1 triangle.
			GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 3);
			
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