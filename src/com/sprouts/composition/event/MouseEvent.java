package com.sprouts.composition.event;

import org.lwjgl.glfw.GLFW;

import com.sprouts.composition.Composition;

public final class MouseEvent extends Event {

	public static final int MOUSE_ENTERED_TYPE  = 100;
	public static final int MOUSE_EXITED_TYPE   = 101;
	
	public static final int MOUSE_MOVED_TYPE    = 102;
	public static final int MOUSE_DRAGGED_TYPE  = 103;
	
	public static final int MOUSE_PRESSED_TYPE  = 104;
	public static final int MOUSE_RELEASED_TYPE = 105;
	
	public static final int MOUSE_SCROLLED_TYPE = 106;
	
	private static final int FIRST_EVENT_TYPE = MOUSE_ENTERED_TYPE;
	private static final int LAST_EVENT_TYPE  = MOUSE_SCROLLED_TYPE;
	
	public static final int UNKNOWN_BUTTON = -1;
	
	public static final int BUTTON_1 = GLFW.GLFW_MOUSE_BUTTON_1;
	public static final int BUTTON_2 = GLFW.GLFW_MOUSE_BUTTON_2;
	public static final int BUTTON_3 = GLFW.GLFW_MOUSE_BUTTON_3;
	public static final int BUTTON_4 = GLFW.GLFW_MOUSE_BUTTON_4;
	public static final int BUTTON_5 = GLFW.GLFW_MOUSE_BUTTON_5;
	public static final int BUTTON_6 = GLFW.GLFW_MOUSE_BUTTON_6;
	public static final int BUTTON_7 = GLFW.GLFW_MOUSE_BUTTON_7;
	public static final int BUTTON_8 = GLFW.GLFW_MOUSE_BUTTON_8;

	public static final int BUTTON_LEFT   = GLFW.GLFW_MOUSE_BUTTON_LEFT;
	public static final int BUTTON_MIDDLE = GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
	public static final int BUTTON_RIGHT  = GLFW.GLFW_MOUSE_BUTTON_RIGHT;
	
	private static final int FIRST_BUTTON = GLFW.GLFW_MOUSE_BUTTON_1;
	private static final int LAST_BUTTON  = GLFW.GLFW_MOUSE_BUTTON_8;
	
	/* The type of mouse event */
	private final int type;
	
	/* The origin of the event */
	private final Composition source;
	
	/* The current mouse coordinates */
	private final int x;
	private final int y;
	
	/* The button that is held, pressed, released, or unknown button */
	private final int button;
	
	/* The modifiers that are held when pressing or releasing */
	private final int modifiers;
	
	/* DragX and DragY when dragging, ScrollX and ScrollY when scrolling */
	private final float extraX;
	private final float extraY;
	
	public MouseEvent(int type, Composition source, int x, int y, 
	                  int button, int modifiers, float extraX, float extraY) {
		
		if (source == null)
			throw new IllegalArgumentException("source is null!");
		
		if (type < FIRST_EVENT_TYPE || type > LAST_EVENT_TYPE)
			type = UNKNOWN_TYPE;
		if (button < FIRST_BUTTON || button > LAST_BUTTON)
			button = UNKNOWN_BUTTON;
		
		this.type = type;
		
		this.source = source;
		
		this.x = x;
		this.y = y;
		
		this.button = button;
		this.modifiers = modifiers & ALL_MODIFIERS;
		
		this.extraX = extraX;
		this.extraY = extraY;
	}
	
	@Override
	public int getType() {
		return type;
	}
	
	@Override
	public Composition getSource() {
		return source;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getButton() {
		return button;
	}
	
	public int getModifiers() {
		return modifiers;
	}

	public boolean isModifierHeld(int modifier) {
		return (modifiers & modifier) == modifier;
	}
	
	public float getDragX() {
		return (type == MOUSE_DRAGGED_TYPE) ? extraX : 0.0f;
	}

	public float getDragY() {
		return (type == MOUSE_DRAGGED_TYPE) ? extraY : 0.0f;
	}

	public float getScrollX() {
		return (type == MOUSE_SCROLLED_TYPE) ? extraX : 0.0f;
	}

	public float getScrollY() {
		return (type == MOUSE_SCROLLED_TYPE) ? extraY : 0.0f;
	}
	
	public static MouseEvent createMouseEnteredEvent(Composition source, int x, int y) {
		return new MouseEvent(MOUSE_ENTERED_TYPE, source, x, y, UNKNOWN_BUTTON, NO_MODIFIERS, 0.0f, 0.0f);
	}

	public static MouseEvent createMouseExitedEvent(Composition source, int x, int y) {
		return new MouseEvent(MOUSE_EXITED_TYPE, source, x, y, UNKNOWN_BUTTON, NO_MODIFIERS, 0.0f, 0.0f);
	}

	public static MouseEvent createMouseMovedEvent(Composition source, int x, int y) {
		return new MouseEvent(MOUSE_MOVED_TYPE, source, x, y, UNKNOWN_BUTTON, NO_MODIFIERS, 0.0f, 0.0f);
	}

	public static MouseEvent createMouseDraggedEvent(Composition source, int x, int y, int button, float dragX, float dragY) {
		return new MouseEvent(MOUSE_DRAGGED_TYPE, source, x, y, button, NO_MODIFIERS, dragX, dragY);
	}

	public static MouseEvent createMousePressedEvent(Composition source, int x, int y, int button, int modifiers) {
		return new MouseEvent(MOUSE_PRESSED_TYPE, source, x, y, button, modifiers, 0.0f, 0.0f);
	}

	public static MouseEvent createMouseReleasedEvent(Composition source, int x, int y, int button, int modifiers) {
		return new MouseEvent(MOUSE_RELEASED_TYPE, source, x, y, button, modifiers, 0.0f, 0.0f);
	}

	public static MouseEvent createMouseScrolledEvent(Composition source, int x, int y, float scrollX, float scrollY) {
		return new MouseEvent(MOUSE_SCROLLED_TYPE, source, x, y, UNKNOWN_BUTTON, NO_MODIFIERS, scrollX, scrollY);
	}
}
