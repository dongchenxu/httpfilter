package com.googlecode.httpfilter.proxy.rabbit.proxy;

/**
 * A logger interface.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface ConnectionLogger {
	/**
	 * Log a finished connection.
	 * 
	 * @param con
	 *            the Connection that has finished one operation.
	 */
	void logConnection(Connection con);
}
