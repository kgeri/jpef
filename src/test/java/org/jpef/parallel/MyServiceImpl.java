package org.jpef.parallel;

import java.util.List;


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
		if (numbers == null) {
			return 0;
		}
		int sum = 0;
		for (Integer number : numbers) {
			sum += number;
		}
		return sum;
	}

	public int sum2(List<Integer> numbers) {
		if (numbers == null) {
			return 0;
		}
		int sum = 0;
		for (Integer number : numbers) {
			sum += number;
		}
		return sum;
	}

	public String missingArgs() {
		return null;
	}

	public String missingJoiner(int[] numbers) {
		return null;
	}
}
