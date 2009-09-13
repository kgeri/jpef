package org.jpef.splitter;
import java.util.List;

import org.jpef.parallel.Splitter;

/**
 * A {@link Splitter} implementation for splitting generic lists.
 * 
 * @author Gergely Kiss
 */
public class ListSplitter implements Splitter<List<?>> {

	public List<?>[] split(List<?> input) {
		int len = input.size();
		int center = len / 2;

		if (center == 0) {
			return new List<?>[] { input, null };
		}

		return new List<?>[] { input.subList(0, center), input.subList(center, len) };
	}
}
