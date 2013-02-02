package com.googlecode.httpfilter.proxy.rabbit.zip;

/**
 * A listener for gzip unpack data events.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface GZipUnpackListener extends GZipListener {

	/**
	 * Some data has been unpacked.
	 * 
	 * @param buf
	 *            the buffer for the unpacked data
	 * @param off
	 *            the starting offset for the unpacked data.
	 * @param len
	 *            the length of the unpacked data.
	 */
	void unpacked(byte[] buf, int off, int len);

	/** Call when unpacking is really finished. */
	void finished();
}
