package org.jpef.parallel;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@SuppressWarnings("unchecked")
class ParallelInvocationHandler implements InvocationHandler {
	// TODO parametrize
	private ExecutorService executor = Executors.newFixedThreadPool(4);

	private final Object impl;
	private int maxExecutors = 2;

	
	public ParallelInvocationHandler(Object imp) {
		this.impl = imp;
	}

	public Object invoke(Object proxy, Method method, Object[] arguments)
			throws Throwable {
		method.setAccessible(true);

		Joiner joiner = Parallel.getJoiner(method.getReturnType());
		List<Future<Object>> futures = new ArrayList<Future<Object>>(
				maxExecutors);

		splitInvoke(method, arguments, 0, futures);
		
		return joinInvoke(joiner, 0, futures);
	}

	private Object joinInvoke(Joiner joiner, int level,
			Collection<Future<Object>> futures) throws ExecutionException, InterruptedException {
		//TODO or throw an ex instead?
		if (futures.size() == 0) {
			return null;
		}
		
		LinkedList<Object> results = new LinkedList<Object>();
		
		for (Future<Object> future : futures) {
			results.add(future.get());
		}
		
		//TODO what if results size is not a power of two
		while (results.size() > 1) {
			Object first = results.removeFirst();
			Object second = results.removeFirst();
			results.addLast(joiner.join(first, second));
		}
		
		return results.get(0);
	}

	private void splitInvoke(Method method, Object[] arguments, int level,
			Collection<Future<Object>> futures) throws Throwable {
		if (1 << level >= maxExecutors) {
			singleInvoke(method, arguments, futures);
			return;
		}

		Object[][] splitArgs = new Object[2][arguments.length];
		boolean splitNothing = true;

		for (int i = 0; i < arguments.length; i++) {
			Object argument = arguments[i];
			Splitter splitter = Parallel.getSplitter(argument.getClass());

			if (splitter == null) {
				splitArgs[0][i] = argument;
				splitArgs[1][i] = argument;
			} else {
				Object[] splitArg = splitter.split(argument);

				if (splitArg == null) {
					splitArgs[0][i] = argument;
					splitArgs[1][i] = argument;
				} else {
					splitArgs[0][i] = splitArg[0];
					splitArgs[1][i] = splitArg[1];
					splitNothing = false;
				}
			}
		}

		if (splitNothing) {
			// TODO warn
			singleInvoke(method, arguments, futures);
			return;
		}

		level++;

		splitInvoke(method, splitArgs[1], level, futures);
		splitInvoke(method, splitArgs[0], level, futures);
	}

	private void singleInvoke(final Method method, final Object[] arguments,
			Collection<Future<Object>> futures) throws Throwable {

		Future<Object> result = executor.submit(new Callable<Object>() {
			public Object call() throws Exception {
				return method.invoke(impl, arguments);
			}
		});

		futures.add(result);
	}
}
