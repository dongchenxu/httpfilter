package com.googlecode.httpfilter.proxy.rabbit.proxy;

import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.io.WebConnection;

/**
 * A client resource handler
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface ClientResourceHandler {
	/**
	 * Modify the request sent to the server, used to add "Expect: 100 Continue"
	 * and similar.
	 * 
	 * @param header
	 *            the HttpHeader to be modified by this client request.
	 */
	void modifyRequest(HttpHeader header);

	/**
	 * Transfer the resouce data
	 * 
	 * @param wc
	 *            the web connection to send the resource to
	 * @param crtl
	 *            the listener that want to know when the resource have been
	 *            sent or when a failure have occurred.
	 */
	void transfer(WebConnection wc, ClientResourceTransferredListener crtl);

	/**
	 * Add a listener for the client resource data.
	 * 
	 * @param crl
	 *            the listener
	 */
	void addContentListener(ClientResourceListener crl);
}
