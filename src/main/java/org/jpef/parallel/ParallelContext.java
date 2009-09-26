package org.jpef.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A context for parallel execution parameters.
 * 
 * @author Gergely Kiss
 */
public class ParallelContext {
	/** The thread pool for parallel execution. */
	private ExecutorService executor = Executors.newFixedThreadPool(4);

	/** Maximum number of parallel executors. */
	private int maxExecutors = 4;

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public int getMaxExecutors() {
		return maxExecutors;
	}

	public void setMaxExecutors(int maxExecutors) {
		this.maxExecutors = maxExecutors;
	}
}
