package org.jpef.splitter;

import java.lang.reflect.Array;

import org.jpef.parallel.Splitter;

/**
 * A {@link Splitter} implementation for splitting object arrays.
 * 
 * @author Gergely Kiss
 * 
 */
public class ArraySplitter implements Splitter<Object> {

	/**
	 * Splits the input array in two halves.
	 */
	public Object[] split(Object input) {
		int len = Array.getLength(input);
		
		if (len < 2) {
			return null;
		}

		int flen = len / 2;
		int slen = len - flen;

		Class<?> type = input.getClass().getComponentType();
		Object[] ret = new Object[2];
		ret[0] = Array.newInstance(type, flen);
		ret[1] = Array.newInstance(type, slen);

		System.arraycopy(input, 0, ret[0], 0, flen);
		System.arraycopy(input, flen, ret[1], 0, slen);

		return ret;
	}
}
