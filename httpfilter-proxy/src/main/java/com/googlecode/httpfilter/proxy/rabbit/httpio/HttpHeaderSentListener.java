package com.googlecode.httpfilter.proxy.rabbit.httpio;

/**
 * A listener for notification that a http header has been sent.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface HttpHeaderSentListener extends AsyncListener {
	/** The http header has been sent. */
	void httpHeaderSent();
}
