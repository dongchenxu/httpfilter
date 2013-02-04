package com.googlecode.httpfilter.proxy.rabbit.io;

import java.net.InetAddress;

/**
 * An interface for a listener of asyncronous dns lookups.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface InetAddressListener {
	/**
	 * An dns lookup has completed.
	 * 
	 * @param ia
	 *            the InetAddress requested.
	 */
	void lookupDone(InetAddress ia);

	/**
	 * Lookup failed.
	 * 
	 * @param e
	 *            the cause of the problem
	 */
	void unknownHost(Exception e);
}
