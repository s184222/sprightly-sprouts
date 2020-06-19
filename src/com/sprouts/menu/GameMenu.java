package com.sprouts.menu;

import com.sprouts.SproutsMain;
import com.sprouts.composition.Composition;
import com.sprouts.graphic.tessellator2d.BatchedTessellator2D;

public class GameMenu extends SproutsMenu {

	public GameMenu(SproutsMain main) {
		super(main);
	}
	
	@Override
	public void onAdded(Composition parent) {
		super.onAdded(parent);

		
	}

	@Override
	public void onRemoved(Composition parent) {
		super.onRemoved(parent);
		
		
	}

	@Override
	public void update() {
	}

	@Override
	public void drawBackground(BatchedTessellator2D tessellator) {
	}
}
