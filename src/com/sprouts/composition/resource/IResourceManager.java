package com.sprouts.composition.resource;

import com.sprouts.IResource;
import com.sprouts.composition.Composition;
import com.sprouts.graphic.font.Font;
import com.sprouts.graphic.font.FontData;

/**
 * @author Christian
 */
public interface IResourceManager extends IResource {

	default public Font createFont(float fontSize) {
		return createFont(getDefFontData(), fontSize);
	}

	public Font createFont(FontData fontData, float fontSize);
	
	public FontData getDefFontData();
	
	public <T extends IResource> T registerResource(T resource);
	
	public void putResourcePack(Class<? extends Composition> clazz, IResourcePack pack);

	default public IResourcePack getResourcePack(Composition comp) {
		return getResourcePack(comp.getClass());
	}
	
	public IResourcePack getResourcePack(Class<? extends Composition> clazz);
	
}
