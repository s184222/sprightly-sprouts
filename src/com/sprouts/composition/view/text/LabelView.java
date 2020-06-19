package com.sprouts.composition.view.text;

import com.sprouts.composition.Composition;
import com.sprouts.composition.text.LabelComposition;

/**
 * @author Christian
 */
public abstract class LabelView extends TextCompositionView {

	@Override
	protected void onBindView(Composition comp) {
		onBindView((LabelComposition)comp);
	}

	@Override
	public void onUnbindView(Composition comp) {
		onUnbindView((LabelComposition)comp);
	}

	protected abstract void onBindView(LabelComposition label);

	protected abstract void onUnbindView(LabelComposition label);
	
}
