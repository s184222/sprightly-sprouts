package com.sprouts.composition.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sprouts.IResource;
import com.sprouts.composition.Composition;
import com.sprouts.composition.ParentComposition;
import com.sprouts.composition.border.AnimatedUnderlineBorder;
import com.sprouts.composition.border.ColorBorder;
import com.sprouts.composition.border.EmptyBorder;
import com.sprouts.composition.border.Margin;
import com.sprouts.composition.drawable.ColorDrawable;
import com.sprouts.composition.drawable.EmptyDrawable;
import com.sprouts.composition.drawable.FadeColorDrawable;
import com.sprouts.composition.drawable.IDrawable;
import com.sprouts.composition.material.AnimationType;
import com.sprouts.composition.material.ColorGradientMaterial;
import com.sprouts.composition.material.IColorMaterial;
import com.sprouts.composition.text.ButtonComposition;
import com.sprouts.composition.text.LabelComposition;
import com.sprouts.composition.text.editable.TextFieldComposition;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.font.Font;
import com.sprouts.graphic.font.FontData;
import com.sprouts.graphic.font.FontLoader;

/**
 * @author Christian
 */
public class ResourceManager implements IResourceManager {

	private static final String DEFAULT_FONT_PATH = "/fonts/arial.ttf";
	private static final float DEFAULT_FONT_SIZE = 32.0f;
	
	private final Map<Class<? extends Composition>, IResourcePack> resourcePacks;
	private final List<IResource> resources;
	
	private FontData defFontData;
	private Font defFont;
	
	private boolean resourcesLoaded;
	
	public ResourceManager() {
		resourcePacks = new HashMap<Class<? extends Composition>, IResourcePack>();
		resources = new ArrayList<IResource>();
	}
	
	public void loadResources() throws IOException {
		if (resourcesLoaded)
			throw new IllegalStateException("Resources are already loaded!");
		
		defFontData = FontLoader.loadFont(DEFAULT_FONT_PATH);
		defFont = createFont(defFontData, DEFAULT_FONT_SIZE);

		IColorMaterial whiteMaterial = new ColorGradientMaterial(VertexColor.WHITE);

		IDrawable selectionBackground = new ColorDrawable(new VertexColor(192, 14, 108, 220));
		IColorMaterial selectionMaterial = new ColorGradientMaterial(VertexColor.LIGHT_YELLOW);
		
		Margin borderMargin = new Margin(2);
		VertexColor borderColor = VertexColor.DARK_GRAY.withAlpha(196);
		
		IResourcePack compositionPack = new ResourcePack();
		compositionPack.putResource(Composition.BACKGROUND_RESOURCE, EmptyDrawable.INSTANCE);
		compositionPack.putResource(Composition.BORDER_RESOURCE, EmptyBorder.INSTANCE);
		putResourcePack(Composition.class, compositionPack);
		putResourcePack(ParentComposition.class, compositionPack);
	
		IResourcePack labelPack = new ResourcePack();
		labelPack.putResource(LabelComposition.BACKGROUND_RESOURCE, EmptyDrawable.INSTANCE);
		labelPack.putResource(LabelComposition.BORDER_RESOURCE, EmptyBorder.INSTANCE);
		labelPack.putResource(LabelComposition.TEXT_COLOR_RESOURCE, whiteMaterial);
		labelPack.putResource(LabelComposition.FONT_RESOURCE, defFont);
		putResourcePack(LabelComposition.class, labelPack);
		
		IResourcePack buttonPack = new ResourcePack();
		buttonPack.putResource(ButtonComposition.BACKGROUND_RESOURCE, new ColorDrawable(VertexColor.DIM_GRAY.withAlpha(64)));
		buttonPack.putResource(ButtonComposition.BORDER_RESOURCE, new ColorBorder(borderMargin, borderColor));
		buttonPack.putResource(ButtonComposition.HOVERED_BACKGROUND_RESOURCE, new ColorDrawable(VertexColor.GRAY.withAlpha(96)));
		buttonPack.putResource(ButtonComposition.PRESSED_BACKGROUND_RESOURCE, new ColorDrawable(new VertexColor(96, 60, 60, 60)));
		buttonPack.putResource(ButtonComposition.TEXT_COLOR_RESOURCE, whiteMaterial);
		buttonPack.putResource(ButtonComposition.FONT_RESOURCE, defFont);
		putResourcePack(ButtonComposition.class, buttonPack);
		
		IResourcePack textFieldPack = new ResourcePack();
		textFieldPack.putResource(TextFieldComposition.BACKGROUND_RESOURCE, new ColorDrawable(VertexColor.DIM_GRAY.withAlpha(96)));
		textFieldPack.putResource(TextFieldComposition.FOCUSED_BACKGROUND_RESOURCE, new FadeColorDrawable(VertexColor.TRANSPARENT, AnimationType.QUAD_IN, 200));
		textFieldPack.putResource(TextFieldComposition.BORDER_RESOURCE, new AnimatedUnderlineBorder(borderMargin, borderColor, AnimationType.CUBIC_IN, 200));
		textFieldPack.putResource(TextFieldComposition.FOCUSED_BORDER_RESOURCE, new AnimatedUnderlineBorder(borderMargin, borderColor, AnimationType.CUBIC_OUT, 200));
		textFieldPack.putResource(TextFieldComposition.TEXT_COLOR_RESOURCE, whiteMaterial);
		textFieldPack.putResource(TextFieldComposition.FONT_RESOURCE, defFont);
		textFieldPack.putResource(TextFieldComposition.CARET_DRAWABLE_RESOURCE, new FadeColorDrawable(VertexColor.WHITE, 0, 100));
		textFieldPack.putResource(TextFieldComposition.SELECTION_BACKGROUND_RESOURCE, selectionBackground);
		textFieldPack.putResource(TextFieldComposition.SELECTION_TEXT_COLOR_RESOURCE, selectionMaterial);
		putResourcePack(TextFieldComposition.class, textFieldPack);
		
		resourcesLoaded = true;
	}

	
	@Override
	public Font createFont(FontData fontData, float fontSize) {
		return registerResource(fontData.createFont(fontSize));
	}

	@Override
	public FontData getDefFontData() {
		return defFontData;
	}
	
	@Override
	public <T extends IResource> T registerResource(T resource) {
		resources.add(resource);
		return resource;
	}
	
	@Override
	public void putResourcePack(Class<? extends Composition> clazz, IResourcePack pack) {
		if (clazz == null)
			throw new IllegalArgumentException("clazz is null!");
		if (pack == null)
			throw new IllegalArgumentException("pack is null!");
		
		resourcePacks.put(clazz, pack);
	}

	@Override
	public IResourcePack getResourcePack(Class<? extends Composition> clazz) {
		IResourcePack pack = resourcePacks.get(clazz);
		if (pack == null && clazz != Composition.class) {
			@SuppressWarnings("unchecked")
			Class<? extends Composition> sc = (Class<? extends Composition>)clazz.getSuperclass();
			return getResourcePack(sc);
		}
		
		return pack;
	}

	@Override
	public void dispose() {
		if (resourcesLoaded) {
			resourcesLoaded = false;
			
			resourcePacks.clear();
			
			resources.forEach(IResource::dispose);
			resources.clear();
		}
	}
}
