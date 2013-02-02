package com.googlecode.httpfilter.proxy.rabbit.zip;

/**
 * A listener for gzip pack data events.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface GZipPackListener extends GZipListener {
	/**
	 * some data has been packed.
	 * 
	 * @param buf
	 *            the buffer for the unpacked data
	 * @param off
	 *            the starting offset for the unpacked data.
	 * @param len
	 *            the length of the unpacked data.
	 */
	void packed(byte[] buf, int off, int len);

	/** Called when all data has been packed. */
	void dataPacked();

	/** Called when the output has been finished. */
	void finished();
}
