package com.sprouts.composition.event;

public interface IKeyEventListener extends IEventListener {

	public void keyPressed(KeyEvent event);

	public void keyRepeated(KeyEvent event);

	public void keyReleased(KeyEvent event);

	public void keyTyped(KeyEvent event);
	
}
