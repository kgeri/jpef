package org.jpef.parallel;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Different parallel execution test cases.
 * 
 * @author Gergely Kiss
 * 
 */
public class ParallelTest {
	private MyService service = new MyServiceImpl();

	@Test
	public void testParallelExecution() throws Exception {
		MyService proxy = Parallel.proxy(service, MyService.class);

		// NoOp
		assertEquals(5, service.noop(new int[] { 1, 2, 3, 4, 5 }).length);
		assertEquals(5, proxy.noop(new int[] { 1, 2, 3, 4, 5 }).length);

		// Sum
		assertEquals(15, service.sum(new int[] { 1, 2, 3, 4, 5 }));
		assertEquals(15, proxy.sum(new int[] { 1, 2, 3, 4, 5 }));
	}
}
