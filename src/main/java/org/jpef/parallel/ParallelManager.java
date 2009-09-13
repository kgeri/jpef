package org.jpef.parallel;

import java.util.List;

import org.jpef.base.ArrayJoiner;
import org.jpef.base.ArraySplitter;
import org.jpef.base.ListJoiner;
import org.jpef.base.ListSplitter;
import org.jpef.common.TypeMap;

/**
 * Parallel {@link Splitter} and {@link Joiner} manager.
 * 
 * <p>
 * Manages the {@link Splitter} and {@link Joiner} instances which may be used
 * for method argument assembly and disassembly for parallel execution.
 * </p>
 * 
 * @author Gergely Kiss
 * 
 * @see Splitter
 * @see Joiner
 */
@SuppressWarnings("unchecked")
public abstract class ParallelManager {
	private static TypeMap<Splitter<?>> splitterManager = new TypeMap<Splitter<?>>();
	private static TypeMap<Joiner<?>> joinerManager = new TypeMap<Joiner<?>>();
	private static Splitter arraySplitter = new ArraySplitter();
	private static Joiner arrayJoiner = new ArrayJoiner();

	static {
		// Registering default instances
		registerSplitter(List.class, new ListSplitter());
		registerJoiner(List.class, new ListJoiner());
	}

	/**
	 * Registers a {@link Joiner} instance for the specified type.
	 * 
	 * @param <T>
	 * @param type
	 * @param joiner
	 */
	public static <T> void registerJoiner(Class<T> type, Joiner<T> joiner) {
		joinerManager.put(type, joiner);
	}

	/**
	 * Registers a {@link Splitter} instance for the specified type.
	 * 
	 * @param <T>
	 *            The type to split
	 * @param type
	 * @param splitter
	 */
	public static <T> void registerSplitter(Class<T> type, Splitter<T> splitter) {
		splitterManager.put(type, splitter);
	}

	/**
	 * Gets a {@link Joiner} instance for the specified type.
	 * 
	 * <p>
	 * Joiners are searched in the following order:
	 * <ol>
	 * <li>Querying the joiner registry. Registration is possible via the
	 * <code>registerJoiner</code> method
	 * <li>Trying to instantiate <code>type.getName() + "Joiner"</code> (on
	 * success, the instance is registered)
	 * <li>If <code>type</code> is an array, the default array joiner is
	 * returned
	 * </ol>
	 * </p>
	 * 
	 * @param <T>
	 * @param type
	 * 
	 * @return The {@link Joiner} implementation, or <code>null</code> if none
	 *         is available
	 */
	public static <T> Joiner<T> getJoiner(Class<T> type) {
		// 1. Trying registry
		Joiner joiner = joinerManager.get(type);

		if (joiner != null) {
			return joiner;
		}

		// 2. Try adding 'Joiner' to the class name
		try {
			String cname = type.getName() + "Joiner";
			joiner = (Joiner) Class.forName(cname).newInstance();

			// Found joiner, registering
			registerJoiner(type, joiner);
			return joiner;
		} catch (Exception ex) {
			// Silently ignore any errors.
		}

		// 3. Trying generic array joiner
		if (type.isArray()) {
			return arrayJoiner;
		}

		// Joiner not found
		return null;
	}

	/**
	 * Gets a {@link Splitter} instance for the specified type.
	 * 
	 * <p>
	 * Splitters are searched in the following order:
	 * <ol>
	 * <li>Querying the splitter registry. Registration is possible via the
	 * <code>registerSplitter</code> method
	 * <li>Trying to instantiate <code>type.getName() + "Splitter"</code> (on
	 * success, the instance is registered)
	 * <li>If <code>type</code> is an array, the default array splitter is
	 * returned
	 * </ol>
	 * </p>
	 * 
	 * @param <T>
	 *            The type to split
	 * @param type
	 * 
	 * @return The {@link Splitter} implementation, or <code>null</code> if none
	 *         is available
	 */
	public static <T> Splitter<T> getSplitter(Class<T> type) {
		// 1. Trying registry
		Splitter splitter = splitterManager.get(type);

		if (splitter != null) {
			return splitter;
		}

		// 2. Try adding 'Splitter' to the class name
		try {
			String cname = type.getName() + "Splitter";
			splitter = (Splitter) Class.forName(cname).newInstance();

			// Found splitter, registering
			registerSplitter(type, splitter);
			return splitter;
		} catch (Exception ex) {
			// Silently ignore any errors.
		}

		// 3. Trying generic array splitter
		if (type.isArray()) {
			return arraySplitter;
		}

		// Splitter not found
		return null;
	}
}
