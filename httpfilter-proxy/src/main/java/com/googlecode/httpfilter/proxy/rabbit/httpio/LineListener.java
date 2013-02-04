package com.googlecode.httpfilter.proxy.rabbit.httpio;

/**
 * An event handler for lines of text.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface LineListener {
	/**
	 * Event sent when a line of text have been read.
	 * 
	 * @param line
	 *            the line of text that have been read.
	 */
	void lineRead(String line);
}
