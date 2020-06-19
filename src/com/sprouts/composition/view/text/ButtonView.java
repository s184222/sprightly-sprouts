package com.sprouts.composition.view.text;

import com.sprouts.composition.Composition;
import com.sprouts.composition.text.ButtonComposition;

/**
 * @author Christian
 */
public abstract class ButtonView extends TextCompositionView {

	@Override
	protected final void onBindView(Composition comp) {
		onBindView((ButtonComposition)comp);
	}

	@Override
	protected final void onUnbindView(Composition comp) {
		onUnbindView((ButtonComposition)comp);
	}
	
	protected abstract void onBindView(ButtonComposition button);

	protected abstract void onUnbindView(ButtonComposition button);

}
