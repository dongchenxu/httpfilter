package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.nio.channels.SocketChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * This is a filter that set up rabbit for reverse proxying.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ReverseProxy implements HttpFilter {
	private String matcher = null;
	private String replacer = null;
	private Pattern deny = null;
	private boolean allowMeta = false;

	public HttpHeader doHttpInFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		String s = header.getRequestURI();
		if (deny != null) {
			Matcher m = deny.matcher(s);
			if (m.matches() && allowMeta) {
				String metaStart = "http://"
						+ con.getProxy().getHost().getHostName() + ":"
						+ con.getProxy().getPort() + "/";
				if (!s.startsWith(metaStart)) {
					return con.getHttpGenerator().get403();
				}
			}
		}
		if (matcher != null && replacer != null && s != null && s.length() > 0
				&& s.charAt(0) == '/') {
			String newRequest = s.replaceAll(matcher, replacer);
			header.setRequestURI(newRequest);
		}
		return null;
	}

	public HttpHeader doHttpOutFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		return null;
	}

	public HttpHeader doConnectFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		return null;
	}

	/**
	 * Setup this class with the given properties.
	 * 
	 * @param properties
	 *            the new configuration of this class.
	 */
	public void setup(SProperties properties, HttpProxy proxy) {
		matcher = properties.getProperty("transformMatch", "");
		replacer = properties.getProperty("transformTo", "");
		String denyString = properties.getProperty("deny");
		if (denyString != null)
			deny = Pattern.compile(denyString);
		allowMeta = properties.getProperty("allowMeta", "true")
				.equalsIgnoreCase("true");
	}
}
