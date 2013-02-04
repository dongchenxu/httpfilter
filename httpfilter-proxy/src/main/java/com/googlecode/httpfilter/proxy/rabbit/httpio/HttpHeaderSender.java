package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.io.SimpleBufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A handler that writes http headers
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HttpHeaderSender implements BlockSentListener {
	private final boolean fullURI;
	private final HttpHeaderSentListener sender;
	private final BlockSender bs;

	/**
	 * @param channel
	 *            the SocketChannel to write the header to
	 * @param nioHandler
	 *            the NioHandler to use to wait for write ready
	 * @param tl
	 *            the statics gatherer to use
	 * @param header
	 *            the HttpHeader to send
	 * @param fullURI
	 *            if false then try to change header.uri into just the file
	 * @param sender
	 *            the listener that will be notified when the header has been
	 *            sent (or sending has failed
	 * @throws IOException
	 *             if the header can not be converted to network data
	 */
	public HttpHeaderSender(SocketChannel channel, NioHandler nioHandler,
			TrafficLogger tl, HttpHeader header, boolean fullURI,
			HttpHeaderSentListener sender) throws IOException {
		this.fullURI = fullURI;
		this.sender = sender;
		BufferHandle bh = new SimpleBufferHandle(getBuffer(header));
		bs = new BlockSender(channel, nioHandler, tl, bh, false, this);
	}

	/**
	 * Send the header
	 */
	public void sendHeader() {
		bs.write();
	}

	private ByteBuffer getBuffer(HttpHeader header) throws IOException {
		String uri = header.getRequestURI();
		try {
			if (header.isRequest() && !header.isSecure() && !fullURI
					&& uri.charAt(0) != '/') {
				URL url = new URL(uri);
				String file = url.getFile();
				if (file.equals(""))
					file = "/";
				header.setRequestURI(file);
			}
			byte[] bytes = header.getBytes();
			return ByteBuffer.wrap(bytes);
		} finally {
			header.setRequestURI(uri);
		}
	}

	public void timeout() {
		sender.timeout();
	}

	public void failed(Exception cause) {
		sender.failed(cause);
	}

	public void blockSent() {
		sender.httpHeaderSent();
	}
}
