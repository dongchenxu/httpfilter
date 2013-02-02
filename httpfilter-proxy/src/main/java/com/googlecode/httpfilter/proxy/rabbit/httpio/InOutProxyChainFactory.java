package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.rabbit.dns.DNSHandler;
import com.googlecode.httpfilter.proxy.rabbit.io.ProxyChain;
import com.googlecode.httpfilter.proxy.rabbit.io.ProxyChainFactory;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * A factory that creates InOutProxyChain:s.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class InOutProxyChainFactory implements ProxyChainFactory {
	public ProxyChain getProxyChain(SProperties props, NioHandler nio,
			DNSHandler dnsHandler, Logger logger) {
		String insideMatch = props.getProperty("inside_match");
		String pname = props.getProperty("proxyhost", "").trim();
		String pport = props.getProperty("proxyport", "").trim();
		String pauth = props.getProperty("proxyauth");

		try {
			InetAddress proxy = dnsHandler.getInetAddress(pname);
			try {
				int port = Integer.parseInt(pport);
				return new InOutProxyChain(insideMatch, nio, dnsHandler, proxy,
						port, pauth);
			} catch (NumberFormatException e) {
				logger.severe("Strange proxyport: '" + pport
						+ "', will not chain");
			}
		} catch (UnknownHostException e) {
			logger.severe("Unknown proxyhost: '" + pname + "', will not chain");
		}
		return null;
	}
}
