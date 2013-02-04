package com.googlecode.httpfilter.proxy.rabbit.io;

import java.nio.ByteBuffer;

/**
 * A handle to a ByteBuffer
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface BufferHandle {
	/**
	 * Check if this handle is empty, that is if no buffer exists or the buffer
	 * is empty.
	 * 
	 * @return true if the buffer currently is empty
	 */
	boolean isEmpty();

	/**
	 * Get a byte buffer of reasonable size, the buffer will have been cleared.
	 * 
	 * @return the actual ByteBuffer
	 */
	ByteBuffer getBuffer();

	/**
	 * Get a byte buffer of reasonable size, the buffer will have been cleared.
	 * 
	 * @return the large buffer to use
	 */
	ByteBuffer getLargeBuffer();

	/**
	 * Check if the given buffer is a large buffer
	 * 
	 * @param buffer
	 *            the ByteBuffer to check
	 * @return true if the given buffer is large
	 */
	boolean isLarge(ByteBuffer buffer);

	/** release a buffer if possible. */
	void possiblyFlush();

	/**
	 * Flag that the internal ByteBuffer may not be flushed.
	 * 
	 * @param mayBeFlushed
	 *            if true the buffer may be returned, if false the putBuffer
	 *            call will throw an exception
	 */
	void setMayBeFlushed(boolean mayBeFlushed);
}
