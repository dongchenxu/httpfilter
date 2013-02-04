package com.googlecode.httpfilter.proxy.rabbit.zip;

/**
 * A listener for gzip handling events.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface GZipListener {
	/**
	 * Get the data buffer the listener wants the handled data in.
	 * 
	 * @return the data buffer
	 */
	byte[] getBuffer();

	/**
	 * Signal that an exception has occurred during handling of data.
	 * 
	 * @param e
	 *            the real error
	 */
	void failed(Exception e);
}
