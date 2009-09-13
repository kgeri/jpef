package org.jpef.base;

import java.util.List;

import org.jpef.parallel.Joiner;

/**
 * A {@link Joiner} implementation for merging list results.
 * 
 * @author Gergely Kiss
 */
@SuppressWarnings("unchecked")
public class ListJoiner implements Joiner<List> {

	/**
	 * Joins the second list to the end of the first list.
	 * 
	 * <p>
	 * The first list instance is reused and returned.
	 * </p>
	 */
	public List join(List first, List second) {
		first.addAll(second);
		return first;
	}
}
