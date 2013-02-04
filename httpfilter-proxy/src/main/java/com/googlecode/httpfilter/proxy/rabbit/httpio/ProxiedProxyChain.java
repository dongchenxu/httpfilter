package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.net.InetAddress;
import com.googlecode.httpfilter.proxy.rabbit.io.ProxyChain;
import com.googlecode.httpfilter.proxy.rabbit.io.Resolver;

/**
 * An implementation of ProxyChain that always goes through some other proxy
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ProxiedProxyChain implements ProxyChain {
	private final Resolver resolver;

	/**
	 * Create a new ProxyChain that always will proxy to the given address
	 * 
	 * @param proxy
	 *            the hostname to connect to
	 * @param port
	 *            the port to connect to
	 * @param proxyAuth
	 *            the http basic proxy authentication token
	 */
	public ProxiedProxyChain(InetAddress proxy, int port, String proxyAuth) {
		resolver = new ProxyResolver(proxy, port, proxyAuth);
	}

	public Resolver getResolver(String url) {
		return resolver;
	}
}
