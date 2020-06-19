package com.sprouts.menu;

import com.sprouts.SproutsMain;
import com.sprouts.composition.ParentComposition;
import com.sprouts.composition.border.Margin;
import com.sprouts.composition.layout.CompositionFill;
import com.sprouts.composition.layout.GridLayoutManager;
import com.sprouts.composition.layout.LayoutSpecification;
import com.sprouts.composition.text.ButtonComposition;
import com.sprouts.composition.text.TextAlignment;

public class MainSproutsMenu extends SproutsMenu {

	private final ButtonComposition playButton;
	private final ButtonComposition loadButton;
	private final ButtonComposition quitButton;
	
	public MainSproutsMenu(SproutsMain main) {
		super(main);

		playButton = new ButtonComposition("Play Game");
		loadButton = new ButtonComposition("Load Game");
		quitButton = new ButtonComposition("Quit");
		
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
		quitButton.setPadding(buttonPadding);
		quitButton.setTextAlignment(TextAlignment.CENTER);

		buttonPanel.add(wrapOverlay(playButton));
		buttonPanel.add(wrapOverlay(loadButton));
		buttonPanel.add(wrapOverlay(quitButton));
	
		spec.setHorizontalFill(CompositionFill.FILL_MINIMUM);
		spec.setVerticalFill(CompositionFill.FILL_MINIMUM);
		spec.setHorizontalAlignment(LayoutSpecification.ALIGN_CENTER);
		spec.setVerticalAlignment(LayoutSpecification.ALIGN_CENTER);
		
		add(buttonPanel, spec);
	}
	
	private void uiEvents() {
		playButton.addButtonListener((source) -> {
			main.setMenu(new GameMenu(main));
		});

		loadButton.addButtonListener((source) -> {
			main.setMenu(new LoadGameSproutsMenu(main, this));
		});

		quitButton.addButtonListener((source) -> {
			main.stop();
		});
	}
	
	@Override
	public void update() {
	}
}
