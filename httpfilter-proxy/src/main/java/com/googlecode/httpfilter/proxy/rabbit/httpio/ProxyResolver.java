package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.net.InetAddress;
import java.net.URL;
import com.googlecode.httpfilter.proxy.rabbit.io.InetAddressListener;
import com.googlecode.httpfilter.proxy.rabbit.io.Resolver;

/**
 * A resolver that always return the proxy address.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ProxyResolver implements Resolver {
	/** Adress of connected proxy. */
	private final InetAddress proxy;
	/** Port of the connected proxy. */
	private final int port;
	/** The proxy auth token we will use. */
	private final String auth;

	/**
	 * Create a new ProxyResolver that will always return the given address.
	 * 
	 * @param proxy
	 *            the upstream proxy to use for all requests
	 * @param port
	 *            the upstream proxy port to use for all requests
	 * @param auth
	 *            the upstream proxy basic auth string to use for all request
	 */
	public ProxyResolver(InetAddress proxy, int port, String auth) {
		this.proxy = proxy;
		this.port = port;
		this.auth = auth;
	}

	public void getInetAddress(URL url, InetAddressListener listener) {
		listener.lookupDone(proxy);
	}

	public int getConnectPort(int wantedPort) {
		return port;
	}

	public boolean isProxyConnected() {
		return true;
	}

	public String getProxyAuthString() {
		return auth;
	}
}