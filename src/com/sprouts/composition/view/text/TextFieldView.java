package com.sprouts.composition.view.text;

import com.sprouts.composition.Composition;
import com.sprouts.composition.text.editable.TextFieldComposition;

public abstract class TextFieldView extends EditableTextCompositionView {

	@Override
	protected void onBindView(Composition comp) {
		onBindView((TextFieldComposition)comp);
	}

	@Override
	protected void onUnbindView(Composition comp) {
		onUnbindView((TextFieldComposition)comp);
	}
	
	protected abstract void onBindView(TextFieldComposition textField);

	protected abstract void onUnbindView(TextFieldComposition textField);
	
}
