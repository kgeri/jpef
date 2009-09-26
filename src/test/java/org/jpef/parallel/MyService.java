package org.jpef.parallel;

import java.util.List;


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

	/**
	 * Sums the given numbers.
	 * 
	 * @param numbers
	 * @return
	 */
	int sum2(List<Integer> numbers);

	/**
	 * Bogus parallel method.
	 */
	String missingArgs();
	
	/**
	 * Bogus parallel method.
	 */
	String missingJoiner(int[] numbers);

}
