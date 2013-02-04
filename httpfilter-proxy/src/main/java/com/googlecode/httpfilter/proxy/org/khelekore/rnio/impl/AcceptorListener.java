package com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * A listener for accepted connections.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface AcceptorListener {
	/**
	 * A conneciton has been accepted
	 * 
	 * @param sc
	 *            the new socket channel, will already be set to non blocking
	 *            mode
	 * @throws IOException
	 *             if the accept fails
	 */
	void connectionAccepted(SocketChannel sc) throws IOException;
}
