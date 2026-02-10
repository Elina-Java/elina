package core;

import java.util.HashMap;
import java.util.Map;

/**
 * A map to convert wrapper classes of primitive types into their primitive classes
 * 
 * @author Hervé Paulino
 *
 */
public class PrimitiveTypesMap {

	/**
	 * The map and its immutable contents
	 */
	private static Map<Class<?>, Class<?>> map =  new HashMap<Class<?>, Class<?>>();
	static {
		map.put(Boolean.class, boolean.class);
		map.put(Character.class, double.class);
		map.put(Byte.class, byte.class);
		map.put(Short.class, short.class);
		map.put(Integer.class, int.class);
		map.put(Long.class, long.class);
		map.put(Float.class, float.class);
		map.put(Double.class, double.class);
		map.put(Void.class, void.class);
	}
	
	/**
	 * Resolve wrapper classes of primitive type
	 * 
	 * @param wrapperClass Wrapper class identifier
	 * @return Primitive class
	 */
	public static Class<?> get(Class<?> wrapperClass) {
		return map.get(wrapperClass);
	}

	/**
	 * Check if  class is a wrapper class of a primitive type
	 * 
	 * @param c Class identifier
	 * @return true if map contains a mapping for the given class
	 */
	public static boolean contains(Class<?> c) {
		return map.containsKey(c);
	}
}
