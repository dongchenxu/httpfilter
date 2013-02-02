package com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl;

import com.googlecode.httpfilter.proxy.org.khelekore.rnio.StatisticsHolder;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.TaskIdentifier;

/**
 * A class that executes one task and gathers information about the time spent
 * and the success status of the task.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class StatisticsCollector implements Runnable {
	private final StatisticsHolder stats;
	private final Runnable realTask;
	private final TaskIdentifier ti;

	/**
	 * Create a new StatisticsCollector that will update the given
	 * StatisticsHolder about the specific job.
	 * 
	 * @param stats
	 *            the StatisticsHolder to update
	 * @param realTask
	 *            the task to run
	 * @param ti
	 *            the identifier of the task
	 */
	public StatisticsCollector(StatisticsHolder stats, Runnable realTask,
			TaskIdentifier ti) {
		this.stats = stats;
		this.realTask = realTask;
		this.ti = ti;
	}

	/**
	 * Run the task.
	 */
	public void run() {
		stats.changeTaskStatusToRunning(ti);
		long started = System.currentTimeMillis();
		boolean wasOk = false;
		try {
			realTask.run();
			wasOk = true;
		} finally {
			long ended = System.currentTimeMillis();
			long diff = ended - started;
			stats.changeTaskStatusToFinished(ti, wasOk, diff);
		}
	}
}
