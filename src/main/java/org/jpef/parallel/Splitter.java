package org.jpef.parallel;

/**
 * An interface for splitting problems spaces in half.
 * 
 * <p>
 * Splitting means the input instance must be cut into two (preferably equally
 * sized) parts of the same type. If this is impossible for a given type, then
 * parallel execution of a method containing this type is not possible.
 * </p>
 * 
 * <p>
 * Some generic splitter implementations may be found at
 * {@link org.jpef.splitter}, but most probably you will have to write your own
 * Splitter-Joiner pair.
 * </p>
 * 
 * @author Gergely Kiss
 * 
 * @param <P>
 *            The problem space's type, usually a Map, List or an array
 * @see Joiner
 */
public interface Splitter<P> {

	/**
	 * Splits the input problem into two, preferably equally sized subproblems.
	 * 
	 * <p>
	 * The implementation should be prepared for degenerate cases, eg. a List of
	 * one element. In this case <code>null</code> may be returned.
	 * </p>
	 * 
	 * <p>
	 * Testing for nulls is only necessary if the parallelized method may
	 * receive null arguments. In this case the handling of null values
	 * (ingoring them or signalling an error) depends entirely on this
	 * implementation.
	 * </p>
	 * 
	 * @param input
	 *            The input problem
	 * 
	 * @return P[2], containing the first and second half of the problem, or
	 *         <code>null</code> if further splitting of the input is impossible
	 */
	P[] split(P input);
}
