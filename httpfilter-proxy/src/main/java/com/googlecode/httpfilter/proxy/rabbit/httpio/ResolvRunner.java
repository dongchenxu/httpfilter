package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import com.googlecode.httpfilter.proxy.rabbit.dns.DNSHandler;
import com.googlecode.httpfilter.proxy.rabbit.io.InetAddressListener;

/**
 * A dns lookup class that runs in the background.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ResolvRunner implements Runnable {
	private final DNSHandler dnsHandler;
	private final URL url;
	private final InetAddressListener ial;

	/**
	 * Create a new resolver that does the DNS request on a background thread.
	 * 
	 * @param dnsHandler
	 *            the actual DNSHandler to use for dns lookups
	 * @param url
	 *            the url to look up
	 * @param ial
	 *            the listener that will get the callback when the dns lookup is
	 *            done
	 */
	public ResolvRunner(DNSHandler dnsHandler, URL url, InetAddressListener ial) {
		this.dnsHandler = dnsHandler;
		this.url = url;
		this.ial = ial;
	}

	/**
	 * Run a dns lookup and then notifies the listener on the selector thread.
	 */
	public void run() {
		try {
			final InetAddress ia = dnsHandler.getInetAddress(url);
			ial.lookupDone(ia);
		} catch (final UnknownHostException e) {
			ial.unknownHost(e);
		}
	}
}
