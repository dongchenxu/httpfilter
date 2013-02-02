package com.googlecode.httpfilter.proxy.rabbit.handler;

/**
 * This interface descsribes the methods neccessary to implement a handler.
 * Besides this a constructor is also nedded
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface Handler {
	/**
	 * handle a request.
	 */
	void handle();

	/**
	 * Check if this handler will change the content size.
	 * 
	 * @return true if Content-Lenght may be changed by this handler typically
	 *         used for handlers that may modify the content. Return false if
	 *         this handler will not change the size.
	 */
	boolean changesContentSize();
}
