package com.sprouts.composition.event;

public interface IMouseEventListener extends IEventListener {

	public void mouseEntered(MouseEvent event);

	public void mouseExited(MouseEvent event);
	
	public void mouseMoved(MouseEvent event);

	public void mouseDragged(MouseEvent event);

	public void mousePressed(MouseEvent event);

	public void mouseReleased(MouseEvent event);

	public void mouseScrolled(MouseEvent event);
	
}
