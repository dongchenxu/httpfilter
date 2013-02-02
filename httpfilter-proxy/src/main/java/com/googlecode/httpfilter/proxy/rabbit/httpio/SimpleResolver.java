package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.net.URL;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.DefaultTaskIdentifier;
import com.googlecode.httpfilter.proxy.rabbit.dns.DNSHandler;
import com.googlecode.httpfilter.proxy.rabbit.io.InetAddressListener;
import com.googlecode.httpfilter.proxy.rabbit.io.Resolver;

/**
 * A simple resolver that uses the given dns handler.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SimpleResolver implements Resolver {
	private final DNSHandler dnsHandler;
	private final NioHandler nio;

	/**
	 * Create a new Resolver that does normal DNS lookups.
	 * 
	 * @param nio
	 *            the NioHandler to use for running background tasks
	 * @param dnsHandler
	 *            the DNSHandler to use for the DNS lookup
	 */
	public SimpleResolver(NioHandler nio, DNSHandler dnsHandler) {
		this.dnsHandler = dnsHandler;
		this.nio = nio;
	}

	public void getInetAddress(URL url, InetAddressListener listener) {
		String groupId = getClass().getSimpleName();
		nio.runThreadTask(new ResolvRunner(dnsHandler, url, listener),
				new DefaultTaskIdentifier(groupId, url.toString()));
	}

	public int getConnectPort(int port) {
		return port;
	}

	public boolean isProxyConnected() {
		return false;
	}

	public String getProxyAuthString() {
		return null;
	}
}
