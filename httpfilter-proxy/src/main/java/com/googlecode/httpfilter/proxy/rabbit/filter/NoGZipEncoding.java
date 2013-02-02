package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * This is a class that removes "Accept-Encoding: gzip"
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class NoGZipEncoding implements HttpFilter {
	private boolean remove = true;

	public HttpHeader doHttpInFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		if (!remove)
			return null;

		List<String> aes = header.getHeaders("Accept-Encoding");
		List<String> faes = new ArrayList<String>(aes.size());
		boolean found = false;
		int s = aes.size();
		for (int i = 0; i < s; i++) {
			String ae = aes.get(i);
			String lcAe = ae.toLowerCase();
			int k = lcAe.indexOf("gzip");
			if (k != -1) {
				found = true;
				StringBuilder sb = new StringBuilder();
				if (k > 0)
					sb.append(ae.substring(0, k));
				if (ae.length() > k + 4) {
					String rest = ae.substring(k + 4);
					if (rest.charAt(0) == ',')
						rest = rest.substring(1);
					sb.append(rest);
				}
				ae = sb.toString();
				ae = ae.trim();
				if (!"".equals(ae))
					faes.add(ae);
			}
		}
		if (found) {
			header.removeHeader("Accept-Encoding");
			s = faes.size();
			for (int i = 0; i < s; i++)
				header.addHeader("Accept-Encoding", faes.get(i));
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
		String rs = properties.getProperty("remove", "");
		remove = "true".equalsIgnoreCase(rs);
	}
}
