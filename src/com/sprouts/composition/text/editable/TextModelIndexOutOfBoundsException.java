package com.sprouts.composition.text.editable;

@SuppressWarnings("serial")
public class TextModelIndexOutOfBoundsException extends IndexOutOfBoundsException {

	public TextModelIndexOutOfBoundsException() {
		super();
	}

	public TextModelIndexOutOfBoundsException(String msg) {
		super(msg);
	}
	
	public TextModelIndexOutOfBoundsException(int index) {
		super("TextModel index out of bounds: " + index);
	}
}
