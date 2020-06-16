package com.sprouts.input;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.sprouts.graphic.Display;

public class Keyboard {

	private static final int BACKSPACE_CODE_POINT       = 0x08;
	private static final int TAB_CODE_POINT             = 0x09;
	private static final int NEW_LINE_CODE_POINT        = 0x0A;
	private static final int CONTROL_Z_CODE_POINT       = 0x1A;
	private static final int ESCAPE_CODE_POINT          = 0x1B;
	private static final int DELETE_CODE_POINT          = 0x7F;
	
	private final Display display;
	
	private final List<IKeyboardListener> listeners;
	
	public Keyboard(Display display) {
		this.display = display;
		
		listeners = new ArrayList<IKeyboardListener>();
	}
	
	private void glfwKeyCallback(long window, int key, int scancode, int action, int mods) {
		switch (action) {
		case GLFW.GLFW_PRESS:
			checkAndDispatchControlCharacter(key, mods);
			dispatchKeyPressedEvent(key, mods);
			break;
		case GLFW.GLFW_REPEAT:
			checkAndDispatchControlCharacter(key, mods);
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
	
	private void checkAndDispatchControlCharacter(int key, int mods) {
		switch (key) {
		case GLFW.GLFW_KEY_BACKSPACE:
			dispatchKeyTypedEvent(BACKSPACE_CODE_POINT);
			break;
		case GLFW.GLFW_KEY_TAB:
			dispatchKeyTypedEvent(TAB_CODE_POINT);
			break;
		case GLFW.GLFW_KEY_ENTER:
			dispatchKeyTypedEvent(NEW_LINE_CODE_POINT);
			break;
		case GLFW.GLFW_KEY_Z:
			if ((mods & GLFW.GLFW_MOD_CONTROL) != 0)
				dispatchKeyTypedEvent(CONTROL_Z_CODE_POINT);
			break;
		case GLFW.GLFW_KEY_ESCAPE:
			dispatchKeyTypedEvent(ESCAPE_CODE_POINT);
			break;
		case GLFW.GLFW_KEY_DELETE:
			dispatchKeyTypedEvent(DELETE_CODE_POINT);
			break;
		}
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
