package com.googlecode.httpfilter.proxy.rabbit.dns;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import org.xbill.DNS.Address;
import org.xbill.DNS.Cache;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * A DNS handler using the dnsjava packages
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class DNSJavaHandler implements DNSHandler {
	private final Logger logger = Logger.getLogger(getClass().getName());

	/** Do any neccessary setup. */
	public void setup(SProperties config) {
		if (config == null)
			config = new SProperties();
		String ct = config.getProperty("dnscachetime", "8").trim();
		int time = 8 * 3600;
		try {
			time = Integer.parseInt(ct) * 3600;
		} catch (NumberFormatException e) {
			logger.warning("bad number for dnscachetime: '" + ct + "', using: "
					+ (time / 3600) + " hours");
		}
		Cache dnsCache = Lookup.getDefaultCache(DClass.IN);
		dnsCache.setMaxCache(time);
		dnsCache.setMaxNCache(time);
	}

	/** Look up an internet address. */
	public InetAddress getInetAddress(URL url) throws UnknownHostException {
		return getInetAddress(url.getHost());
	}

	public InetAddress getInetAddress(String host) throws UnknownHostException {
		return Address.getByName(host);
	}
}
