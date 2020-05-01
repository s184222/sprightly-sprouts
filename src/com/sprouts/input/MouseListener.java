package com.sprouts.input;

public interface MouseListener {

	public void mouseMoved(double mouseX, double mouseY);
	
	public void mouseDragged(int button, double mouseX, double mouseY, double dragX, double dragY);

	public void mouseClicked(int button, double mouseX, double mouseY);
	
	public void mouseReleased(int button, double mouseX, double mouseY);
	
	public void mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY);
	
}
