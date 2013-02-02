package com.googlecode.httpfilter.proxy.rabbit.io;

/**
 * A listener for waiting on web connections.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface WebConnectionListener {

	/**
	 * A connection has been made.
	 * 
	 * @param wc
	 *            the now open connection
	 */
	void connectionEstablished(WebConnection wc);

	/**
	 * Creating the connection timed out.
	 */
	void timeout();

	/**
	 * Creating the connection failed.
	 * 
	 * @param e
	 *            the cause of the failure
	 */
	void failed(Exception e);
}
