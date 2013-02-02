package com.googlecode.httpfilter.proxy.rabbit.proxy;

import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;

/**
 * A listener for client resource data (POST:ed content).
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface ClientResourceListener {
	/**
	 * Some parts of the resource has been read.
	 * 
	 * @param bufHandle
	 *            the holder of the read data
	 */
	void resourceDataRead(BufferHandle bufHandle);
}
