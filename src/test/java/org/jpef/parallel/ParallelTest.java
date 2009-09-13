package org.jpef.parallel;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;

import org.jpef.example.IntSumJoiner;
import org.junit.Test;

/**
 * Different parallel execution test cases.
 * 
 * @author Gergely Kiss
 * 
 */
public class ParallelTest {
	private MyService service = new MyServiceImpl();

	public ParallelTest() {
		ParallelManager.registerJoiner(int.class, new IntSumJoiner());
	}

	@Test
	public void testParallelProxy() {
		MyService proxy = Parallel.proxy(service, MyService.class);

		// NoOp
		assertEquals(5, service.noop(new int[] { 1, 2, 3, 4, 5 }).length);
		assertEquals(5, proxy.noop(new int[] { 1, 2, 3, 4, 5 }).length);

		// Sum
		assertEquals(15, service.sum(new int[] { 1, 2, 3, 4, 5 }));
		assertEquals(15, proxy.sum(new int[] { 1, 2, 3, 4, 5 }));
	}

	@Test
	public void testParallelInvocation() throws InvocationTargetException {
		// NoOp
		assertEquals(5,
				Parallel.invoke(service, "noop", int[].class, new int[] { 1, 2, 3, 4, 5 }).length);

		// Sum
		assertEquals(new Integer(15), Parallel.invoke(service, "sum", int.class, new int[] { 1, 2,
				3, 4, 5 }));
	}
}
