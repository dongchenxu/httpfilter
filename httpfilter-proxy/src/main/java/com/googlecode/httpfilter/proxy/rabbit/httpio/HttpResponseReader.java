package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.BufferHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.CacheBufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A handler that write one http header and reads a response
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HttpResponseReader implements HttpHeaderSentListener,
		HttpHeaderListener {

	private final SocketChannel channel;
	private final NioHandler nioHandler;
	private final TrafficLogger tl;
	private final BufferHandler bufHandler;
	private final boolean strictHttp;
	private final HttpResponseListener listener;
	private final HttpHeaderSender sender;

	/**
	 * Create a new HttpResponseReader.
	 * 
	 * @param channel
	 *            the Channel to the client
	 * @param nioHandler
	 *            the NioHandler to use for network and background tasks
	 * @param tl
	 *            the network statistics gatherer
	 * @param bufHandler
	 *            the BufferHandler to use
	 * @param header
	 *            the request to send
	 * @param fullURI
	 *            if true the request will have a full uri instead of just a
	 *            relative one
	 * @param strictHttp
	 *            if true then use strict http
	 * @param listener
	 *            the listener that will be notified when the response has been
	 *            read.
	 * @throws IOException
	 *             if the request can not be sent
	 */
	public HttpResponseReader(SocketChannel channel, NioHandler nioHandler,
			TrafficLogger tl, BufferHandler bufHandler, HttpHeader header,
			boolean fullURI, boolean strictHttp, HttpResponseListener listener)
			throws IOException {
		this.channel = channel;
		this.nioHandler = nioHandler;
		this.tl = tl;
		this.bufHandler = bufHandler;
		this.strictHttp = strictHttp;
		this.listener = listener;
		sender = new HttpHeaderSender(channel, nioHandler, tl, header, fullURI,
				this);
	}

	/**
	 * Start the process of sending the header and reading the response.
	 */
	public void sendRequestAndWaitForResponse() {
		sender.sendHeader();
	}

	public void httpHeaderSent() {
		try {
			BufferHandle bh = new CacheBufferHandle(bufHandler);
			HttpHeaderReader reader = new HttpHeaderReader(channel, bh,
					nioHandler, tl, false, strictHttp, this);
			reader.readHeader();
		} catch (IOException e) {
			failed(e);
		}
	}

	public void httpHeaderRead(HttpHeader header, BufferHandle bh,
			boolean keepalive, boolean isChunked, long dataSize) {
		listener.httpResponse(header, bh, keepalive, isChunked, dataSize);
	}

	public void closed() {
		listener.failed(new IOException("Connection closed"));
	}

	public void failed(Exception cause) {
		listener.failed(cause);
	}

	public void timeout() {
		listener.timeout();
	}
}
