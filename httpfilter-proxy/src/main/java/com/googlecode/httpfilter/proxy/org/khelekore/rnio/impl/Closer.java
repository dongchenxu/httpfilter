package com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A helper class that can close resources without throwing exceptions.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Closer {

	/**
	 * Try to close the Closeable. If an exception is thrown when calling
	 * close() it will be logged to the logger.
	 * 
	 * @param c
	 *            the object to close
	 * @param logger
	 *            the Logger to use if the close fails
	 */
	public static void close(Closeable c, Logger logger) {
		if (c == null)
			return;
		try {
			c.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to close connection: " + c, e);
		}
	}
}
