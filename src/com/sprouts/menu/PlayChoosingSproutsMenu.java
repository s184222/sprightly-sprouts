package com.sprouts.menu;

import com.sprouts.SproutsMain;
import com.sprouts.composition.ParentComposition;
import com.sprouts.composition.border.Margin;
import com.sprouts.composition.layout.CompositionFill;
import com.sprouts.composition.layout.GridLayoutManager;
import com.sprouts.composition.layout.LayoutSpecification;
import com.sprouts.composition.text.ButtonComposition;
import com.sprouts.composition.text.TextAlignment;
import com.sprouts.graphic.tessellator2d.BatchedTessellator2D;

public class PlayChoosingSproutsMenu extends SproutsMenu {

	private final GameMenu gameMenu;
	
	private final ButtonComposition playButton;
	private final ButtonComposition loadButton;
	
	public PlayChoosingSproutsMenu(SproutsMain main, GameMenu gameMenu) {
		super(main);

		this.gameMenu = gameMenu;
		
		playButton = new ButtonComposition("New Game");
		loadButton = new ButtonComposition("Load Game");
		
		uiLayout();
		uiEvents();
	}
	
	private void uiLayout() {
		ParentComposition buttonPanel = new ParentComposition(new GridLayoutManager(0, 1, 0, 30));
		
		LayoutSpecification spec = new LayoutSpecification();
		
		Margin buttonPadding = new Margin(60, 60, 25, 25);
		
		playButton.setPadding(buttonPadding);
		playButton.setTextAlignment(TextAlignment.CENTER);
		loadButton.setPadding(buttonPadding);
		loadButton.setTextAlignment(TextAlignment.CENTER);

		buttonPanel.add(wrapOverlay(playButton));
		buttonPanel.add(wrapOverlay(loadButton));
	
		spec.setHorizontalFill(CompositionFill.FILL_MINIMUM);
		spec.setVerticalFill(CompositionFill.FILL_MINIMUM);
		spec.setHorizontalAlignment(LayoutSpecification.ALIGN_CENTER);
		spec.setVerticalAlignment(LayoutSpecification.ALIGN_CENTER);
		
		add(buttonPanel, spec);
	}
	
	private void uiEvents() {
		playButton.addButtonListener((source) -> {
			gameMenu.reset(8);
			
			main.setMenu(gameMenu);
		});

		loadButton.addButtonListener((source) -> {
			main.setMenu(new LoadGameSproutsMenu(main, this, gameMenu));
		});
	}
	
	@Override
	public void update() {
	}

	@Override
	public void drawBackground(BatchedTessellator2D tessellator) {
	}
}
