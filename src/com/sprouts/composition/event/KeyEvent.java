package com.sprouts.composition.event;

import org.lwjgl.glfw.GLFW;

import com.sprouts.composition.Composition;

public final class KeyEvent extends Event {

	public static final int KEY_PRESSED_TYPE  = 200;
	public static final int KEY_REPEATED_TYPE = 201;
	public static final int KEY_RELEASED_TYPE = 202;
	public static final int KEY_TYPED_TYPE    = 203;
	
	public static final int FIRST_TYPE = 200;
	public static final int LAST_TYPE  = 203;
	
	public static final int UNKNOWN_KEY = GLFW.GLFW_KEY_UNKNOWN;
	public static final int UNKNOWN_CODE_POINT = '\0'; /* NULL char */
	
	private final int type;
	
	private final Composition source;

	/* The KeyCode for PRESSED, REPEATED, and RELEASED. The CodePoint for TYPED. */
	private final int keyCode;
	/* The Modifiers for PRESSED, REPREATED, and RELEASED. NO_MODIFIERS for TYPED. */
	private final int modifiers;
	
	public KeyEvent(int type, Composition source, int keyCode, int modifiers) {
		if (source == null)
			throw new IllegalStateException("source is null!");
		
		if (type < FIRST_TYPE || type > LAST_TYPE)
			type = UNKNOWN_TYPE;
		
		this.type = type;
		
		this.source = source;

		this.keyCode = keyCode;
		this.modifiers = modifiers & ALL_MODIFIERS;
	}
	
	@Override
	public int getType() {
		return type;
	}
	
	@Override
	public Composition getSource() {
		return source;
	}
	
	public int getKeyCode() {
		return (type != KEY_TYPED_TYPE) ? keyCode : UNKNOWN_KEY;
	}

	public int getCodePoint() {
		return (type == KEY_TYPED_TYPE) ? keyCode : UNKNOWN_CODE_POINT;
	}
	
	public int getModifiers() {
		return modifiers;
	}

	public boolean isModifierHeld(int modifier) {
		return (modifiers & modifier) == modifier;
	}
	
	public static KeyEvent createKeyPressedEvent(Composition source, int keyCode, int modifiers) {
		return new KeyEvent(KEY_PRESSED_TYPE, source, keyCode, modifiers);
	}

	public static KeyEvent createKeyRepeatedEvent(Composition source, int keyCode, int modifiers) {
		return new KeyEvent(KEY_REPEATED_TYPE, source, keyCode, modifiers);
	}

	public static KeyEvent createKeyReleasedEvent(Composition source, int keyCode, int modifiers) {
		return new KeyEvent(KEY_RELEASED_TYPE, source, keyCode, modifiers);
	}

	public static KeyEvent createKeyTypedEvent(Composition source, int codePoint) {
		return new KeyEvent(KEY_TYPED_TYPE, source, codePoint, NO_MODIFIERS);
	}
}
