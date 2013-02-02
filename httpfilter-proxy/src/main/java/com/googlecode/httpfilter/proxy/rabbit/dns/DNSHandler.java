package com.googlecode.httpfilter.proxy.rabbit.dns;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * A DNS handler.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface DNSHandler {
	/**
	 * Do any neccessary setup.
	 * 
	 * @param config
	 *            the properties for this handler
	 */
	void setup(SProperties config);

	/**
	 * Look up an internet address.
	 * 
	 * @param url
	 *            the url to get the host from
	 * @return the InetAddress of the url
	 * @throws UnknownHostException
	 *             if the lookup fails
	 */
	InetAddress getInetAddress(URL url) throws UnknownHostException;

	/**
	 * Look up an internet address.
	 * 
	 * @param host
	 *            the name of the host to lookup
	 * @return the InetAddress for the given host
	 * @throws UnknownHostException
	 *             if the lookup fails
	 */
	InetAddress getInetAddress(String host) throws UnknownHostException;
}
