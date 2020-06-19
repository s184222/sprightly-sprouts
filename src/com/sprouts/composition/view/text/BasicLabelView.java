package com.sprouts.composition.view.text;

import com.sprouts.composition.Composition;
import com.sprouts.composition.text.LabelComposition;
import com.sprouts.composition.view.BorderViewHandler;
import com.sprouts.composition.view.DrawableViewHandler;
import com.sprouts.composition.view.MaterialViewHandler;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * @author Christian
 */
public class BasicLabelView extends LabelView {

	private final DrawableViewHandler background;
	private final BorderViewHandler border;
	private final MaterialViewHandler textColor;
	
	private String trimmedText;
	
	public BasicLabelView() {
		background = new DrawableViewHandler();
		border = new BorderViewHandler();
		textColor = new MaterialViewHandler();

		registerViewHandler(background);
		registerViewHandler(border);
		registerViewHandler(textColor);
	}
	
	@Override
	protected void onBindView(LabelComposition label) {
		background.setResourceType(LabelComposition.BACKGROUND_RESOURCE);
		border.setResourceType(LabelComposition.BORDER_RESOURCE);
		textColor.setResourceType(LabelComposition.TEXT_COLOR_RESOURCE);
	}
	
	@Override
	protected void onUnbindView(LabelComposition label) {
		trimmedText = null;
	}
	
	@Override
	public void layoutChanged(Composition comp) {
		super.layoutChanged(comp);
		
		updateTrimmedText((LabelComposition)comp);
	}
	
	private void updateTrimmedText(LabelComposition label) {
		String text = label.getText();
		if (!text.isEmpty()) {
			trimmedText = label.getFont().trimText(text, label.getWidth(), TRIMMED_TEXT_ELLIPSIS);
		} else {
			trimmedText = text;
		}
	}

	@Override
	protected void drawView(Composition comp, ITessellator2D tessellator) {
		drawBackground(comp, tessellator, background);
		drawBorder(comp, tessellator, border);
		drawText((LabelComposition)comp, tessellator);
	}
	
	private void drawText(LabelComposition label, ITessellator2D tessellator) {
		tessellator.clearMaterial();
		textColor.applyMaterial(tessellator);

		drawAlignedText(label, tessellator, trimmedText);
	}
}
