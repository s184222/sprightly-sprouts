package com.sprouts.composition.event;

public class MouseButtonStroke implements IButtonStroke {

	private final int button;
	private final int modifiers;

	public MouseButtonStroke(int button) {
		this(button, MouseEvent.NO_MODIFIERS);
	}
	
	public MouseButtonStroke(int button, int modifiers) {
		this.button = button;
		this.modifiers = modifiers;
	}
	
	@Override
	public boolean isMatching(Event event) {
		if (!(event instanceof MouseEvent))
			return false;
		
		MouseEvent me = (MouseEvent)event;
		if (me.getType() != MouseEvent.MOUSE_DRAGGED_TYPE &&
		    me.getType() != MouseEvent.MOUSE_PRESSED_TYPE &&
		    me.getType() != MouseEvent.MOUSE_RELEASED_TYPE) {
			
			// Only support pressed and released.
			return false;
		}
		
		return (me.getButton() == button && me.isModifierHeld(modifiers));
	}
}
