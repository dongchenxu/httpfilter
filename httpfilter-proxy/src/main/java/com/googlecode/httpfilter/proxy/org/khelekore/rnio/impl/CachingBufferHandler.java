package com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.googlecode.httpfilter.proxy.org.khelekore.rnio.BufferHandler;

/**
 * A buffer handler that re-uses returned buffers.
 * 
 * <p>
 * This class uses no synchronization.
 * 
 * <p>
 * This class only allocates direct buffers.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class CachingBufferHandler implements BufferHandler {
	private Queue<BufferHolder> cache = new ConcurrentLinkedQueue<BufferHolder>();
	private Queue<BufferHolder> largeCache = new ConcurrentLinkedQueue<BufferHolder>();

	private ByteBuffer getBuffer(Queue<BufferHolder> bufs, int size) {
		BufferHolder r = bufs.poll();
		ByteBuffer b = null;
		if (r != null)
			b = r.getBuffer();
		else
			b = ByteBuffer.allocateDirect(size);
		b.clear();
		return b;
	}

	public ByteBuffer getBuffer() {
		return getBuffer(cache, 4096);
	}

	private void addCache(Queue<BufferHolder> bufs, BufferHolder bh) {
		bufs.add(bh);
	}

	public void putBuffer(ByteBuffer buffer) {
		if (buffer == null)
			throw new IllegalArgumentException("null buffer not allowed");
		BufferHolder bh = new BufferHolder(buffer);
		if (buffer.capacity() == 4096)
			addCache(cache, bh);
		else
			addCache(largeCache, bh);
	}

	public ByteBuffer growBuffer(ByteBuffer buffer) {
		ByteBuffer lb = getBuffer(largeCache, 128 * 1024);
		if (buffer != null) {
			lb.put(buffer);
			putBuffer(buffer);
		}
		return lb;
	}

	private static final class BufferHolder {
		private ByteBuffer buffer;

		public BufferHolder(ByteBuffer buffer) {
			this.buffer = buffer;
		}

		// Two holders are equal if they hold the same buffer
		@Override
		public boolean equals(Object o) {
			if (o == null)
				return false;
			if (o == this)
				return true;

			// ByteBuffer.equals depends on content, not what I want.
			if (o instanceof BufferHolder)
				return ((BufferHolder) o).buffer == buffer;
			return false;
		}

		@Override
		public int hashCode() {
			// ByteBuffer.hashCode depends on its contents.
			return System.identityHashCode(buffer);
		}

		public ByteBuffer getBuffer() {
			return buffer;
		}
	}
}
