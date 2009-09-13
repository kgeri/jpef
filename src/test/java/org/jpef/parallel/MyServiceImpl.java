package org.jpef.parallel;


/**
 * Sample single-threaded service implementation.
 * 
 * @author Gergely Kiss
 * 
 */
public class MyServiceImpl implements MyService {

	public int[] noop(int[] numbers) {
		return numbers;
	}

	public int sum(int[] numbers) {
		int sum = 0;
		for (Integer number : numbers) {
			sum += number;
		}
		return sum;
	}
}
