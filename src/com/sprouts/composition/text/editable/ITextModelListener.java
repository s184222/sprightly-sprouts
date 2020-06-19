package com.sprouts.composition.text.editable;

public interface ITextModelListener {

	public void textInserted(ITextModel model, int offset, int count);
	
	public void textRemoved(ITextModel model, int offset, int count);
	
}
