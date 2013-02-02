package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

/**
 * The methods needed for fast transferTo.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface Transferable {

	/**
	 * Get the length of the resource in bytes.
	 * 
	 * @return the size of the resource or -1 if unknown.
	 */
	long length();

	/**
	 * Transfers bytes from this channel's file to the given writable byte
	 * channel.
	 * 
	 * @see java.nio.channels.FileChannel#transferTo(long,long,
	 *      WritableByteChannel) transferTo
	 * 
	 * @param position
	 *            The position within the file at which the transfer is to
	 *            begin; must be non-negative
	 * @param count
	 *            The maximum number of bytes to be transferred; must be
	 *            non-negative
	 * @param target
	 *            The target channel
	 * @return The number of bytes, possibly zero, that were actually
	 *         transferred
	 * @throws IOException
	 *             if data transfer fails
	 */
	long transferTo(long position, long count, WritableByteChannel target)
			throws IOException;
}
