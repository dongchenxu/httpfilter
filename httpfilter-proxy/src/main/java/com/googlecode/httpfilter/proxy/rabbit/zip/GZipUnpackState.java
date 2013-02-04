package com.googlecode.httpfilter.proxy.rabbit.zip;

/**
 * The state a gzip unpacking can be in.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
interface GZipUnpackState {
	/**
	 * Check if the unpacker currently needs more data
	 * 
	 * @return true if more input data is currently needed
	 */
	boolean needsInput();

	/**
	 * Handle a buffer.
	 * 
	 * @param unpacker
	 *            the data handler
	 * @param buf
	 *            the data to be handled.
	 * @param off
	 *            the start offset of the data.
	 * @param len
	 *            the length of the data.
	 */
	void handleBuffer(GZipUnpacker unpacker, byte[] buf, int off, int len);

	/**
	 * Handle the next block of the current data.
	 * 
	 * @param unpacker
	 *            the data handler
	 */
	void handleCurrentData(GZipUnpacker unpacker);
}
