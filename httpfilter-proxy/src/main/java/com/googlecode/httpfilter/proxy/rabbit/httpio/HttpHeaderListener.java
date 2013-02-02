package com.googlecode.httpfilter.proxy.rabbit.httpio;

import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;

/**
 * A listener for http headers.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface HttpHeaderListener extends AsyncListener {
	/**
	 * One http header has been read
	 * 
	 * @param header
	 *            the HttpHeader that was read
	 * @param bh
	 *            the BufferHandle that may or may not hold unread data.
	 * @param keepalive
	 *            if the sender want to use keepalive.
	 * @param isChunked
	 *            if false content is not chunked, if true content is chunked.
	 * @param dataSize
	 *            the contents size or -1 if size is unknown.
	 */
	void httpHeaderRead(HttpHeader header, BufferHandle bh, boolean keepalive,
			boolean isChunked, long dataSize);

	/**
	 * The socket connection has been closed, either by this end or the other
	 * side. Quite common on persistent connections.
	 */
	void closed();
}
