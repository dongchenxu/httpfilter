package com.googlecode.httpfilter.proxy.rabbit.client;

import java.util.concurrent.atomic.AtomicInteger;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;

/**
 * A helper class that shuts down the clientBase when all requests have
 * finished.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class CountingClientBaseStopper {
	private final AtomicInteger outstandingRequests = new AtomicInteger();
	private final ClientBase clientBase;

	/**
	 * Create a new CountingClientBaseStopper that will shutdown the given
	 * client once all outstanding requests have been fully handled.
	 * 
	 * @param clientBase
	 *            the actual client
	 */
	public CountingClientBaseStopper(ClientBase clientBase) {
		this.clientBase = clientBase;
	}

	/**
	 * Send a request
	 * 
	 * @param request
	 *            the http header to send
	 * @param listener
	 *            the client handling the resource
	 */
	public void sendRequest(HttpHeader request, ClientListener listener) {
		outstandingRequests.incrementAndGet();
		clientBase.sendRequest(request, listener);
	}

	/**
	 * Called when one request has finished
	 */
	public void requestDone() {
		int outstanding = outstandingRequests.decrementAndGet();
		if (outstanding == 0)
			clientBase.shutdown();
	}
}