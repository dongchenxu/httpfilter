package com.googlecode.httpfilter.proxy.rabbit.client.sample;

import java.io.IOException;
import com.googlecode.httpfilter.proxy.rabbit.client.ClientBase;
import com.googlecode.httpfilter.proxy.rabbit.client.ClientListenerAdapter;
import com.googlecode.httpfilter.proxy.rabbit.client.CountingClientBaseStopper;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.WebConnectionResourceSource;

/**
 * A class that performs a set HEAD request to the given urls. This is mostly an
 * example of how to use the rabbit client classes.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Head {
	private final HeadResponseListener listener;
	private final ClientBase clientBase;
	private final CountingClientBaseStopper ccbs;

	/**
	 * Run a HEAD request for any url passed in args and then prints the results
	 * on System.out.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		try {
			LogHeadToSystemOut ltso = new LogHeadToSystemOut();
			Head h = new Head(ltso);
			h.head(args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a new HEAD requestor.
	 * 
	 * @param listener
	 *            the HeadResponseListener to notify when a request has
	 *            completed
	 * @throws IOException
	 *             if the client can not be created
	 */
	public Head(HeadResponseListener listener) throws IOException {
		this.listener = listener;
		clientBase = new ClientBase();
		ccbs = new CountingClientBaseStopper(clientBase);
	}

	/**
	 * Run HEAD requests to all the urls given.
	 * 
	 * @param urls
	 *            a number of urls.
	 * @throws IOException
	 *             if sending the requests fails
	 */
	public void head(String[] urls) throws IOException {
		for (String url : urls)
			head(url);
	}

	/**
	 * Run HEAD requests to the given url
	 * 
	 * @param url
	 *            the url to run a HEAD reqeusts against.
	 * @throws IOException
	 *             if sending the request fails
	 */
	public void head(String url) throws IOException {
		ccbs.sendRequest(clientBase.getRequest("HEAD", url), new HeadListener());
	}

	private class HeadListener extends ClientListenerAdapter {
		@Override
		public void redirectedTo(String url) throws IOException {
			head(url);
		}

		@Override
		public void handleResponse(HttpHeader request, HttpHeader response,
				WebConnectionResourceSource wc) {
			listener.response(request, response);
			wc.release();
			requestDone(request);
		}

		@Override
		public void requestDone(HttpHeader request) {
			ccbs.requestDone();
		}
	}
}
