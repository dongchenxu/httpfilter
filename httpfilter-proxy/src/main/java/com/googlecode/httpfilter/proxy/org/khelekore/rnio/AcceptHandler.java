package com.googlecode.httpfilter.proxy.org.khelekore.rnio;

/**
 * A handler that accepts connections
 */
public interface AcceptHandler extends SocketChannelHandler {

	/** The channel is ready for read. */
	void accept();
}