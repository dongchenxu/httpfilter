package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.nio.channels.SocketChannel;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * A filter for http headers.
 */
public interface HttpFilter {

	/**
	 * Test if a socket/header combination is valid or return a new HttpHeader.
	 * 
	 * @param socket
	 *            the Socket that made the request.
	 * @param header
	 *            the actual request made.
	 * @param con
	 *            the Connection handling the request.
	 * @return null if everything is fine or a HTTPHeader describing the error
	 *         (like a 403).
	 */
	HttpHeader doHttpInFiltering(SocketChannel socket, HttpHeader header,
			Connection con);

	/**
	 * Test if a socket/header combination is valid or return a new HttpHeader.
	 * 
	 * @param socket
	 *            the Socket that made the request.
	 * @param header
	 *            the actual request made.
	 * @param con
	 *            the Connection handling the request.
	 * @return null if everything is fine or a HTTPHeader describing the error
	 *         (like a 403).
	 */
	HttpHeader doHttpOutFiltering(SocketChannel socket, HttpHeader header,
			Connection con);

	/**
	 * Test if a socket/header combination is valid or return a new HttpHeader.
	 * 
	 * @param socket
	 *            the Socket that made the request.
	 * @param header
	 *            the actual request made.
	 * @param con
	 *            the Connection handling the request.
	 * @return null if everything is fine or a HTTPHeader describing the error
	 *         (like a 403).
	 */
	HttpHeader doConnectFiltering(SocketChannel socket, HttpHeader header,
			Connection con);

	/**
	 * Setup this filter.
	 * 
	 * @param properties
	 *            the SProperties to get the settings from.
	 * @param proxy
	 *            the HttpProxy that is using this filter
	 */
	void setup(SProperties properties, HttpProxy proxy);
}
