package com.googlecode.httpfilter.proxy.rabbit.io;

import com.googlecode.httpfilter.proxy.org.khelekore.rnio.BufferHandler;
import java.nio.ByteBuffer;

/**
 * A handle to a ByteBuffer that uses a buffer handler
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class CacheBufferHandle implements BufferHandle {
	private final BufferHandler bh;
	private ByteBuffer buffer;
	private boolean mayBeFlushed = true;

	/**
	 * Create a new CacheBufferHandle that uses the given BufferHandler for the
	 * caching of the ByteBuffer:s
	 * 
	 * @param bh
	 *            the BufferHandler that is the actual cache
	 */
	public CacheBufferHandle(BufferHandler bh) {
		this.bh = bh;
	}

	public synchronized boolean isEmpty() {
		return buffer == null || !buffer.hasRemaining();
	}

	public synchronized ByteBuffer getBuffer() {
		if (buffer == null)
			buffer = bh.getBuffer();
		return buffer;
	}

	public synchronized ByteBuffer getLargeBuffer() {
		if (buffer != null && isLarge(buffer))
			return buffer;
		buffer = bh.growBuffer(buffer);
		return buffer;
	}

	public boolean isLarge(ByteBuffer buffer) {
		// return bh.isLarge (buffer);
		return false;
	}

	public synchronized void possiblyFlush() {
		if (!mayBeFlushed)
			throw new IllegalStateException("buffer may not be flushed!: "
					+ System.identityHashCode(buffer));
		if (buffer == null)
			return;
		if (!buffer.hasRemaining()) {
			bh.putBuffer(buffer);
			buffer = null;
		}
	}

	public synchronized void setMayBeFlushed(boolean mayBeFlushed) {
		this.mayBeFlushed = mayBeFlushed;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[buffer: " + buffer + ", bh: " + bh
				+ "}";
	}
}
