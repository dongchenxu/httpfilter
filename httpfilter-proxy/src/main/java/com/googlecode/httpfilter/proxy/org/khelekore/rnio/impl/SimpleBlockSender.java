package com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.WriteHandler;

/**
 * A simple sender of data. Will try to send all data with no timeout.
 * 
 * <p>
 * Subclass this sender and implement <code>done()</code> to do any work after
 * the data has been sent.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public abstract class SimpleBlockSender extends
		SocketHandlerBase<SocketChannel> implements WriteHandler {
	private final ByteBuffer buf;
	private final Logger logger = Logger.getLogger("org.khelekore.rnio");

	/**
	 * @param sc
	 *            the channel to handle
	 * @param nioHandler
	 *            the NioHandler
	 * @param buf
	 *            the ByteBuffer to send
	 * @param timeout
	 *            the timeout in millis, may be null if no timeout is wanted.
	 */
	public SimpleBlockSender(SocketChannel sc, NioHandler nioHandler,
			ByteBuffer buf, Long timeout) {
		super(sc, nioHandler, timeout);
		this.buf = buf;
	}

	/**
	 * Get the buffer we are sending data from.
	 * 
	 * @return the ByteBuffer with the data that is being sent
	 */
	public ByteBuffer getBuffer() {
		return buf;
	}

	public void write() {
		try {
			int written = 0;
			do {
				written = sc.write(buf);
			} while (buf.hasRemaining() && written > 0);
			if (buf.hasRemaining())
				register();
			else
				done();
		} catch (IOException e) {
			handleIOException(e);
		}
	}

	/**
	 * Handle the exception, default is to log it and to close the channel.
	 * 
	 * @param e
	 *            the IOException that is the cause of data write failure
	 */
	public void handleIOException(IOException e) {
		logger.log(Level.WARNING, "Failed to send data", e);
		Closer.close(sc, logger);
	}

	/**
	 * The default is to do nothing, override in subclasses if needed.
	 */
	public void done() {
		// empty
	}

	/**
	 * Register writeWait on the nioHandler
	 */
	public void register() {
		nioHandler.waitForWrite(sc, this);
	}
}
