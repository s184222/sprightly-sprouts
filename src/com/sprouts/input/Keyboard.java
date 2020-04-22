package com.sprouts.input;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.sprouts.graphic.Display;

public class Keyboard {

	private final Display display;
	
	private final List<KeyboardListener> listeners;
	
	public Keyboard(Display display) {
		this.display = display;
		
		listeners = new ArrayList<KeyboardListener>();
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
		if (Character.isBmpCodePoint(codepoint))
			dispatchKeyTypedEvent((char)codepoint);
	}
	
	public void addListener(KeyboardListener listener) {
		listeners.add(listener);
	}

	public void removeListener(KeyboardListener listener) {
		listeners.remove(listener);
	}
	
	private void dispatchKeyPressedEvent(int key, int mods) {
		for (KeyboardListener listener : listeners)
			listener.keyPressed(key, mods);
	}

	private void dispatchKeyRepeatedEvent(int key, int mods) {
		for (KeyboardListener listener : listeners)
			listener.keyRepeated(key, mods);
	}

	private void dispatchKeyReleasedEvent(int key, int mods) {
		for (KeyboardListener listener : listeners)
			listener.keyReleased(key, mods);
	}

	private void dispatchKeyTypedEvent(char keyChar) {
		for (KeyboardListener listener : listeners)
			listener.keyTyped(keyChar);
	}
	
	public void init() {
		display.setKeyCallback(this::glfwKeyCallback);
		display.setCharCallback(this::glfwCharCallback);
	}
}
