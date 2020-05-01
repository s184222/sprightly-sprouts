package com.sprouts.graphic.texture;

public class InvalidFormatException extends RuntimeException {
	private static final long serialVersionUID = -3555550294570310142L;

	public InvalidFormatException() {
	}
	
	public InvalidFormatException(String msg) {
		super(msg);
	}
}
