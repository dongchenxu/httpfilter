package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.nio.channels.SocketChannel;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;
import com.googlecode.httpfilter.proxy.rabbit.util.PatternHelper;

/**
 * This is a class that blocks access to certain part of the www. You can either
 * specify a deny filter, using blockURLmatching or you can specify an accept
 * filter, using allowURLmatching.
 * 
 * If you specify an accept filter, then no other urls will be accepted.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class BlockFilter implements HttpFilter {
	private Pattern blockPattern;
	private Pattern allowPattern;

	public HttpHeader doHttpInFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		if (allowPattern != null) {
			Matcher m = allowPattern.matcher(header.getRequestURI());
			if (m.find())
				return null;
			return con.getHttpGenerator().get403();
		}

		if (blockPattern == null)
			return null;
		Matcher m = blockPattern.matcher(header.getRequestURI());
		if (m.find())
			return con.getHttpGenerator().get403();
		return null;
	}

	public HttpHeader doHttpOutFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		return null;
	}

	public HttpHeader doConnectFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		// TODO: possibly block connect requests?
		return null;
	}

	/**
	 * Setup this class with the given properties.
	 * 
	 * @param properties
	 *            the new configuration of this class.
	 */
	public void setup(SProperties properties, HttpProxy proxy) {
		PatternHelper ph = new PatternHelper();
		blockPattern = ph.getPattern(properties, "blockURLmatching",
				"BlockFilter: bad pattern: ");
		allowPattern = ph.getPattern(properties, "allowURLmatching",
				"AllowFilter: bad pattern: ");
	}
}
