package com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl;

import java.util.concurrent.ThreadFactory;

/**
 * A very simple thread factory that only creates new threads.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SimpleThreadFactory implements ThreadFactory {
	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r);
	}
}