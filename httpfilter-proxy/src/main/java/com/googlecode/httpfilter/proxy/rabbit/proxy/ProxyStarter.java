package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.IOException;

/**
 * A class that starts up proxies.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ProxyStarter {

	/**
	 * Create the ProxyStarter and let it parse the command line arguments.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		ProxyStarter ps = new ProxyStarter();
		ps.startProxy();
	}

	public void startProxy() {
		try {
			HttpProxy p = new HttpProxy();
			p.setConfig(ProxyStarter.class.getResourceAsStream("/conf/rabbit.conf"));
			p.start();
		} catch (IOException e) {
			System.err.println("failed to configure proxy, ignoring: " + e);
			e.printStackTrace();
		}
	}
}
