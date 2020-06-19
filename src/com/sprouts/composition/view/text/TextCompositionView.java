package com.sprouts.composition.view.text;

import com.sprouts.composition.Composition;
import com.sprouts.composition.CompositionSize;
import com.sprouts.composition.border.Margin;
import com.sprouts.composition.text.TextAlignment;
import com.sprouts.composition.text.TextComposition;
import com.sprouts.composition.view.CompositionView;
import com.sprouts.graphic.font.Font;
import com.sprouts.graphic.font.TextBounds;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

public abstract class TextCompositionView extends CompositionView {

	public static final String TRIMMED_TEXT_ELLIPSIS = "...";
	
	public void drawAlignedText(TextComposition textComp, ITessellator2D tessellator, String text) {
		if (text == null || text.isEmpty())
			return;

		Font font = textComp.getFont();
		TextAlignment alignment = textComp.getTextAlignment();

		Margin padding = textComp.getPadding();
		
		TextBounds textBounds = font.getTextBounds(text);
		
		// It is assumed that default alignment is LEFT.
		float x = textComp.getX() + padding.left - textBounds.x;
		if (alignment == TextAlignment.CENTER) {
			x += (textComp.getWidth() - padding.left - padding.right - textBounds.width) / 2.0f;
		} else if (alignment == TextAlignment.RIGHT) {
			x += textComp.getWidth() - padding.left - padding.right - textBounds.width;
		}
		
		float y = textComp.getY() + padding.top - textBounds.y;
		
		y += (textComp.getHeight() - padding.top - padding.bottom - textBounds.height) / 2.0f;
		
		font.drawString(tessellator, text, x, y);
	}
	
	@Override
	public CompositionSize getMinimumSize(Composition comp) {
		TextComposition textComp = (TextComposition)comp;
		
		Font font = textComp.getFont();
		String text = textComp.getText();
		
		TextBounds textBounds = font.getTextBounds(text);
		int tw = (int)Math.ceil(textBounds.width);
		int th = (int)Math.ceil(textBounds.height);

		Margin padding = comp.getPadding();
		int width = padding.getHorizontalMargin() + Math.max(1, tw);
		int height = padding.getVerticalMargin() + Math.max(1, th);
		
		return new CompositionSize(width, height);
	}
}
