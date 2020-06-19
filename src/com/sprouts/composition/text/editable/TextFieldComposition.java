package com.sprouts.composition.text.editable;

import com.sprouts.composition.view.ICompositionView;
import com.sprouts.composition.view.text.BasicTextFieldView;
import com.sprouts.composition.view.text.EditableTextCompositionView;
import com.sprouts.composition.view.text.TextFieldView;

public class TextFieldComposition extends EditableTextComposition {

	public TextFieldComposition() {
		this("");
	}
	
	public TextFieldComposition(String text) {
		super(new SingleLineTextModel());
		
		if (text != null) {
			ITextModel textModel = getTextModel();
			textModel.insertText(0, text);
		}

		setView(new BasicTextFieldView());
	}
	
	public void setView(TextFieldView view) {
		super.setView(view);
	}
	
	@Override
	public void setView(ICompositionView view) {
		if (!(view instanceof TextFieldView))
			throw new IllegalArgumentException("view is not a TextFieldView!");
		
		super.setView(view);
	}
	
	@Override
	public EditableTextCompositionView getView() {
		return (EditableTextCompositionView)super.getView();
	}
	
	@Override
	public String getText() {
		ITextModel textModel = getTextModel();
		return textModel.getText(0, textModel.getLength());
	}
	
	@Override
	public void setText(String text) {
		ITextModel textModel = getTextModel();
		if (textModel.getLength() != 0)
			textModel.removeText(0, textModel.getLength());
		if (text != null && !text.isEmpty())
			textModel.insertText(0, text);
	}
	
	public void appendText(String text) {
		if (text == null || text.isEmpty())
			return;

		getTextModel().appendText(text);
	}
}
