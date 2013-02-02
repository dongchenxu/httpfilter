package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.http.ContentRangeParser;

/**
 * An updater that writes an updated range to a cache file.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class PartialCacher {
	private ContentRangeParser crp;
	private FileWriter fw;

	/**
	 * Create a new PartialCacher that will update the given file with data from
	 * the given response.
	 * 
	 * @param fileName
	 *            the cache resource to update
	 * @param response
	 *            the response header
	 * @throws IOException
	 *             if updating the cached resource fails
	 */
	public PartialCacher(File fileName, HttpHeader response) throws IOException {
		// Content-Range: 0-4/25\r\n
		String cr = response.getHeader("Content-Range");
		if (cr != null)
			crp = new ContentRangeParser(cr);
		if (!crp.isValid())
			throw new IllegalArgumentException("bad range: " + cr);
		RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
		FileChannel fc = raf.getChannel();
		fc.position(crp.getStart());
		fw = new FileWriter(fc);
	}

	private static class FileWriter implements WritableByteChannel {
		private final FileChannel fc;

		public FileWriter(FileChannel fc) {
			this.fc = fc;
		}

		public int write(ByteBuffer src) throws IOException {
			return fc.write(src);
		}

		public boolean isOpen() {
			return fc.isOpen();
		}

		public void close() throws IOException {
			fc.close();
		}
	}

	/**
	 * Get the channel that is written to.
	 * 
	 * @return the channel that the resource is cached to
	 */
	public WritableByteChannel getChannel() {
		return fw;
	}

	/**
	 * Get the start position of the range.
	 * 
	 * @return the start position of the range
	 */
	public long getStart() {
		return crp.getStart();
	}

	/**
	 * Get the end position of the range.
	 * 
	 * @return the end position of the range
	 */
	public long getEnd() {
		return crp.getEnd();
	}

	/**
	 * Get the size of the range
	 * 
	 * @return the number of bytes for the range
	 */
	public long getTotal() {
		return crp.getTotal();
	}
}
