package com.sprouts.composition.event;

import com.sprouts.composition.Composition;

public final class FocusEvent extends Event {

	public static final int FOCUS_GAINED_TYPE  = 300;
	public static final int FOCUS_LOST_TYPE    = 301;
	
	public static final int FIRST_TYPE = 300;
	public static final int LAST_TYPE  = 301;
	
	private final int type;
	
	private final Composition source;

	public FocusEvent(int type, Composition source) {
		if (source == null)
			throw new IllegalStateException("source is null!");
		
		if (type < FIRST_TYPE || type > LAST_TYPE)
			type = UNKNOWN_TYPE;
		
		this.type = type;
		
		this.source = source;
	}
	
	@Override
	public int getType() {
		return type;
	}
	
	@Override
	public Composition getSource() {
		return source;
	}
	
	public static FocusEvent createFocusGainedEvent(Composition source) {
		return new FocusEvent(FOCUS_GAINED_TYPE, source);
	}

	public static FocusEvent createFocusLostEvent(Composition source) {
		return new FocusEvent(FOCUS_LOST_TYPE, source);
	}
}
