package org.jpef.parallel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An invocation handler for managing parallel execution of a single method
 * call.
 * 
 * @author Gergely Kiss
 */
@SuppressWarnings("unchecked")
class ParallelInvocationHandler implements InvocationHandler {
	private static final Logger log = LoggerFactory.getLogger(ParallelInvocationHandler.class);

	/** The original implementation. */
	private final Object impl;

	/** The context parameters of the parallel execution. */
	private final ParallelContext ctx;

	public ParallelInvocationHandler(Object imp, ParallelContext ctx) {
		this.impl = imp;
		this.ctx = ctx;
	}

	public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
		if (arguments == null || arguments.length == 0) {
			throw new IllegalArgumentException("Method '" + method.toGenericString()
					+ "' does not have any arguments."
					+ " Only methods with arguments may be parallelized.");
		}

		method.setAccessible(true);

		List<Future<Object>> futures = new ArrayList<Future<Object>>(ctx.getMaxExecutors());

		// Splitting
		splitInvoke(method, arguments, 0, futures);

		// Joining
		return joinInvoke(method, futures);
	}

	/*
	 * Splits the metod invocation recursively.
	 */
	private void splitInvoke(Method method, Object[] arguments, int level,
			Collection<Future<Object>> futures) throws Throwable {

		// We've reached the maximum number of executors, performing execution
		if (1 << level >= ctx.getMaxExecutors()) {
			singleInvoke(method, arguments, futures);
			return;
		}

		// Splitting arguments
		Object[][] splitArgs = new Object[2][arguments.length];
		boolean noNonNullArgs = true;
		boolean noSplitter = true;
		boolean notSplit = true;

		for (int i = 0; i < arguments.length; i++) {
			Object argument = arguments[i];

			// No argument, skipping
			if (argument == null) {
				continue;
			}

			noNonNullArgs = false;
			Splitter splitter = ParallelManager.getSplitter(argument.getClass());

			if (splitter == null) {
				// No splitter was found, duplicating argument
				splitArgs[0][i] = argument;
				splitArgs[1][i] = argument;
			} else {
				Object[] splitArg = splitter.split(argument);

				if (splitArg == null) {
					// Nothing to split, duplicating argument
					splitArgs[0][i] = argument;
					splitArgs[1][i] = argument;
					noSplitter = false;
				} else {
					// Splitting argument
					splitArgs[0][i] = splitArg[0];
					splitArgs[1][i] = splitArg[1];
					noSplitter = false;
					notSplit = false;
				}
			}
		}

		if (noNonNullArgs) {
			// No argument was split, because everything was null
			log.debug("Arguments were null, can't split anything: {}", method.toGenericString());

			singleInvoke(method, arguments, futures);
			return;
		} else if (noSplitter) {
			// No argument was split, this is most possibly a misconfiguration
			log.warn("No splitters were found for any of the arguments for the method: {}", method
					.toGenericString());

			singleInvoke(method, arguments, futures);
			return;
		} else if (notSplit) {
			// No argument was split because of a degenerate case
			log.debug("No argument was split, degenerate case found for method: {}", method
					.toGenericString());

			singleInvoke(method, arguments, futures);
			return;
		} else {
			// Splitting happened
			level++;

			splitInvoke(method, splitArgs[1], level, futures);
			splitInvoke(method, splitArgs[0], level, futures);
		}
	}

	/*
	 * Instructs a single method invocation with the executor.
	 */
	private void singleInvoke(final Method method, final Object[] arguments,
			Collection<Future<Object>> futures) throws Throwable {

		Future<Object> result = ctx.getExecutor().submit(new Callable<Object>() {
			public Object call() throws Exception {
				return method.invoke(impl, arguments);
			}
		});

		futures.add(result);
	}

	/*
	 * Joins the futures into one single result.
	 */
	private Object joinInvoke(Method method, Collection<Future<Object>> futures)
			throws ExecutionException, InterruptedException {
		Object[] results = new Object[futures.size()];
		int cnt = 0;

		for (Future<Object> future : futures) {
			results[cnt++] = future.get();
		}

		// Shortcut: exactly one result, no joining needed
		if (results.length == 1) {
			return results[0];
		}

		// Searching for joiner
		Joiner joiner = ParallelManager.getJoiner(method.getReturnType());
		if (joiner == null) {
			throw new UnsupportedOperationException(
					"Parallel execution failed: No Joiner was found for type "
							+ method.getReturnType());
		}

		// Binary tree collapse
		int len = results.length;
		while (len > 1) {
			for (int i = 0; i * 2 < len; i++) {
				if (i * 2 + 1 < len) {
					// Collapsing and setting neighbour results
					results[i] = joiner.join(results[i * 2], results[i * 2 + 1]);
				} else {
					// Setting tail
					// Note: effectively never happens because futures.size is
					// always some 2^x value, code is left here for robustness
					results[i] = results[i * 2 + 1];
				}
			}

			// Length will be exactly: ceil(len / 2.0)
			len = (len + 1) / 2;
		}

		return results[0];
	}
}
