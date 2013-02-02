package com.googlecode.httpfilter.proxy.org.khelekore.rnio;

/**
 * A handler that signals that a channel is ready to connect.
 */
public interface ConnectHandler extends SocketChannelHandler {

	/** The channel is ready for read. */
	void connect();
}