package com.sprouts.composition.view;

import com.sprouts.composition.Composition;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * @author Christian
 */
public final class BasicCompositionView extends CompositionView {

	private final DrawableViewHandler background;
	private final BorderViewHandler border;
	
	public BasicCompositionView() {
		background = new DrawableViewHandler();
		border = new BorderViewHandler();
		
		registerViewHandler(background);
		registerViewHandler(border);
	}
	
	@Override
	protected void onBindView(Composition comp) {
		background.setResourceType(Composition.BACKGROUND_RESOURCE);
		border.setResourceType(Composition.BORDER_RESOURCE);
	}

	@Override
	protected void onUnbindView(Composition comp) {
	}

	@Override
	protected void drawView(Composition comp, ITessellator2D tessellator) {
		drawBackground(comp, tessellator, background);
		drawBorder(comp, tessellator, border);
	}
}
