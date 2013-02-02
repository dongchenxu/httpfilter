package com.googlecode.httpfilter.proxy.rabbit.webserver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.impl.Closer;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpDateParser;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.FileResourceSource;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderReader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderSender;
import com.googlecode.httpfilter.proxy.rabbit.httpio.HttpHeaderSentListener;
import com.googlecode.httpfilter.proxy.rabbit.httpio.ResourceSource;
import com.googlecode.httpfilter.proxy.rabbit.httpio.TransferHandler;
import com.googlecode.httpfilter.proxy.rabbit.httpio.TransferListener;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.CacheBufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.util.MimeTypeMapper;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A connection to a web client.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Connection {
	private final SimpleWebServer sws;
	private final SocketChannel sc;
	private BufferHandle clientBufferHandle;
	private boolean timeToClose = false;
	private ResourceSource resourceSource = null;

	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Create a new Connection for the given web server and socket channel.
	 * 
	 * @param sws
	 *            the web server
	 * @param sc
	 *            the channel for the request
	 */
	public Connection(SimpleWebServer sws, SocketChannel sc) {
		this.sws = sws;
		this.sc = sc;
	}

	/**
	 * Set up a http reader to listen for http request.
	 * 
	 * @throws IOException
	 *             if reading the request fails
	 */
	public void readRequest() throws IOException {
		if (clientBufferHandle == null)
			clientBufferHandle = new CacheBufferHandle(sws.getBufferHandler());
		HttpHeaderListener requestListener = new RequestListener();
		HttpHeaderReader requestReader = new HttpHeaderReader(sc,
				clientBufferHandle, sws.getNioHandler(),
				sws.getTrafficLogger(), true, true, requestListener);
		requestReader.readHeader();
	}

	private void shutdown() {
		Closer.close(sc, logger);
	}

	private void handleRequest(HttpHeader header) {
		String method = header.getMethod();
		if ("GET".equals(method) || "HEAD".equals(method)) {
			String path = header.getRequestURI();
			if (path == null || "".equals(path)) {
				badRequest();
				return;
			}
			try {
				if (!path.startsWith("/")) {
					URL u = new URL(path);
					path = u.getFile();
				}
				if (path.endsWith("/"))
					path += "index.html";
				path = path.substring(1);
				File f = new File(sws.getBaseDir(), path);
				f = f.getCanonicalFile();
				if (isSafe(f) && f.exists() && f.isFile()) {
					HttpHeader resp = getHeader("HTTP/1.1 200 Ok");
					String type = MimeTypeMapper.getMimeType(f
							.getAbsolutePath());
					if (type != null)
						resp.setHeader("Content-Type", type);
					resp.setHeader("Content-Length", Long.toString(f.length()));
					Date d = new Date(f.lastModified());
					resp.setHeader("Last-Modified",
							HttpDateParser.getDateString(d));
					if ("HTTP/1.0".equals(header.getHTTPVersion()))
						resp.setHeader("Connection", "Keep-Alive");

					if (logger.isLoggable(Level.FINEST))
						logger.finest("Connection; http response: " + resp);

					if ("GET".equals(method))
						resourceSource = new FileResourceSource(f,
								sws.getNioHandler(), sws.getBufferHandler());
					sendResponse(resp);
				} else {
					notFound();
				}
			} catch (IOException e) {
				internalError();
			}
		} else {
			methodNotAllowed();
		}
	}

	private boolean isSafe(File f) throws IOException {
		File dir = sws.getBaseDir();
		return f.getCanonicalPath().startsWith(dir.getCanonicalPath());
	}

	private void notFound() {
		sendResponse(getHeader("HTTP/1.1 404 Not Found"));
	}

	private void badRequest() {
		sendBadResponse(getHeader("HTTP/1.1 400 Bad Request"));
	}

	private void methodNotAllowed() {
		sendBadResponse(getHeader("HTTP/1.1 405 Method Not Allowed"));
	}

	private void internalError() {
		sendBadResponse(getHeader("HTTP/1.1 500 Internal Error"));
	}

	private void notImplemented() {
		sendBadResponse(getHeader("HTTP/1.1 501 Not Implemented"));
	}

	private void sendBadResponse(HttpHeader response) {
		response.setHeader("Content-type", "text/html");
		timeToClose = true;
		sendResponse(response);
	}

	private void sendResponse(HttpHeader response) {
		try {
			ResponseSentListener sentListener = new ResponseSentListener();
			HttpHeaderSender sender = new HttpHeaderSender(sc,
					sws.getNioHandler(), sws.getTrafficLogger(), response,
					false, sentListener);
			sender.sendHeader();
		} catch (IOException e) {
			shutdown();
		}
	}

	private void sendResource() {
		TransferListener transferDoneListener = new TransferDoneListener();
		TrafficLogger tl = sws.getTrafficLogger();
		NioHandler nh = sws.getNioHandler();
		TransferHandler th = new TransferHandler(nh, resourceSource, sc, tl,
				tl, transferDoneListener);
		th.transfer();
	}

	private HttpHeader getHeader(String statusLine) {
		HttpHeader ret = new HttpHeader();
		ret.setStatusLine(statusLine);
		ret.setHeader("Server", sws.getClass().getName());
		ret.setHeader("Date", HttpDateParser.getDateString(new Date()));
		return ret;
	}

	private void closeOrContinue() {
		if (timeToClose) {
			shutdown();
		} else {
			try {
				readRequest();
			} catch (IOException e) {
				shutdown();
			}
		}
	}

	private class AsyncBaseListener {
		public void timeout() {
			shutdown();
		}

		public void failed(Exception e) {
			shutdown();
		}
	}

	private class RequestListener extends AsyncBaseListener implements
			HttpHeaderListener {
		public void httpHeaderRead(final HttpHeader header, BufferHandle bh,
				boolean keepalive, boolean isChunked, long dataSize) {
			bh.possiblyFlush();
			if (isChunked || dataSize > 0)
				notImplemented();
			if (!keepalive)
				timeToClose = true;
			handleRequest(header);
		}

		public void closed() {
			shutdown();
		}
	}

	private class ResponseSentListener extends AsyncBaseListener implements
			HttpHeaderSentListener {
		public void httpHeaderSent() {
			if (resourceSource != null)
				sendResource();
			else
				closeOrContinue();
		}
	}

	private class TransferDoneListener extends AsyncBaseListener implements
			TransferListener {
		public void transferOk() {
			resourceSource.release();
			closeOrContinue();
		}
	}
}
