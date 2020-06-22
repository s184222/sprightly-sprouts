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

public class MainSproutsMenu extends SproutsMenu {

	private final ButtonComposition playFriendButton;
	private final ButtonComposition playAIButton;
	private final ButtonComposition quitButton;
	
	public MainSproutsMenu(SproutsMain main) {
		super(main);

		playFriendButton = new ButtonComposition("Play Game");
		playAIButton = new ButtonComposition("Play AI");
		quitButton = new ButtonComposition("Quit");
		
		uiLayout();
		uiEvents();
	}
	
	private void uiLayout() {
		ParentComposition buttonPanel = new ParentComposition(new GridLayoutManager(0, 1, 0, 30));
		
		LayoutSpecification spec = new LayoutSpecification();
		
		Margin buttonPadding = new Margin(60, 60, 25, 25);
		
		playFriendButton.setPadding(buttonPadding);
		playFriendButton.setTextAlignment(TextAlignment.CENTER);
		playAIButton.setPadding(buttonPadding);
		playAIButton.setTextAlignment(TextAlignment.CENTER);
		quitButton.setPadding(buttonPadding);
		quitButton.setTextAlignment(TextAlignment.CENTER);

		buttonPanel.add(wrapOverlay(playFriendButton));
		buttonPanel.add(wrapOverlay(playAIButton));
		buttonPanel.add(wrapOverlay(quitButton));
	
		spec.setHorizontalFill(CompositionFill.FILL_MINIMUM);
		spec.setVerticalFill(CompositionFill.FILL_MINIMUM);
		spec.setHorizontalAlignment(LayoutSpecification.ALIGN_CENTER);
		spec.setVerticalAlignment(LayoutSpecification.ALIGN_CENTER);
		
		add(buttonPanel, spec);
	}
	
	private void uiEvents() {
		playFriendButton.addButtonListener((source) -> {
			main.setMenu(new PlayChoosingSproutsMenu(main, this, new GameMenu(main)));
		});
		
		playAIButton.addButtonListener((source) -> {
			main.setMenu(new PlayChoosingSproutsMenu(main, this, new AIGameMenu(main)));
		});

		quitButton.addButtonListener((source) -> {
			main.stop();
		});
	}
	
	@Override
	public void update() {
	}

	@Override
	public void drawBackground(BatchedTessellator2D tessellator) {
	}
	
	@Override
	public boolean isSimpleBackground() {
		return false;
	}
}
