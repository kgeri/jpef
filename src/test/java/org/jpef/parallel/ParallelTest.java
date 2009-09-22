package org.jpef.parallel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

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
		assertEquals(1, proxy.noop(new int[] { 1 }).length);

		// Sum
		assertEquals(15, service.sum(new int[] { 1, 2, 3, 4, 5 }));
		assertEquals(15, proxy.sum(new int[] { 1, 2, 3, 4, 5 }));

		// Sum 2
		assertEquals(15, service.sum2(Arrays.asList(1, 2, 3, 4, 5)));
		assertEquals(15, proxy.sum2(Arrays.asList(1, 2, 3, 4, 5)));
		assertEquals(1, proxy.sum2(Arrays.asList(1)));
		assertEquals(0, proxy.sum2(null));

		// Errors
		try {
			proxy.missingArgs();
			fail("Expected error");
		} catch (IllegalArgumentException e) {
		}
		try {
			proxy.missingJoiner(new int[] { 1, 2, 3 });
			fail("Expected error");
		} catch (UnsupportedOperationException e) {
		}
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
