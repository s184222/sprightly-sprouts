package com.sprouts.input;

public interface KeyboardListener {

	public void keyPressed(int key, int mods);

	public void keyRepeated(int key, int mods);

	public void keyReleased(int key, int mods);
	
	public void keyTyped(char keyChar);
	
}
