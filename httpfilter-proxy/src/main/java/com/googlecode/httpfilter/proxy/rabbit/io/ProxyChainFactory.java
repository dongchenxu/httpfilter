package com.googlecode.httpfilter.proxy.rabbit.io;

import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.rabbit.dns.DNSHandler;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * A constructor of ProxyChain:s.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface ProxyChainFactory {
	/**
	 * Create a ProxyChain given the properties.
	 * 
	 * @param props
	 *            the properties to use when constructing the proxy chain
	 * @param nio
	 *            the NioHandler to use for network and background tasks
	 * @param dnsHandler
	 *            the DNSHandler to use for normal DNS lookups
	 * @param logger
	 *            the Logger to log errors to
	 * @return the new ProxyChain
	 */
	ProxyChain getProxyChain(SProperties props, NioHandler nio,
			DNSHandler dnsHandler, Logger logger);
}