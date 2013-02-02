package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.io.IOException;

/**
 * A class to handle the case where http header lines are too long.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class RequestLineTooLongException extends IOException {
	private static final long serialVersionUID = 1;

	/**
	 * Create a new RequestLineTooLongException
	 */
	public RequestLineTooLongException() {
		super("Request line too long");
	}
}
