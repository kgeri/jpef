package org.jpef.parallel;

import org.jpef.base.ListJoiner;

/**
 * An interface for joining solutions.
 * 
 * <p>
 * Solution joining is as simple as merging two instances of the return type to
 * one. If this is impossible for a given type, then parallel execution of a
 * method returning this type is not possible. <b>Implementations must always be
 * stateless!</b>
 * </p>
 * 
 * <p>
 * Some generic joiner implementations may be found at {@link org.jpef.base},
 * but most probably you will have to write your own Splitter-Joiner pair.
 * </p>
 * 
 * @author Gergely Kiss
 * 
 * @param <S>
 *            The solution's type
 * @see Splitter
 */
public interface Joiner<S> {

	/**
	 * Joins the first and second halves of the solution.
	 * 
	 * <p>
	 * Note: the first instance of the solution may be reused and returned, if
	 * its implementation makes it possible. For details see {@link ListJoiner}.
	 * </p>
	 * 
	 * <p>
	 * Testing for nulls is only necessary if the parallelized method may return
	 * nulls. In this case the handling of null values (ingoring them or
	 * signalling an error) depends entirely on this implementation.
	 * </p>
	 * 
	 * @param first
	 *            The first half of the solution
	 * @param second
	 *            The second half of the solution
	 * 
	 * @return The joined solution
	 */
	S join(S first, S second);
}