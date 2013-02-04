package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.Closer;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpDateParser;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderReader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderSender;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderSentListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.WebConnectionResourceSource;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.ConnectionHandler;
import com.googlecode.httpfilter.proxy.rabbit.io.Resolver;
import com.googlecode.httpfilter.proxy.rabbit.io.WebConnection;
import com.googlecode.httpfilter.proxy.rabbit.io.WebConnectionListener;

/**
 * A class that tries to establish a connection to the real server or the next
 * proxy in the chain.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SWC implements HttpHeaderSentListener, HttpHeaderListener,
		WebConnectionListener, ClientResourceTransferredListener {
	private final Connection con;
	private final Resolver resolver;
	private final HttpHeader header;
	private final TrafficLoggerHandler tlh;
	private final ClientResourceHandler crh;
	private final RequestHandler rh;

	private int attempts = 0;
	private final String method;
	private boolean safe = true;

	private char status = '0';

	private Exception lastException;
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Create a new connection establisher.
	 * 
	 * @param con
	 *            the Connection handling the request
	 * @param resolver
	 *            the dns resolver
	 * @param header
	 *            the actual request
	 * @param tlh
	 *            the traffic statistics gatherer
	 * @param crh
	 *            the handler for client data
	 * @param rh
	 *            the RequestHandler to use
	 */
	public SWC(Connection con, Resolver resolver, HttpHeader header,
			TrafficLoggerHandler tlh, ClientResourceHandler crh,
			RequestHandler rh) {
		this.con = con;
		this.resolver = resolver;
		this.header = header;
		this.tlh = tlh;
		this.crh = crh;
		this.rh = rh;
		method = header.getMethod().trim();
	}

	/**
	 * Try to establish a web connection.
	 */
	public void establish() {
		attempts++;
		con.getCounter()
				.inc("Trying to establish a WebConnection: " + attempts);

		// if we cant get a connection in five cancel..
		if (!safe || attempts > 5) {
			con.webConnectionSetupFailed(rh, lastException);
		} else {
			con.getProxy().getWebConnection(header, this);
		}
	}

	public void connectionEstablished(WebConnection wc) {
		con.getCounter().inc("WebConnection established: " + attempts);
		rh.setWebConnection(wc);
		/*
		 * TODO: handle this if (header.getContentStream () != null)
		 * header.setHeader ("Transfer-Encoding", "chunked");
		 */

		// we cant retry if we sent the header...
		safe = wc.getReleasedAt() > 0
				|| (method != null && (method.equals("GET") || method
						.equals("HEAD")));

		try {
			if (crh != null)
				crh.modifyRequest(header);

			HttpHeaderSender hhs = new HttpHeaderSender(wc.getChannel(),
					con.getNioHandler(), tlh.getNetwork(), header,
					useFullURI(), this);
			hhs.sendHeader();
		} catch (IOException e) {
			failed(e);
		}
	}

	/**
	 * Check if the full uri should be included in the request.
	 * 
	 * @return true if the upstream server need the full uri in the request
	 */
	public boolean useFullURI() {
		return resolver.isProxyConnected();
	}

	public void httpHeaderSent() {
		if (crh != null)
			crh.transfer(rh.getWebConnection(), this);
		else
			httpHeaderSentTransferDone();
	}

	public void clientResourceTransferred() {
		httpHeaderSentTransferDone();
	}

	public void clientResourceAborted(HttpHeader reason) {
		if (rh != null && rh.getWebConnection() != null) {
			rh.getWebConnection().setKeepalive(false);
			con.getProxy().releaseWebConnection(rh.getWebConnection());
		}
		con.sendAndClose(reason);
	}

	private void httpHeaderSentTransferDone() {
		if (!header.isDot9Request()) {
			readRequest();
		} else {
			// HTTP/0.9 close after resource..
			rh.getWebConnection().setKeepalive(false);
			setupResource(rh.getWebHandle(), false, -1);
			con.webConnectionEstablished(rh);
		}
	}

	private void readRequest() {
		con.getCounter().inc(
				"Trying read response from WebConnection: " + attempts);
		try {
			HttpHeaderReader hhr = new HttpHeaderReader(rh.getWebConnection()
					.getChannel(), rh.getWebHandle(), con.getNioHandler(),
					tlh.getNetwork(), false, con.getProxy().getStrictHttp(),
					this);
			hhr.readHeader();
		} catch (IOException e) {
			failed(e);
		}
	}

	public void httpHeaderRead(HttpHeader header, BufferHandle wbh,
			boolean keepalive, boolean isChunked, long dataSize) {
		con.getCounter().inc("Read response from WebConnection: " + attempts);
		rh.setWebHeader(header);
		rh.setWebHandle(wbh);
		rh.getWebConnection().setKeepalive(keepalive);

		String sc = rh.getWebHeader().getStatusCode();
		// if client is using http/1.1
		if (sc.length() > 0 && (status = sc.charAt(0)) == '1'
				&& con.getRequestVersion().endsWith("1.1")) {
			// tell client
			Looper l = new Looper();
			con.getCounter().inc("WebConnection got 1xx reply " + attempts);
			try {
				HttpHeaderSender hhs = new HttpHeaderSender(con.getChannel(),
						con.getNioHandler(), tlh.getClient(), header, false, l);
				hhs.sendHeader();
				return;
			} catch (IOException e) {
				failed(e);
			}
		}

		// since we have posted the full request we
		// loop while we get 100 (continue) response.
		if (status == '1') {
			readRequest();
		} else {
			String responseVersion = rh.getWebHeader().getResponseHTTPVersion();
			setAge(rh);
			WarningsHandler wh = new WarningsHandler();
			wh.removeWarnings(rh.getWebHeader(), false);
			rh.getWebHeader().addHeader("Via", responseVersion + " RabbIT");
			rh.setSize(dataSize);
			setupResource(wbh, isChunked, dataSize);
			con.webConnectionEstablished(rh);
		}
	}

	private void setupResource(BufferHandle wbh, boolean isChunked,
			long dataSize) {
		HttpProxy proxy = con.getProxy();
		ConnectionHandler ch = con.getProxy().getConnectionHandler();
		WebConnectionResourceSource rs = new WebConnectionResourceSource(ch,
				con.getNioHandler(), rh.getWebConnection(), wbh,
				tlh.getNetwork(), isChunked, dataSize, proxy.getStrictHttp());
		rh.setContent(rs);
	}

	public void closed() {
		if (rh.getWebConnection() != null) {
			closeDownWebConnection();
			lastException = new IOException("closed");
			establish();
		}
	}

	/**
	 * Calculate the age of the resource, needs ntp to be accurate.
	 * 
	 * @param rh
	 *            the RequestHandler holding the headers
	 */
	private void setAge(RequestHandler rh) {
		long now = System.currentTimeMillis();
		String age = rh.getWebHeader().getHeader("Age");
		String date = rh.getWebHeader().getHeader("Date");
		Date dd = HttpDateParser.getDate(date);
		long ddt = now;
		if (dd != null)
			ddt = dd.getTime();
		long lage = 0;
		try {
			if (age != null)
				lage = Long.parseLong(age);
			long dt = Math.max((now - ddt) / 1000, 0);
			// correct_age is found in spec, but is not actually used
			// long correct_age = lage + dt;
			long correct_recieved_age = Math.max(dt, lage);
			long corrected_initial_age = correct_recieved_age + dt;
			if (corrected_initial_age > 0) {
				rh.getWebHeader().setHeader("Age", "" + corrected_initial_age);
			}
		} catch (NumberFormatException e) {
			// if we cant parse it, we leave the Age header..
			logger.warning("Bad age: " + age);
		}
	}

	private class Looper implements HttpHeaderSentListener {

		public void httpHeaderSent() {
			// read the next request...
			readRequest();
		}

		public void timeout() {
			SWC.this.timeout();
		}

		public void failed(Exception e) {
			SWC.this.failed(e);
		}
	}

	private void closeDownWebConnection() {
		WebConnection wc = rh.getWebConnection();
		rh.setWebConnection(null);
		if (wc != null) {
			con.getNioHandler().close(wc.getChannel());
			Closer.close(wc, logger);
		}
	}

	public void timeout() {
		// retry
		lastException = new IOException("timeout");
		closeDownWebConnection();
		establish();
	}

	public void failed(Exception e) {
		lastException = e;
		con.getCounter().inc("WebConnections failed: " + attempts + ": " + e);
		closeDownWebConnection();
		// retry
		establish();
	}
}
