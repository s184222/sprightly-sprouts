package com.sprouts.composition.resource;

import java.util.NoSuchElementException;

/**
 * @author Christian
 */
public interface IResourcePack {

	public <T> void putResource(ResourceType<T> type, T resource);

	public <T> T getResource(ResourceType<T> type) throws NoSuchElementException;

	public <T> boolean hasResource(ResourceType<T> type);
	
}
