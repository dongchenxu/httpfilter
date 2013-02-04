package com.googlecode.httpfilter.proxy.rabbit.client;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.WebConnectionResourceSource;

/**
 * A basic ClientListener.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ClientListenerAdapter implements ClientListener {
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Create the redirected url and calls redirectedTo() and requestDone().
	 */
	@Override
	public void redirected(HttpHeader request, String location, ClientBase base) {
		try {
			URL u = base.getRedirectedURL(request, location);
			redirectedTo(u.toString());
			requestDone(request);
		} catch (IOException e) {
			handleFailure(request, e);
		}
	}

	/**
	 * This method does nothing, override to perform actual request.
	 * 
	 * @param url
	 *            the new URL that the redirect header contained
	 * @throws IOException
	 *             if redirecting fails
	 */
	public void redirectedTo(String url) throws IOException {
		// nothing
	}

	/**
	 * This method does nothing.
	 */
	@Override
	public void handleResponse(HttpHeader request, HttpHeader response,
			WebConnectionResourceSource wc) {
		// empty
	}

	/**
	 * This method returns true, override if you want different behaviour.
	 */
	@Override
	public boolean followRedirects() {
		return true;
	}

	/**
	 * Logs an error to the logger and calls requestDone().
	 */
	@Override
	public void handleTimeout(HttpHeader request) {
		logger.warning("Request to " + request.getRequestURI() + " timed out");
		requestDone(request);
	}

	/**
	 * Logs an error to the logger and calls requestDone().
	 */
	@Override
	public void handleFailure(HttpHeader request, Exception e) {
		logger.log(Level.WARNING, "Request to " + request.getRequestURI()
				+ " failed: ", e);
		requestDone(request);
	}

	/** Handle any cleanup in this method. */
	@Override
	public void requestDone(HttpHeader request) {
		// nothing.
	}
}