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
	
	private double mouseX;
	private double mouseY;
	
	private double dragX;
	private double dragY;
	
	public Mouse(Display display) {
		this.display = display;
		
		listeners = new ArrayList<MouseListener>();
		
		pressedButtons = new LinkedHashSet<Integer>();
	}
	
	public void glfwCursorPosCallback(long window, double xpos, double ypos) {
		dragX = xpos - mouseX;
		dragY = ypos - mouseY;
		
		mouseX = xpos;
		mouseY = ypos;
	
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
		dispatchMouseScrollEvent(mouseX, mouseY, xoffset, yoffset);
	}
	
	public void addListener(MouseListener listener) {
		listeners.add(listener);
	}

	public void removeListener(MouseListener listener) {
		listeners.remove(listener);
	}
	
	private void dispatchMouseMovedEvent(double mouseX, double mouseY) {
		for (MouseListener listener : listeners)
			listener.mouseMoved(mouseX, mouseY);
	}	
	
	private void dispatchMouseDraggedEvent(int button, double mouseX, double mouseY, double dragX, double dragY) {
		for (MouseListener listener : listeners)
			listener.mouseDragged(button, mouseX, mouseY, dragX, dragY);
	}

	private void dispatchMouseClickedEvent(int button, double mouseX, double mouseY) {
		for (MouseListener listener : listeners)
			listener.mouseClicked(button, mouseX, mouseY);
	}

	private void dispatchMouseReleasedEvent(int button, double mouseX, double mouseY) {
		for (MouseListener listener : listeners)
			listener.mouseReleased(button, mouseX, mouseY);
	}

	private void dispatchMouseScrollEvent(double mouseX, double mouseY, double scrollX, double scrollY) {
		for (MouseListener listener : listeners)
			listener.mouseScroll(mouseX, mouseY, scrollX, scrollY);
	}
	
	public void init() {
		display.setCursorPosCallback(this::glfwCursorPosCallback);
		display.setMouseButtonCallback(this::glfwMouseButtonCallback);
		display.setScrollCallback(this::glfwScrollCallback);
	}
}
