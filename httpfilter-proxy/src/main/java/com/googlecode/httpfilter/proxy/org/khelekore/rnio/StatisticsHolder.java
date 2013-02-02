package com.googlecode.httpfilter.proxy.org.khelekore.rnio;

import java.util.List;
import java.util.Map;

import com.googlecode.httpfilter.proxy.org.khelekore.rnio.statistics.CompletionEntry;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.statistics.TotalTimeSpent;

/**
 * A holder of statistics for tasks.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface StatisticsHolder {

	/**
	 * A new task is put in the queue, waiting to be handled.
	 * 
	 * @param ti
	 *            the identifier of the new task.
	 */
	void addPendingTask(TaskIdentifier ti);

	/**
	 * A pending task is about to be run.
	 * 
	 * @param ti
	 *            the identifier of the task that will start to run.
	 */
	void changeTaskStatusToRunning(TaskIdentifier ti);

	/**
	 * A task has been completed.
	 * 
	 * @param ti
	 *            the identifier of the task that has completed.
	 * @param wasOk
	 *            true if the task completed without errors, false otherwise.
	 * @param timeSpent
	 *            wall clock time spent on the task.
	 */
	void changeTaskStatusToFinished(TaskIdentifier ti, boolean wasOk,
			long timeSpent);

	/**
	 * Get information about the currently pending tasks.
	 * 
	 * @return a mapping from group ids to the task identifiers
	 */
	Map<String, List<TaskIdentifier>> getPendingTasks();

	/**
	 * Get information about the currently running tasks.
	 * 
	 * @return a mapping from group ids to the task identifiers
	 */
	Map<String, List<TaskIdentifier>> getRunningTasks();

	/**
	 * Get information about the most recent completed tasks
	 * 
	 * @return a mapping from group ids to the task identifiers
	 */
	Map<String, List<CompletionEntry>> getLatest();

	/**
	 * Get information about the longest running task.
	 * 
	 * @return a mapping from group ids to the task identifiers
	 */
	Map<String, List<CompletionEntry>> getLongest();

	/**
	 * Get the total time spent for each task.
	 * 
	 * @return a mapping from group ids to the task identifiers
	 */
	Map<String, TotalTimeSpent> getTotalTimeSpent();
}
