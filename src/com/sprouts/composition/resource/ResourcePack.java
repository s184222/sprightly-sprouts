package com.sprouts.composition.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Christian
 */
public class ResourcePack implements IResourcePack {

	private final Map<ResourceType<?>, Object> resources;
	
	public ResourcePack() {
		resources = new HashMap<ResourceType<?>, Object>();
	}
	
	@Override
	public <T> void putResource(ResourceType<T> type, T resource) {
		if (type == null)
			throw new IllegalArgumentException("type is null!");
		if (resource == null)
			throw new IllegalArgumentException("resource is null!");
		
		if (!type.getResourceClass().isInstance(resource))
			throw new IllegalArgumentException("resource is not a " + type.getResourceClass());
		
		resources.put(type, resource);
	}

	@Override
	public <T> T getResource(ResourceType<T> type) throws NoSuchElementException {
		T resource = type.cast(resources.get(type));
		if (resource == null)
			throw new NoSuchElementException("Resource '" + type.getName() + "' does not exist!");
		return resource;
	}
	
	@Override
	public <T> boolean hasResource(ResourceType<T> type) {
		return resources.containsKey(type);
	}
}
