package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.BufferHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.TaskIdentifier;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.Closer;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.DefaultTaskIdentifier;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.CacheBufferHandle;

/**
 * A resource that comes from a file.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class FileResourceSource implements ResourceSource {
	protected FileChannel fc;

	// used for block handling.
	private BlockListener listener;
	private NioHandler nioHandler;
	protected BufferHandle bufHandle;

	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Create a new FileResourceSource using the given filename
	 * 
	 * @param filename
	 *            the file for this resource
	 * @param nioHandler
	 *            the NioHandler to use for background tasks
	 * @param bufHandler
	 *            the BufferHandler to use when reading and writing
	 * @throws IOException
	 *             if the file is a valid file
	 */
	public FileResourceSource(String filename, NioHandler nioHandler,
			BufferHandler bufHandler) throws IOException {
		this(new File(filename), nioHandler, bufHandler);
	}

	/**
	 * Create a new FileResourceSource using the given filename
	 * 
	 * @param f
	 *            the resource
	 * @param nioHandler
	 *            the NioHandler to use for background tasks
	 * @param bufHandler
	 *            the BufferHandler to use when reading and writing
	 * @throws IOException
	 *             if the file is a valid file
	 */
	public FileResourceSource(File f, NioHandler nioHandler,
			BufferHandler bufHandler) throws IOException {
		if (!f.exists())
			throw new FileNotFoundException("File: " + f.getName()
					+ " not found");
		if (!f.isFile())
			throw new FileNotFoundException("File: " + f.getName()
					+ " is not a regular file");
		FileInputStream fis = new FileInputStream(f);
		fc = fis.getChannel();
		this.nioHandler = nioHandler;
		this.bufHandle = new CacheBufferHandle(bufHandler);
	}

	/**
	 * FileChannels can be used, will always return true.
	 * 
	 * @return true
	 */
	public boolean supportsTransfer() {
		return true;
	}

	public long length() {
		try {
			return fc.size();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public long transferTo(long position, long count, WritableByteChannel target)
			throws IOException {
		try {
			return fc.transferTo(position, count, target);
		} catch (IOException e) {
			if ("Resource temporarily unavailable".equals(e.getMessage())) {
				// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5103988
				// transferTo on linux throws IOException on full buffer.
				return 0;
			}
			throw e;
		}
	}

	/**
	 * Generally we do not come into this method, but it can happen..
	 */
	public void addBlockListener(BlockListener listener) {
		this.listener = listener;
		// Get buffer on selector thread.
		bufHandle.getBuffer();
		TaskIdentifier ti = new DefaultTaskIdentifier(getClass()
				.getSimpleName(), "addBlockListener: channel: " + fc);
		nioHandler.runThreadTask(new ReadBlock(), ti);
	}

	private class ReadBlock implements Runnable {
		public void run() {
			try {
				ByteBuffer buffer = bufHandle.getBuffer();
				int read = fc.read(buffer);
				if (read == -1) {
					returnFinished();
				} else {
					buffer.flip();
					returnBlockRead();
				}
			} catch (IOException e) {
				returnWithFailure(e);
			}
		}
	}

	private void returnWithFailure(final Exception e) {
		bufHandle.possiblyFlush();
		listener.failed(e);
	}

	private void returnFinished() {
		bufHandle.possiblyFlush();
		listener.finishedRead();
	}

	private void returnBlockRead() {
		listener.bufferRead(bufHandle);
	}

	public void release() {
		Closer.close(fc, logger);
		listener = null;
		nioHandler = null;
		bufHandle.possiblyFlush();
		bufHandle = null;
	}
}
