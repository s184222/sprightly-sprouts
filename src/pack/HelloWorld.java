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
	private VertexBuffer buffer;
	
	public HelloWorld() {
		this.display = new Display();
	}
	
	public void run() {
		System.out.println("Using LWJGL version " + Version.getVersion());

		init();
		loop();
		
		display.dispose();
	}

	private void init() {
		display.initDisplay(WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT);
		display.addDisplayListener(this::onViewportChanged);
	
		shader = new TestShader();
		buffer = new VertexBuffer(new float[] {
			  0.0f,  0.75f, 0.0f,
			 0.75f, -0.75f, 0.0f,
			-0.75f, -0.75f, 0.0f
		}, 3);
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
			buffer.bind();
			GL30.glEnableVertexAttribArray(TestShader.POSITION_ATTRIB_INDEX);
			GL30.glVertexAttribPointer(TestShader.POSITION_ATTRIB_INDEX, buffer.getComponentCount(), GL30.GL_FLOAT, false, 0, 0);
			GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, buffer.getSize() / buffer.getComponentCount());
			GL30.glDisableVertexAttribArray(TestShader.POSITION_ATTRIB_INDEX);
			buffer.unbind();
			shader.disable();
			
			display.update();
		}
	}

	public static void main(String[] args) {
		new HelloWorld().run();
	}
}