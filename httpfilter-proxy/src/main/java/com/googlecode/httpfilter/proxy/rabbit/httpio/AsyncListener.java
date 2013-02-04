package com.googlecode.httpfilter.proxy.rabbit.httpio;

/**
 * A listener for asynchronous events.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface AsyncListener {
	/**
	 * Reading failed
	 * 
	 * @param cause
	 *            the real reason the operation failed.
	 */
	void failed(Exception cause);

	/**
	 * The operation timed out
	 */
	void timeout();
}
