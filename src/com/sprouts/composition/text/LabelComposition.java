package com.sprouts.composition.text;

import com.sprouts.composition.view.ICompositionView;
import com.sprouts.composition.view.text.BasicLabelView;
import com.sprouts.composition.view.text.LabelView;

/**
 * @author Christian
 */
public class LabelComposition extends TextComposition {

	protected String text;
	
	public LabelComposition() {
		this(null);
	}
	
	public LabelComposition(String text) {
		this.text = (text == null) ? "" : text;
	
		setView(new BasicLabelView());
	}
	
	public void setView(LabelView view) {
		super.setView(view);
	}
	
	@Override
	public void setView(ICompositionView view) {
		if (!(view instanceof LabelView))
			throw new IllegalArgumentException("view is not a LabelView!");
		
		super.setView(view);
	}
	
	@Override
	public LabelView getView() {
		return (LabelView)view;
	}

	@Override
	public void setText(String text) {
		if (text == null)
			text = "";
		
		if (!text.equals(this.text)) {
			this.text = text;
			
			requestLayoutAndDraw();
		}
	}

	@Override
	public String getText() {
		return text;
	}
}
