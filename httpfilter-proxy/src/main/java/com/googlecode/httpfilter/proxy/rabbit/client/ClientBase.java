package com.googlecode.httpfilter.proxy.rabbit.client;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.BufferHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.StatisticsHolder;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.BasicStatisticsHolder;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.CachingBufferHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.MultiSelectorNioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.SimpleThreadFactory;
import com.googlecode.httpfilter.proxy.rabbit.dns.DNSJavaHandler;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpResponseListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpResponseReader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.SimpleProxyChain;
import com.googlecode.httpfilter.proxy.rabbit.httpio.WebConnectionResourceSource;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.ConnectionHandler;
import com.googlecode.httpfilter.proxy.rabbit.io.ProxyChain;
import com.googlecode.httpfilter.proxy.rabbit.io.WebConnection;
import com.googlecode.httpfilter.proxy.rabbit.io.WebConnectionListener;
import com.googlecode.httpfilter.proxy.rabbit.util.Counter;
import com.googlecode.httpfilter.proxy.rabbit.util.SimpleTrafficLogger;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A class for doing http requests.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ClientBase {
	private final Logger logger = Logger.getLogger(getClass().getName());
	private final ConnectionHandler connectionHandler;
	private final NioHandler nioHandler;
	private final TrafficLogger trafficLogger = new SimpleTrafficLogger();
	private final BufferHandler bufHandler;

	/**
	 * Create a new ClientBase.
	 * 
	 * @throws IOException
	 *             if creating the nio handler fails
	 */
	public ClientBase() throws IOException {
		ExecutorService es = Executors.newCachedThreadPool();
		StatisticsHolder sh = new BasicStatisticsHolder();
		nioHandler = new MultiSelectorNioHandler(es, sh, 4, 15000L);
		nioHandler.start(new SimpleThreadFactory());
		DNSJavaHandler jh = new DNSJavaHandler();
		jh.setup(null);
		ProxyChain proxyChain = new SimpleProxyChain(nioHandler, jh);
		Counter counter = new Counter();
		connectionHandler = new ConnectionHandler(counter, proxyChain,
				nioHandler);

		bufHandler = new CachingBufferHandler();
	}

	/**
	 * Submit a new request, using the given method to the given url.
	 * 
	 * @param method
	 *            HEAD or GET or POST or ...
	 * @param url
	 *            the url to do the http request against.
	 * @return the header of the request
	 * @throws IOException
	 *             if the url is not really an URL
	 */
	public HttpHeader getRequest(String method, String url) throws IOException {
		URL u = new URL(url);
		HttpHeader ret = new HttpHeader();
		ret.setStatusLine(method + " " + url + " HTTP/1.1");
		ret.setHeader("Host", u.getHost());
		ret.setHeader("User-Agent", "rabbit client library");
		return ret;
	}

	/**
	 * Get the NioHandler that this client is using
	 * 
	 * @return the current NioHandler
	 */
	public NioHandler getNioHandler() {
		return nioHandler;
	}

	/**
	 * Get the logger that this client is using
	 * 
	 * @return the current logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Shutdown this client handler.
	 */
	public void shutdown() {
		nioHandler.shutdown();
	}

	/**
	 * Send a request and let the client be notified on response.
	 * 
	 * @param request
	 *            the request to send
	 * @param client
	 *            the listener to notify with the response
	 */
	public void sendRequest(HttpHeader request, ClientListener client) {
		WebConnectionListener wcl = new WCL(request, client);
		connectionHandler.getConnection(request, wcl);
	}

	private void handleTimeout(HttpHeader request, ClientListener client) {
		client.handleTimeout(request);
	}

	private void handleFailure(HttpHeader request, ClientListener client,
			Exception e) {
		client.handleFailure(request, e);
	}

	private abstract class BaseAsyncListener {
		protected final HttpHeader request;
		protected final ClientListener client;

		public BaseAsyncListener(HttpHeader request, ClientListener client) {
			this.request = request;
			this.client = client;
		}

		public void timeout() {
			handleTimeout(request, client);
		}

		public void failed(Exception e) {
			handleFailure(request, client, e);
		}
	}

	private class WCL extends BaseAsyncListener implements
			WebConnectionListener {

		public WCL(HttpHeader request, ClientListener client) {
			super(request, client);
		}

		public void connectionEstablished(WebConnection wc) {
			sendRequest(request, client, wc);
		}
	}

	private void sendRequest(HttpHeader request, ClientListener client,
			WebConnection wc) {
		HttpResponseListener hrl = new HRL(request, client, wc);
		try {
			HttpResponseReader rr = new HttpResponseReader(wc.getChannel(),
					nioHandler, trafficLogger, bufHandler, request, true, true,
					hrl);
			rr.sendRequestAndWaitForResponse();
		} catch (IOException e) {
			handleFailure(request, client, e);
		}
	}

	private class HRL extends BaseAsyncListener implements HttpResponseListener {
		private final WebConnection wc;

		public HRL(HttpHeader request, ClientListener client, WebConnection wc) {
			super(request, client);
			this.wc = wc;
		}

		public void httpResponse(HttpHeader response,
				BufferHandle bufferHandle, boolean keepalive,
				boolean isChunked, long dataSize) {
			int status = Integer.parseInt(response.getStatusCode());
			if (client.followRedirects() && isRedirect(status)) {
				connectionHandler.releaseConnection(wc);
				String loc = response.getHeader("Location");
				client.redirected(request, loc, ClientBase.this);
			} else {
				WebConnectionResourceSource wrs = getWebConnectionResouceSource(
						wc, bufferHandle, isChunked, dataSize);
				client.handleResponse(request, response, wrs);
			}
		}
	}

	/**
	 * Check if the status code is a redirect code.
	 * 
	 * @param status
	 *            the status code to check
	 * @return true if the status code is a redirect
	 */
	private boolean isRedirect(int status) {
		return status == 301 || status == 302 || status == 303 || status == 307;
	}

	/**
	 * Create the url that the response redirected the request to.
	 * 
	 * @param request
	 *            the actual request made
	 * @param location
	 *            the redirect location
	 * @return the redirected url
	 * @throws IOException
	 *             if the redirect url can not be created
	 */
	public URL getRedirectedURL(HttpHeader request, String location)
			throws IOException {
		URL u = new URL(request.getRequestURI());
		return new URL(u, location);
	}

	private WebConnectionResourceSource getWebConnectionResouceSource(
			WebConnection wc, BufferHandle bufferHandle, boolean isChunked,
			long dataSize) {
		return new WebConnectionResourceSource(connectionHandler, nioHandler,
				wc, bufferHandle, trafficLogger, isChunked, dataSize, true);
	}
}
