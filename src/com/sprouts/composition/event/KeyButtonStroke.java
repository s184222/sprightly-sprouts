package com.sprouts.composition.event;

public class KeyButtonStroke implements IButtonStroke {

	private final int keyCode;
	private final int modifiers;

	public KeyButtonStroke(int keyCode) {
		this(keyCode, KeyEvent.NO_MODIFIERS);
	}

	public KeyButtonStroke(int keyCode, int modifiers) {
		this.keyCode = keyCode;
		this.modifiers = modifiers;
	}
	
	@Override
	public boolean isMatching(Event event) {
		if (!(event instanceof KeyEvent))
			return false;
		
		KeyEvent ke = (KeyEvent)event;
		if (ke.getType() != KeyEvent.KEY_PRESSED_TYPE &&
		    ke.getType() != KeyEvent.KEY_REPEATED_TYPE &&
		    ke.getType() != KeyEvent.KEY_RELEASED_TYPE) {
			
			// Only support pressed, repeat and released events.
			return false;
		}
		
		return (ke.getKeyCode() == keyCode && ke.isModifierHeld(modifiers));
	}
}
