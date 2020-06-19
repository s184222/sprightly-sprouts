package com.sprouts.menu;

import com.sprouts.SproutsMain;
import com.sprouts.composition.Composition;
import com.sprouts.composition.ParentComposition;
import com.sprouts.composition.drawable.EmptyDrawable;
import com.sprouts.composition.drawable.TextureOverlayDrawable;
import com.sprouts.graphic.tessellator2d.BatchedTessellator2D;

public abstract class SproutsMenu extends ParentComposition {

	protected final SproutsMain main;
	
	public SproutsMenu(SproutsMain main) {
		this.main = main;
	}
	
	protected Composition wrapOverlay(Composition comp) {
		ParentComposition parent = new ParentComposition();
		parent.setBackground(new TextureOverlayDrawable(main.getPostTexture(), EmptyDrawable.INSTANCE));
		parent.add(comp);
		return parent;
	}
	
	public abstract void update();

	public abstract void drawBackground(BatchedTessellator2D tessellator);
	
}
