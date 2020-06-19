package com.sprouts.composition.text.editable;

public interface ITextCaret {

	public void install(EditableTextComposition textComposition);

	public void uninstall(EditableTextComposition textComposition);

	public void addTextCaretListener(ITextCaretListener listener);

	public void removeTextCaretListener(ITextCaretListener listener);
	
	public int getCaretLocation();

	public void setCaretLocation(int location);
	
	public int getCaretDot();

	public void setCaretDot(int dot);
	
	public int getCaretMark();

	public void setCaretMark(int mark);
	
	public boolean hasCaretSelection();
	
	public int getBlinkRate();

	public void setBlinkRate(int blinkRate);

	public int getCaretWidth();

	public void setCaretWidth(int width);

	public int getCaretInsets();

	public void setCaretInsets(int insets);
	
}
