package com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.httpfilter.proxy.org.khelekore.rnio.AcceptHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.ConnectHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.ReadHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.SelectorVisitor;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.SocketChannelHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.StatisticsHolder;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.TaskIdentifier;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.WriteHandler;

/**
 * An implementation of NioHandler that runs several selector threads.
 * 
 * <p>
 * Any tasks that should run on a background thread are passed to the
 * {@link ExecutorService} that was given in the constructor.
 * 
 * <p>
 * This class will log using the "org.khelekore.rnio" {@link Logger}.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class MultiSelectorNioHandler implements NioHandler {
	/** The executor service. */
	private final ExecutorService executorService;
	private final List<SingleSelectorRunner> selectorRunners;
	private final Logger logger = Logger.getLogger("org.khelekore.rnio");
	private final StatisticsHolder stats;
	private final Long defaultTimeout;
	private int nextIndex = 0;

	/**
	 * Create a new MultiSelectorNioHandler that runs background tasks on the
	 * given executor and has a specified number of selectors.
	 * 
	 * @param executorService
	 *            the ExecutorService to use for this NioHandler
	 * @param stats
	 *            the StatisticsHolder to use for this NioHandler
	 * @param numSelectors
	 *            the number of threads that this NioHandler will use
	 * @param defaultTimeout
	 *            the default timeout value for this NioHandler
	 * @throws IOException
	 *             if the selectors can not be started
	 */
	public MultiSelectorNioHandler(ExecutorService executorService,
			StatisticsHolder stats, int numSelectors, Long defaultTimeout)
			throws IOException {
		this.executorService = executorService;
		this.stats = stats;

		if (numSelectors < 1) {
			String err = "Must have at least one selector: " + numSelectors;
			throw new IllegalArgumentException(err);
		}
		selectorRunners = new ArrayList<SingleSelectorRunner>(numSelectors);
		for (int i = 0; i < numSelectors; i++)
			selectorRunners.add(new SingleSelectorRunner(executorService));
		if (defaultTimeout != null && defaultTimeout.longValue() <= 0) {
			String err = "Default timeout may not be zero or negative";
			throw new IllegalArgumentException(err);
		}
		this.defaultTimeout = defaultTimeout;
	}

	public void start(ThreadFactory tf) {
		for (SingleSelectorRunner ssr : selectorRunners)
			ssr.start(tf);
	}

	public void shutdown() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				executorService.shutdown();
				for (SingleSelectorRunner ssr : selectorRunners)
					ssr.shutdown();
			}
		});
		t.start();
	}

	public Long getDefaultTimeout() {
		if (defaultTimeout == null)
			return null;
		return Long.valueOf(System.currentTimeMillis()
				+ defaultTimeout.longValue());
	}

	public void runThreadTask(Runnable r, TaskIdentifier ti) {
		stats.addPendingTask(ti);
		executorService.execute(new StatisticsCollector(stats, r, ti));
	}

	private SingleSelectorRunner getSelectorRunner() {
		int index;
		synchronized (this) {
			index = nextIndex++;
			nextIndex %= selectorRunners.size();
		}
		return selectorRunners.get(index);
	}

	/**
	 * Run a task on one of the selector threads. The task will be run sometime
	 * in the future.
	 * 
	 * @param channel
	 *            the channel to run the task on
	 * @param sr
	 *            the task to run on the main thread.
	 */
	private void runSelectorTask(SelectableChannel channel, SelectorRunnable sr) {
		// If the channel is already being served by someone, favor that one.
		for (SingleSelectorRunner ssr : selectorRunners) {
			if (ssr.handlesChannel(channel)) {
				ssr.runSelectorTask(sr);
				return;
			}
		}
		// Put it on any selector
		SingleSelectorRunner ssr = getSelectorRunner();
		ssr.runSelectorTask(sr);
	}

	public void waitForRead(final SelectableChannel channel,
			final ReadHandler handler) {
		if (logger.isLoggable(Level.FINEST))
			logger.fine("Waiting for read for: channel: " + channel
					+ ", handler: " + handler);
		runSelectorTask(channel, new SelectorRunnable() {
			public void run(SingleSelectorRunner ssr) throws IOException {
				ssr.waitForRead(channel, handler);
			}
		});
	}

	public void waitForWrite(final SelectableChannel channel,
			final WriteHandler handler) {
		if (logger.isLoggable(Level.FINEST))
			logger.fine("Waiting for write for: channel: " + channel
					+ ", handler: " + handler);
		runSelectorTask(channel, new SelectorRunnable() {
			public void run(SingleSelectorRunner ssr) throws IOException {
				ssr.waitForWrite(channel, handler);
			}
		});
	}

	public void waitForAccept(final SelectableChannel channel,
			final AcceptHandler handler) {
		if (logger.isLoggable(Level.FINEST))
			logger.fine("Waiting for accept for: channel: " + channel
					+ ", handler: " + handler);
		runSelectorTask(channel, new SelectorRunnable() {
			public void run(SingleSelectorRunner ssr) throws IOException {
				ssr.waitForAccept(channel, handler);
			}
		});
	}

	public void waitForConnect(final SelectableChannel channel,
			final ConnectHandler handler) {
		runSelectorTask(channel, new SelectorRunnable() {
			public void run(SingleSelectorRunner ssr) throws IOException {
				ssr.waitForConnect(channel, handler);
			}
		});
	}

	public void cancel(final SelectableChannel channel,
			final SocketChannelHandler handler) {
		for (SingleSelectorRunner sr : selectorRunners) {
			sr.runSelectorTask(new SelectorRunnable() {
				public void run(SingleSelectorRunner ssr) {
					ssr.cancel(channel, handler);
				}
			});
		}
	}

	public void close(final SelectableChannel channel) {
		for (SingleSelectorRunner sr : selectorRunners) {
			sr.runSelectorTask(new SelectorRunnable() {
				public void run(SingleSelectorRunner ssr) {
					ssr.close(channel);
				}
			});
		}
	}

	public void visitSelectors(SelectorVisitor visitor) {
		// TODO: do we need to run on the respective threads?
		for (SingleSelectorRunner sr : selectorRunners)
			sr.visit(visitor);
		visitor.end();
	}

	// TODO: where does this belong?
	public StatisticsHolder getTimingStatistics() {
		return stats;
	}
}
