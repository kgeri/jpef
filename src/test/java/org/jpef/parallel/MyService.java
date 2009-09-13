package org.jpef.parallel;


/**
 * Sample test interface.
 * 
 * @author Gergely Kiss
 */
public interface MyService {

	/**
	 * Does nothing to the given numbers.
	 * 
	 * @param numbers
	 * @return
	 */
	int[] noop(int[] numbers);

	/**
	 * Sums the given numbers.
	 * 
	 * @param numbers
	 * @return
	 */
	int sum(int[] numbers);

}
