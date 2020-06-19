package com.sprouts.composition.resource;

/**
 * @author Christian
 */
public final class ResourceType<T> {

	private final String name;
	private final Class<T> resourceClazz;

	public ResourceType(String name, Class<T> clazz) {
		if (name == null)
			throw new IllegalArgumentException("name is null!");
		if (clazz == null)
			throw new IllegalArgumentException("clazz is null!");
		
		this.name = name;
		resourceClazz = clazz;
	}

	@SuppressWarnings("unchecked")
	public T cast(Object obj) {
		return (T)obj;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<T> getResourceClass() {
		return resourceClazz;
	}

	@Override
	public int hashCode() {
		int hash = name.hashCode();
		hash = 31 * hash + resourceClazz.hashCode();
		return hash;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ResourceType))
			return false;
		
		@SuppressWarnings("rawtypes")
		ResourceType otherType = (ResourceType)other;
		
		if (!otherType.name.equals(name))
			return false;
		if (!otherType.resourceClazz.equals(resourceClazz))
			return false;
		
		return true;
	}
}
