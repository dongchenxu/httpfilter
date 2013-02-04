package com.googlecode.httpfilter.proxy.rabbit.httpio;

import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;

/**
 * A listener for resource data.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface BlockListener extends AsyncListener {

	/**
	 * A buffer has been read, the buffer has been flip:ed before this call is
	 * made so position and remaining are valid.
	 * 
	 * @param bufHandle
	 *            the data that was read
	 */
	void bufferRead(BufferHandle bufHandle);

	/**
	 * The resource have been fully transferred
	 */
	void finishedRead();
}
