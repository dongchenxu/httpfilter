package com.googlecode.httpfilter.proxy.rabbit.zip;

/**
 * The state a gzip packing can be in.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
interface GZipPackState {
	/**
	 * Check if the packer currently needs more data
	 * 
	 * @return true if more input data is currently needed
	 */
	boolean needsInput();

	/**
	 * Handle a buffer.
	 * 
	 * @param packer
	 *            the GZipPacker that is coordinating the packing.
	 * @param buf
	 *            the data to be handled.
	 * @param off
	 *            the start offset of the data.
	 * @param len
	 *            the length of the data.
	 */
	void handleBuffer(GZipPacker packer, byte[] buf, int off, int len);

	/**
	 * Handle the next block of the current data.
	 * 
	 * @param packer
	 *            the GZipPacker that is coordinating the packing.
	 */
	void handleCurrentData(GZipPacker packer);

	/**
	 * Tell the current state that packing is finished.
	 */
	void finish();

	/**
	 * Check if packing is finished.
	 * 
	 * @return true if packing has finished (according to this state)
	 */
	boolean finished();
}
