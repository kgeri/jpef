package org.jpef.joiner;

import java.lang.reflect.Array;

import org.jpef.parallel.Joiner;

/**
 * A {@link Joiner} implementation for merging object arrays.
 * 
 * @author Gergely Kiss
 * 
 */
public class ArrayJoiner implements Joiner<Object> {

	/**
	 * Merges the input arrays into a new array of the same type.
	 */
	public Object join(Object first, Object second) {
		int flen = Array.getLength(first);
		int slen = Array.getLength(second);

		Object ret = Array.newInstance(first.getClass().getComponentType(), flen + slen);

		System.arraycopy(first, 0, ret, 0, flen);
		System.arraycopy(second, 0, ret, flen, slen);

		return ret;
	}
}
