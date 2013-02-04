package com.googlecode.httpfilter.proxy.rabbit.dns;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * A DNS handler using the standard java packages.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class DNSSunHandler implements DNSHandler {
	public void setup(SProperties config) {
		// empty.
	}

	public InetAddress getInetAddress(URL url) throws UnknownHostException {
		return InetAddress.getByName(url.getHost());
	}

	public InetAddress getInetAddress(String host) throws UnknownHostException {
		return InetAddress.getByName(host);
	}
}
