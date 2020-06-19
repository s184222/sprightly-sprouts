package com.sprouts.menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import com.sprouts.SproutsMain;
import com.sprouts.composition.Composition;
import com.sprouts.composition.CompositionSize;
import com.sprouts.composition.ParentComposition;
import com.sprouts.composition.border.Margin;
import com.sprouts.composition.drawable.TextureOverlayDrawable;
import com.sprouts.composition.event.FocusEvent;
import com.sprouts.composition.event.IFocusEventListener;
import com.sprouts.composition.layout.CompositionFill;
import com.sprouts.composition.layout.LayoutDirection;
import com.sprouts.composition.layout.LayoutSpecification;
import com.sprouts.composition.layout.LinearLayoutManager;
import com.sprouts.composition.text.ButtonComposition;
import com.sprouts.composition.text.LabelComposition;
import com.sprouts.composition.text.TextAlignment;
import com.sprouts.composition.text.editable.TextFieldComposition;
import com.sprouts.graphic.tessellator2d.BatchedTessellator2D;

public class LoadGameSproutsMenu extends SproutsMenu {

	private static final int MAX_PREVIEW_LINES = 10;
	
	private final SproutsMenu prevMenu;
	
	private final TextFieldComposition pathField;
	private final ButtonComposition browseButton;
	
	private final ParentComposition preview;
	
	private final ButtonComposition cancelButton;
	private final ButtonComposition loadButton;
	
	public LoadGameSproutsMenu(SproutsMain main, SproutsMenu prevMenu) {
		super(main);
		
		this.prevMenu = prevMenu;
		
		pathField = new TextFieldComposition();
		browseButton = new ButtonComposition("Browse");
		
		preview = new ParentComposition();
		
		cancelButton = new ButtonComposition("Cancel");
		loadButton = new ButtonComposition("Load");
		
		uiLayout();
		uiEvents();
	}
	
	private void uiLayout() {
		ParentComposition content = new ParentComposition(new LinearLayoutManager(LayoutDirection.VERTICAL, 20));
		content.setPadding(new Margin(10));
		content.setMinimumSize(new CompositionSize(700, 500));
		
		LayoutSpecification spec = new LayoutSpecification();
		
		spec.setVerticalFill(CompositionFill.FILL_MINIMUM);
		content.add(createFieldPanel(), spec);

		spec.setHorizontalFill(CompositionFill.FILL_REMAINING);
		spec.setVerticalFill(CompositionFill.FILL_REMAINING);
		content.add(preview, spec);

		spec.setVerticalFill(CompositionFill.FILL_MINIMUM);
		content.add(createButtonPanel(), spec);
	
		spec.setHorizontalFill(CompositionFill.FILL_MINIMUM);
		spec.setVerticalFill(CompositionFill.FILL_MINIMUM);
		spec.setHorizontalAlignment(LayoutSpecification.ALIGN_CENTER);
		spec.setVerticalAlignment(LayoutSpecification.ALIGN_CENTER);
		add(content, spec);
		
		pathField.setBackground(new TextureOverlayDrawable(main.getPostTexture(), pathField.getBackground()));
		
		// Query the view resources. This should probably be done in a different
		// way, but it is simple, and it works.
		preview.setBackground(pathField.getBackground());
		preview.setBorder(browseButton.getBorder());
		
		preview.setLayoutManager(new LinearLayoutManager(LayoutDirection.VERTICAL, 1));
	}

	private Composition createFieldPanel() {
		ParentComposition fieldPanel = new ParentComposition(new LinearLayoutManager(LayoutDirection.HORIZONTAL, 20));

		LayoutSpecification spec = new LayoutSpecification();
		spec.setVerticalFill(CompositionFill.FILL_MINIMUM);
		spec.setHorizontalAlignment(LayoutSpecification.ALIGN_CENTER);
		spec.setVerticalAlignment(LayoutSpecification.ALIGN_CENTER);

		spec.setHorizontalFill(CompositionFill.FILL_MINIMUM);
		fieldPanel.add(new LabelComposition("File Path: "), spec);
		spec.setHorizontalFill(CompositionFill.FILL_REMAINING);
		fieldPanel.add(pathField, spec);
		spec.setHorizontalFill(CompositionFill.FILL_MINIMUM);
		browseButton.setPadding(new Margin(30, 30, 5, 5));
		browseButton.setTextAlignment(TextAlignment.CENTER);
		fieldPanel.add(wrapOverlay(browseButton), spec);
		
		return fieldPanel;
	}
	
	private Composition createButtonPanel() {
		ParentComposition buttonPanel = new ParentComposition();
		
		LayoutSpecification spec = new LayoutSpecification();
		spec.setHorizontalFill(CompositionFill.FILL_MINIMUM);
		spec.setVerticalFill(CompositionFill.FILL_MINIMUM);
		spec.setVerticalAlignment(LayoutSpecification.ALIGN_BOTTOM);

		spec.setHorizontalAlignment(LayoutSpecification.ALIGN_LEFT);
		cancelButton.setPadding(new Margin(30, 30, 5, 5));
		cancelButton.setTextAlignment(TextAlignment.CENTER);
		buttonPanel.add(wrapOverlay(cancelButton), spec);
		
		spec.setHorizontalAlignment(LayoutSpecification.ALIGN_RIGHT);
		loadButton.setMinimumSize(cancelButton.getMinimumSize());
		loadButton.setTextAlignment(TextAlignment.CENTER);
		buttonPanel.add(wrapOverlay(loadButton), spec);
		
		return buttonPanel;
	}
	
	private void uiEvents() {
		pathField.addFocusEventListener(new IFocusEventListener() {
			@Override
			public void focusLost(FocusEvent event) {
				updatePreview();
			}
			
			@Override
			public void focusGained(FocusEvent event) {
			}
		});
		
		browseButton.addButtonListener((source) -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ignore) {
			}
			
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			int result = chooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				pathField.setText(file.getAbsolutePath());
				updatePreview();
			}
		});
		
		cancelButton.addButtonListener((source) -> {
			main.setMenu(prevMenu);
		});
		
		loadButton.addButtonListener((source) -> {
			main.setMenu(new GameMenu(main));
		});
	}
	
	@Override
	public void update() {
	}

	@Override
	public void drawBackground(BatchedTessellator2D tessellator) {
	}
	
	private void updatePreview() {
		preview.removeAll();

		File file = new File(pathField.getText());
		if (file.isFile()) {
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				int lineCount = 0;
				
				LayoutSpecification spec = new LayoutSpecification();
				spec.setHorizontalFill(CompositionFill.FILL_MINIMUM);
				spec.setVerticalFill(CompositionFill.FILL_MINIMUM);
				spec.setHorizontalAlignment(LayoutSpecification.ALIGN_LEFT);
				spec.setVerticalAlignment(LayoutSpecification.ALIGN_CENTER);
				
				String line;
				while ((line = br.readLine()) != null && ++lineCount < MAX_PREVIEW_LINES)
					preview.add(new LabelComposition(line), spec);
				
				if (line != null)
					preview.add(new LabelComposition("..."), spec);
			} catch (IOException e) {
			}
		}
	}
}
