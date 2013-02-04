package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderSender;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderSentListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpResponseListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpResponseReader;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.CacheBufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.ProxyChain;
import com.googlecode.httpfilter.proxy.rabbit.io.Resolver;
import com.googlecode.httpfilter.proxy.rabbit.io.WebConnection;
import com.googlecode.httpfilter.proxy.rabbit.io.WebConnectionListener;
import com.googlecode.httpfilter.proxy.rabbit.util.Base64;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.Closer;

/**
 * A handler that shuttles ssl traffic
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SSLHandler implements TunnelDoneListener {
	private final HttpProxy proxy;
	private final Connection con;
	private final HttpHeader request;
	private final TrafficLoggerHandler tlh;
	private final Resolver resolver;
	private SocketChannel channel;
	private BufferHandle bh;
	private BufferHandle sbh;
	private WebConnection wc;
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Create a new SSLHandler
	 * 
	 * @param proxy
	 *            the HttpProxy this SSL connection is serving
	 * @param con
	 *            the Connection to handle
	 * @param request
	 *            the CONNECT header
	 * @param tlh
	 *            the traffic statistics gatherer
	 */
	public SSLHandler(HttpProxy proxy, Connection con, HttpHeader request,
			TrafficLoggerHandler tlh) {
		this.proxy = proxy;
		this.con = con;
		this.request = request;
		this.tlh = tlh;
		ProxyChain pc = con.getProxy().getProxyChain();
		resolver = pc.getResolver(request.getRequestURI());
	}

	/**
	 * Are we allowed to proxy ssl-type connections ?
	 * 
	 * @return true if we allow the CONNECT &lt;port&gt; command.
	 */
	public boolean isAllowed() {
		String hp = request.getRequestURI();
		int c = hp.indexOf(':');
		Integer port = Integer.valueOf(443);
		if (c >= 0) {
			try {
				port = new Integer(hp.substring(c + 1));
			} catch (NumberFormatException e) {
				logger.warning("Connect to odd port: " + e);
				return false;
			}
		}
		if (!proxy.proxySSL)
			return false;
		if (proxy.proxySSL && proxy.sslports == null)
			return true;
		for (int i = 0; i < proxy.sslports.size(); i++) {
			if (port.equals(proxy.sslports.get(i)))
				return true;
		}
		return false;
	}

	/**
	 * handle the tunnel.
	 * 
	 * @param channel
	 *            the client channel
	 * @param bh
	 *            the buffer handle used, may contain data from client.
	 */
	public void handle(SocketChannel channel, BufferHandle bh) {
		this.channel = channel;
		this.bh = bh;
		if (resolver.isProxyConnected()) {
			String auth = resolver.getProxyAuthString();
			// it should look like this (using RabbIT:RabbIT):
			// Proxy-authorization: Basic UmFiYklUOlJhYmJJVA==
			if (auth != null && !auth.equals(""))
				request.setHeader("Proxy-authorization",
						"Basic " + Base64.encode(auth));
		}
		WebConnectionListener wcl = new WebConnector();
		proxy.getWebConnection(request, wcl);
	}

	private class WebConnector implements WebConnectionListener {
		private final String uri;

		public WebConnector() {
			uri = request.getRequestURI();
			// java needs protocoll to build URL
			request.setRequestURI("http://" + uri);
		}

		public void connectionEstablished(WebConnection wce) {
			wc = wce;
			if (resolver.isProxyConnected()) {
				request.setRequestURI(uri); // send correct connect to next
											// proxy.
				setupChain();
			} else {
				BufferHandle bh = new CacheBufferHandle(
						proxy.getBufferHandler());
				sendOkReplyAndTunnel(bh);
			}
		}

		public void timeout() {
			String err = "SSLHandler: Timeout waiting for web connection: "
					+ uri;
			logger.warning(err);
			closeDown();
		}

		public void failed(Exception e) {
			warn("SSLHandler: failed to get web connection to: " + uri, e);
			closeDown();
		}
	}

	private void closeDown() {
		if (bh != null)
			bh.possiblyFlush();
		if (sbh != null)
			sbh.possiblyFlush();
		Closer.close(wc, logger);
		wc = null;
		con.logAndClose();
	}

	private void warn(String err, Exception e) {
		logger.log(Level.WARNING, err, e);
	}

	private void setupChain() {
		HttpResponseListener cr = new ChainResponseHandler();
		try {
			HttpResponseReader hrr = new HttpResponseReader(wc.getChannel(),
					proxy.getNioHandler(), tlh.getNetwork(),
					proxy.getBufferHandler(), request, proxy.getStrictHttp(),
					resolver.isProxyConnected(), cr);
			hrr.sendRequestAndWaitForResponse();
		} catch (IOException e) {
			warn("IOException when waiting for chained response: "
					+ request.getRequestURI(), e);
			closeDown();
		}
	}

	private class ChainResponseHandler implements HttpResponseListener {
		public void httpResponse(HttpHeader response, BufferHandle rbh,
				boolean keepalive, boolean isChunked, long dataSize) {
			String status = response.getStatusCode();
			if (!"200".equals(status)) {
				closeDown();
			} else {
				sendOkReplyAndTunnel(rbh);
			}
		}

		public void failed(Exception cause) {
			warn("SSLHandler: failed to get chained response: "
					+ request.getRequestURI(), cause);
			closeDown();
		}

		public void timeout() {
			String err = "SSLHandler: Timeout waiting for chained response: "
					+ request.getRequestURI();
			logger.warning(err);
			closeDown();
		}
	}

	private void sendOkReplyAndTunnel(BufferHandle server2client) {
		HttpHeader reply = new HttpHeader();
		reply.setStatusLine("HTTP/1.0 200 Connection established");
		reply.setHeader("Proxy-agent", proxy.getServerIdentity());

		HttpHeaderSentListener tc = new TunnelConnected(server2client);
		try {
			HttpHeaderSender hhs = new HttpHeaderSender(channel,
					proxy.getNioHandler(), tlh.getClient(), reply, false, tc);
			hhs.sendHeader();
		} catch (IOException e) {
			warn("IOException when sending header", e);
			closeDown();
		}
	}

	private class TunnelConnected implements HttpHeaderSentListener {
		private final BufferHandle server2client;

		public TunnelConnected(BufferHandle server2client) {
			this.server2client = server2client;
		}

		public void httpHeaderSent() {
			tunnelData(server2client);
		}

		public void timeout() {
			logger.warning("SSLHandler: Timeout when sending http header: "
					+ request.getRequestURI());
			closeDown();
		}

		public void failed(Exception e) {
			warn("SSLHandler: Exception when sending http header: "
					+ request.getRequestURI(), e);
			closeDown();
		}
	}

	private void tunnelData(BufferHandle server2client) {
		sbh = server2client;
		SocketChannel sc = wc.getChannel();
		Tunnel tunnel = new Tunnel(proxy.getNioHandler(), channel, bh,
				tlh.getClient(), sc, server2client, tlh.getNetwork(), this);
		tunnel.start();
	}

	public void tunnelClosed() {
		if (wc != null) {
			con.logAndClose();
			Closer.close(wc, logger);
		}
		wc = null;
	}
}
