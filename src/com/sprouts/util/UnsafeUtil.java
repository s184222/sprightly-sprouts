package com.sprouts.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class UnsafeUtil {

	private static final sun.misc.Unsafe UNSAFE;
	
	static {
		UNSAFE = scanAndGetUnsafeInstance();
	}
	
	public static sun.misc.Unsafe getUnsafe() {
		return UNSAFE;
	}
	
	private static sun.misc.Unsafe scanAndGetUnsafeInstance() {
		Field[] fields = sun.misc.Unsafe.class.getDeclaredFields();

		/*
		 * Different runtimes use different names for the Unsafe singleton, so
		 * we cannot use .getDeclaredField and we scan instead. For example:
		 * 
		 * Oracle: theUnsafe
		 * PERC : m_unsafe_instance
		 * Android: THE_ONE
		 */
		for (Field field : fields) {
			if (!field.getType().equals(sun.misc.Unsafe.class))
				continue;

			int modifiers = field.getModifiers();
			if (!(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)))
				continue;

			try {
				field.setAccessible(true);
				return (sun.misc.Unsafe) field.get(null);
			} catch (Exception ignored) {
			}
			
			break;
		}

		return null;
	}
}
