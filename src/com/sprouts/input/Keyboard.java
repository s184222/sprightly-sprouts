package com.sprouts.input;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.sprouts.graphic.Display;

public class Keyboard {

	private final Display display;
	
	private final List<IKeyboardListener> listeners;
	
	public Keyboard(Display display) {
		this.display = display;
		
		listeners = new ArrayList<IKeyboardListener>();
	}
	
	private void glfwKeyCallback(long window, int key, int scancode, int action, int mods) {
		switch (action) {
		case GLFW.GLFW_PRESS:
			dispatchKeyPressedEvent(key, mods);
			break;
		case GLFW.GLFW_REPEAT:
			dispatchKeyRepeatedEvent(key, mods);
			break;
		case GLFW.GLFW_RELEASE:
			dispatchKeyReleasedEvent(key, mods);
			break;
		}
	}

	private void glfwCharCallback(long window, int codepoint) {
		dispatchKeyTypedEvent(codepoint);
	}
	
	public void addListener(IKeyboardListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IKeyboardListener listener) {
		listeners.remove(listener);
	}
	
	private void dispatchKeyPressedEvent(int key, int mods) {
		for (IKeyboardListener listener : listeners)
			listener.keyPressed(key, mods);
	}

	private void dispatchKeyRepeatedEvent(int key, int mods) {
		for (IKeyboardListener listener : listeners)
			listener.keyRepeated(key, mods);
	}

	private void dispatchKeyReleasedEvent(int key, int mods) {
		for (IKeyboardListener listener : listeners)
			listener.keyReleased(key, mods);
	}

	private void dispatchKeyTypedEvent(int codePoint) {
		for (IKeyboardListener listener : listeners)
			listener.keyTyped(codePoint);
	}
	
	public void init() {
		display.setKeyCallback(this::glfwKeyCallback);
		display.setCharCallback(this::glfwCharCallback);
	}
}
