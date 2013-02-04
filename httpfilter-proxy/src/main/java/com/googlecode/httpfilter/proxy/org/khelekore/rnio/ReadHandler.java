package com.googlecode.httpfilter.proxy.org.khelekore.rnio;

/**
 * A handler that signals that data is ready to be read.
 */
public interface ReadHandler extends SocketChannelHandler {

	/** The channel is ready for read. */
	void read();
}