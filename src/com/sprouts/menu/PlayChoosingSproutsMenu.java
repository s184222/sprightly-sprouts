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

	private final SproutsMenu prevMenu;
	private final GameMenu gameMenu;
	
	private final ButtonComposition playButton;
	private final ButtonComposition loadButton;
	private final ButtonComposition backButton;
	
	public PlayChoosingSproutsMenu(SproutsMain main, SproutsMenu prevMenu, GameMenu gameMenu) {
		super(main);

		this.prevMenu = prevMenu;
		this.gameMenu = gameMenu;
		
		playButton = new ButtonComposition("New Game");
		loadButton = new ButtonComposition("Load Game");
		backButton = new ButtonComposition("Back");
		
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
		backButton.setPadding(buttonPadding);
		backButton.setTextAlignment(TextAlignment.CENTER);

		buttonPanel.add(wrapOverlay(playButton));
		buttonPanel.add(wrapOverlay(loadButton));
		buttonPanel.add(wrapOverlay(backButton));
	
		spec.setHorizontalFill(CompositionFill.FILL_MINIMUM);
		spec.setVerticalFill(CompositionFill.FILL_MINIMUM);
		spec.setHorizontalAlignment(LayoutSpecification.ALIGN_CENTER);
		spec.setVerticalAlignment(LayoutSpecification.ALIGN_CENTER);
		
		add(buttonPanel, spec);
	}
	
	private void uiEvents() {
		playButton.addButtonListener((source) -> {
			gameMenu.reset(5);
			
			main.setMenu(gameMenu);
		});

		loadButton.addButtonListener((source) -> {
			main.setMenu(new LoadGameSproutsMenu(main, this, gameMenu));
		});

		backButton.addButtonListener((source) -> {
			main.setMenu(prevMenu);
		});
	}
	
	@Override
	public void update() {
	}

	@Override
	public void drawBackground(BatchedTessellator2D tessellator) {
	}
}
