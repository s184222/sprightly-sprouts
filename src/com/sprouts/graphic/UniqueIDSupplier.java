package com.sprouts.graphic;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A basic supplier for integer IDs. The purpose of this class is to provide
 * a stream of unique integer IDs that can be used for registering in the
 * graphics classes.
 * <br><br>
 * The IDs provided by this supplier are guaranteed to be unique given that
 * the contract defined by {@link #recycle(Object)} is followed.
 * 
 * @author Christian
 * 
 * @see #get()
 * @see #recycle(Object)
 */
public class UniqueIDSupplier<T extends UniqueIntegerID> implements Supplier<T> {

	private static final int INITIAL_ID_VALUE = 0;
	
	private final Function<Integer, T> idConstructor;
	private final int initialIdValue;

	private final LinkedList<T> recycledIds;
	private T nextId;
	
	public UniqueIDSupplier(Function<Integer, T> idConstructor) {
		this(idConstructor, INITIAL_ID_VALUE);
	}
	
	public UniqueIDSupplier(Function<Integer, T> idConstructor, int initialIdValue) {
		this.idConstructor = idConstructor;
		this.initialIdValue = initialIdValue;
		
		recycledIds = new LinkedList<T>();
		nextId = idConstructor.apply(initialIdValue);
	}
	
	/**
	 * Optional operation. Recycles the given ID, so it can be retrieved by
	 * the {@link #get()} method.
	 * <br><br>
	 * A call to this method should only take place if the given ID has been
	 * supplied by this supplier and is no longer in use. Any violation of
	 * this contract will invalidate the promise made by {@link #get()}.
	 * 
	 * @param integerId
	 * 
	 * @see #get()
	 */
	public void recycle(T integerId) {
		recycledIds.add(integerId);
	}
	
	/**
	 * Generates and retrieves a unique integer ID, given that the contract made
	 * by {@link #recycle(Object)} has been followed.
	 * <br><br>
	 * It is advised, but not required, that a call to {@link #recycle(Object)}
	 * should be made when the supplied ID is no longer in use.
	 * 
	 * @throws NoSuchElementException if there are no more unique IDs.
	 * 
	 * @see #recycle(Object)
	 */
	@Override
	public T get() throws NoSuchElementException {
		T result = recycledIds.pollFirst();
		
		if (result == null) {
			if (nextId == null)
				throw new NoSuchElementException("All unique IDs are in use.");

			result = nextId;
			
			int next = nextId.getId() + 1;
			
			// If we ever get to the initial layer id (very unlikely)
			// we have run out of elements. In this case the next call
			// to get() without a call to recycle() will then throw a 
			// NoSuchElementException.
			nextId = (next == initialIdValue) ? null : idConstructor.apply(next);
		}
		
		return result;
	}
}
