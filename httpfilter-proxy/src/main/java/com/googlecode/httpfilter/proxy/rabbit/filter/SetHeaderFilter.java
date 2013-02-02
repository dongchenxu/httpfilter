package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.nio.channels.SocketChannel;
import java.util.Map;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * This is a class that sets headers in the request and/or response. Mostly an
 * example of how to set headers.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SetHeaderFilter implements HttpFilter {
	private SProperties props;

	private void addHeaders(HttpHeader header, String prefix) {
		for (Map.Entry<String, String> me : props.entrySet()) {
			String key = me.getKey();
			if (key.startsWith(prefix)) {
				key = key.substring(prefix.length());
				String value = me.getValue();
				header.addHeader(key, value);
			}
		}
	}

	public HttpHeader doHttpInFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		addHeaders(header, "request.");
		return null;
	}

	public HttpHeader doHttpOutFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {

		addHeaders(header, "response.");
		return null;
	}

	public HttpHeader doConnectFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		addHeaders(header, "connect.");
		return null;
	}

	public void setup(SProperties properties, HttpProxy proxy) {
		this.props = properties;
	}
}
