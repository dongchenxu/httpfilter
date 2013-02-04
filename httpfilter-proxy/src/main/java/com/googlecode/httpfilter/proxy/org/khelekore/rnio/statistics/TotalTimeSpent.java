package com.googlecode.httpfilter.proxy.org.khelekore.rnio.statistics;

/**
 * Information about total time spent on a group of tasks.
 * <p>
 * This class is not thread safe.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class TotalTimeSpent {

	private long successful = 0;
	private long failures = 0;
	private long totalMillis = 0;

	/**
	 * Update this information with data from the newly completed task.
	 * 
	 * @param ce
	 *            the CompletionEntry that we want to update our information
	 *            with
	 */
	public void update(CompletionEntry ce) {
		if (ce.wasOk)
			successful++;
		else
			failures++;
		totalMillis += ce.timeSpent;
	}

	/**
	 * Get the number of successfully completed jobs.
	 * 
	 * @return the number of successful jobs
	 */
	public long getSuccessful() {
		return successful;
	}

	/**
	 * Get the number of failed jobs.
	 * 
	 * @return the number of unsuccessful jobs
	 */
	public long getFailures() {
		return failures;
	}

	/**
	 * Get the total time spent doing this kind of task.
	 * 
	 * @return the total time take for all jobs
	 */
	public long getTotalMillis() {
		return totalMillis;
	}
}
