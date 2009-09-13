package org.jpef.parallel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import org.jpef.common.TypeMap;
import org.jpef.example.IntSumJoiner;
import org.jpef.joiner.ArrayJoiner;
import org.jpef.splitter.ArraySplitter;
import org.jpef.splitter.ListSplitter;

/**
 * Parallel execution helper class.
 * 
 * @author Gergely Kiss
 */
public abstract class Parallel {
	private static TypeMap<Splitter<?>> splitterManager;
	private static TypeMap<Joiner<?>> joinerManager;
	private static Splitter<?> arraySplitter = new ArraySplitter();
	private static Joiner<?> arrayJoiner = new ArrayJoiner();

	static {
		// TODO IOC
		splitterManager = new TypeMap<Splitter<?>>();
		joinerManager = new TypeMap<Joiner<?>>();

		splitterManager.put(List.class, new ListSplitter());
		joinerManager.put(int.class, new IntSumJoiner());
	}

	/**
	 * Returns a parallel proxy for the given bean and interface.
	 * 
	 * @param <T>
	 * @param implementation
	 * @param iface
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T proxy(Object implementation, Class<T> iface) {
		// TODO proxy caching
		InvocationHandler ih = new ParallelInvocationHandler(implementation);

		return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] { iface }, ih);
	}

	// TODO parallel invoke

	static Joiner<?> getJoiner(Class<?> type) {
		if (type.isArray()) {
			return arrayJoiner;
		}

		Joiner<?> joiner = joinerManager.get(type);

		if (joiner != null) {
			return joiner;
		}

		throw new IllegalArgumentException("No Joiner was found for return type: " + type);
	}

	static Splitter<?> getSplitter(Class<?> type) {
		if (type.isArray()) {
			return arraySplitter;
		} else {
			return splitterManager.get(type);
		}
	}
}
