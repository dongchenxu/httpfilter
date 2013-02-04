package com.googlecode.httpfilter.proxy.rabbit.httpio;

/**
 * Exception signaling that a http header could not be constructed.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class BadHttpHeaderException extends RuntimeException {
	/**
	 * Serial version.
	 */
	public static final long serialVersionUID = 1L;

	/**
	 * Create a new BadHttpHeaderException.
	 * 
	 * @param msg
	 *            a descriptive error message
	 */
	public BadHttpHeaderException(String msg) {
		super(msg);
	}
}
