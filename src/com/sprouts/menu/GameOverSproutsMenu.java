package com.sprouts.menu;

import com.sprouts.SproutsMain;
import com.sprouts.composition.ParentComposition;
import com.sprouts.composition.border.Margin;
import com.sprouts.composition.drawable.ColorDrawable;
import com.sprouts.composition.drawable.TextureOverlayDrawable;
import com.sprouts.composition.layout.CompositionFill;
import com.sprouts.composition.layout.LayoutDirection;
import com.sprouts.composition.layout.LayoutSpecification;
import com.sprouts.composition.layout.LinearLayoutManager;
import com.sprouts.composition.text.ButtonComposition;
import com.sprouts.composition.text.LabelComposition;
import com.sprouts.composition.text.TextAlignment;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator2d.BatchedTessellator2D;

public class GameOverSproutsMenu extends SproutsMenu {

	private static final String GAME_OVER_TITLE = "Game Over";
	
	private final String subtitle;
	
	private final ButtonComposition mainMenuButton;
	
	public GameOverSproutsMenu(SproutsMain main, String subtitle) {
		super(main);
		
		this.subtitle = subtitle;
		
		mainMenuButton = new ButtonComposition("Back to Main Menu");
		
		uiLayout();
		uiEvents();
	}

	private void uiLayout() {
		setBackground(new TextureOverlayDrawable(main.getPostTexture(), new ColorDrawable(VertexColor.BLACK.withAlpha(96))));

		ParentComposition content = new ParentComposition(new LinearLayoutManager(LayoutDirection.VERTICAL, 30));
		
		LabelComposition titleLabel = new LabelComposition(GAME_OVER_TITLE);
		titleLabel.setTextAlignment(TextAlignment.CENTER);
		titleLabel.setFont(titleLabel.getResourceManager().createFont(80.0f));
		
		content.add(titleLabel);
		if (subtitle != null) {
			LabelComposition subtitleLabel = new LabelComposition(subtitle);
			subtitleLabel.setTextColor(VertexColor.LIGHT_GRAY);
			subtitleLabel.setTextAlignment(TextAlignment.CENTER);
			content.add(subtitleLabel);
		}

		LayoutSpecification spec = new LayoutSpecification();
		spec.setHorizontalFill(CompositionFill.FILL_MINIMUM);
		spec.setVerticalFill(CompositionFill.FILL_MINIMUM);
		spec.setHorizontalAlignment(LayoutSpecification.ALIGN_CENTER);
		spec.setVerticalAlignment(LayoutSpecification.ALIGN_CENTER);
		
		mainMenuButton.setPadding(new Margin(30, 30, 10, 10));
		content.add(mainMenuButton, spec);
		
		add(content, spec);
	}
	
	private void uiEvents() {
		mainMenuButton.addButtonListener((source) -> {
			main.setMenu(new MainSproutsMenu(main));
		});
	}
	
	@Override
	public void update() {
	}

	@Override
	public void drawBackground(BatchedTessellator2D tessellator) {
	}
}
