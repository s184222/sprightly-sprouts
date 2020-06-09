package com.sprouts.input;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import com.sprouts.graphic.Display;

public class Mouse {

	private final Display display;
	
	private final List<MouseListener> listeners;

	private final Set<Integer> pressedButtons;
	
	private float mouseX;
	private float mouseY;
	
	private float dragX;
	private float dragY;
	
	public Mouse(Display display) {
		this.display = display;
		
		listeners = new ArrayList<MouseListener>();
		
		pressedButtons = new LinkedHashSet<Integer>();
	}
	
	public void glfwCursorPosCallback(long window, double xpos, double ypos) {
		dragX = (float)xpos - mouseX;
		dragY = (float)ypos - mouseY;
		
		mouseX = (float)xpos;
		mouseY = (float)ypos;
	
		dispatchMouseMovedEvent(mouseX, mouseY);
		
		for (Integer pressedButton : pressedButtons)
			dispatchMouseDraggedEvent(pressedButton, mouseX, mouseY, dragX, dragY);
	}
	
	public void glfwMouseButtonCallback(long window, int button, int action, int mods) {
		switch (action) {
		case GLFW.GLFW_PRESS:
			dispatchMouseClickedEvent(button, mouseX, mouseY);
			pressedButtons.add(button);
			break;
		case GLFW.GLFW_RELEASE:
			dispatchMouseReleasedEvent(button, mouseX, mouseY);
			pressedButtons.remove(button);
			break;
		}
	}

	public void glfwScrollCallback(long window, double xoffset, double yoffset) {
		dispatchMouseScrollEvent(mouseX, mouseY, (float)xoffset, (float)yoffset);
	}
	
	public void addListener(MouseListener listener) {
		listeners.add(listener);
	}

	public void removeListener(MouseListener listener) {
		listeners.remove(listener);
	}
	
	private void dispatchMouseMovedEvent(float mouseX, float mouseY) {
		for (MouseListener listener : listeners)
			listener.mouseMoved(mouseX, mouseY);
	}	
	
	private void dispatchMouseDraggedEvent(int button, float mouseX, float mouseY, float dragX, float dragY) {
		for (MouseListener listener : listeners)
			listener.mouseDragged(button, mouseX, mouseY, dragX, dragY);
	}

	private void dispatchMouseClickedEvent(int button, float mouseX, float mouseY) {
		for (MouseListener listener : listeners)
			listener.mouseClicked(button, mouseX, mouseY);
	}

	private void dispatchMouseReleasedEvent(int button, float mouseX, float mouseY) {
		for (MouseListener listener : listeners)
			listener.mouseReleased(button, mouseX, mouseY);
	}

	private void dispatchMouseScrollEvent(float mouseX, float mouseY, float scrollX, float scrollY) {
		for (MouseListener listener : listeners)
			listener.mouseScroll(mouseX, mouseY, scrollX, scrollY);
	}

	public boolean isButtonPressed(int button) {
		return pressedButtons.contains(button);
	}
	
	public float getMouseX() {
		return mouseX;
	}

	public float getMouseY() {
		return mouseY;
	}
	
	public void init() {
		display.setCursorPosCallback(this::glfwCursorPosCallback);
		display.setMouseButtonCallback(this::glfwMouseButtonCallback);
		display.setScrollCallback(this::glfwScrollCallback);
	}
}
