package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.ReadHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.SocketChannelHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.WriteHandler;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;

/**
 * A base class for socket handlers.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public abstract class BaseSocketHandler implements SocketChannelHandler {
	/** The client channel. */
	private final SocketChannel channel;

	/** The nio handler we are using. */
	private final NioHandler nioHandler;

	/** The logger to use. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	/** The buffer handle. */
	private final BufferHandle bh;

	/** The timeout value set by the previous channel registration */
	private Long timeout;

	/**
	 * Create a new BaseSocketHandler that will handle the traffic on the given
	 * channel
	 * 
	 * @param channel
	 *            the SocketChannel to read to and write from
	 * @param bh
	 *            the BufferHandle to use for the io operation
	 * @param nioHandler
	 *            the NioHandler to use to wait for operations on
	 */
	public BaseSocketHandler(SocketChannel channel, BufferHandle bh,
			NioHandler nioHandler) {
		this.channel = channel;
		this.bh = bh;
		this.nioHandler = nioHandler;
	}

	protected ByteBuffer getBuffer() {
		return bh.getBuffer();
	}

	protected ByteBuffer getLargeBuffer() {
		return bh.getLargeBuffer();
	}

	protected boolean isUsingSmallBuffer(ByteBuffer buffer) {
		return !bh.isLarge(buffer);
	}

	protected void releaseBuffer() {
		bh.possiblyFlush();
	}

	/** Does nothing by default */
	public void closed() {
		// empty
	}

	/** Does nothing by default */
	public void timeout() {
		// empty
	}

	/** Runs on the selector thread by default */
	public boolean useSeparateThread() {
		return false;
	}

	public String getDescription() {
		return getClass().getName() + ":" + channel;
	}

	public Long getTimeout() {
		return timeout;
	}

	protected Logger getLogger() {
		return logger;
	}

	protected void closeDown() {
		releaseBuffer();
		nioHandler.close(channel);
	}

	/**
	 * Get the channel this BaseSocketHandler is using
	 * 
	 * @return the SocketChannel being used
	 */
	public SocketChannel getChannel() {
		return channel;
	}

	/**
	 * Get the BufferHandle this BaseSocketHandler is using
	 * 
	 * @return the BufferHandle used for io operations
	 */
	public BufferHandle getBufferHandle() {
		return bh;
	}

	/**
	 * Wait for more data to be readable on the channel
	 * 
	 * @param rh
	 *            the handler that will be notified when more data is ready to
	 *            be read
	 */
	public void waitForRead(ReadHandler rh) {
		this.timeout = nioHandler.getDefaultTimeout();
		nioHandler.waitForRead(channel, rh);
	}

	/**
	 * Wait for more data to be writable on the channel
	 * 
	 * @param rh
	 *            the handler that will be notified when more data is ready to
	 *            be written
	 */
	public void waitForWrite(WriteHandler rh) {
		this.timeout = nioHandler.getDefaultTimeout();
		nioHandler.waitForWrite(channel, rh);
	}
}
