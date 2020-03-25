package com.sprouts.graphic;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class Display {

	private long windowHandle;
	
	private List<DisplayListener> listeners;
	
	public Display() {
		windowHandle = -1L;
		
		listeners = new ArrayList<DisplayListener>();
	}
	
	public void addDisplayListener(DisplayListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeDisplayListener(DisplayListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	public void initDisplay(String title, int width, int height) {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

		// Create the window
		windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
		if (windowHandle == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// TODO: move this out of display class
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true);
		});
		
		glfwSetWindowSizeCallback(windowHandle, (window, newWidth, newHeight) -> {
			dispatchSizeChangedEvent(newWidth, newHeight);
		});

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		DisplaySize size = getDisplaySize();
		
		// Center the window
		setDisplayPosition((vidmode.width()  - size.width ) / 2, 
		                  (vidmode.height() - size.height) / 2);
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(windowHandle);
		// Tell LWJGL to look for the OpenGL context.
		GL.createCapabilities();
		
		// Enable v-sync
		glfwSwapInterval(1);
		
		glfwShowWindow(windowHandle);
	}
	
	public void update() {
		glfwSwapBuffers(windowHandle);
		glfwPollEvents();
	}

	private void dispatchSizeChangedEvent(int width, int height) {
		for (DisplayListener listener : listeners)
			listener.sizeChanged(width, height);
	}
	
	public DisplaySize getDisplaySize() {
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*
	
			glfwGetWindowSize(windowHandle, pWidth, pHeight);
		
			return new DisplaySize(pWidth.get(0), pHeight.get(0));
		}
	}
	
	public void setDisplayPosition(int wx, int wy) {
		glfwSetWindowPos(windowHandle, wx, wy);
	}
	
	public boolean isCloseRequested() {
		return glfwWindowShouldClose(windowHandle);
	}
	
	public void dispose() {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(windowHandle);
		glfwDestroyWindow(windowHandle);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
}
