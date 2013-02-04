package com.googlecode.httpfilter.proxy.rabbit.httpio;

import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;

/**
 * A listener for http header sent + read.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface HttpResponseListener extends AsyncListener {
	/**
	 * The http header has been sent.
	 * 
	 * @param response
	 *            the HttpHeader that was read
	 * @param bufferHandle
	 *            the BufferHandle that may or may not hold unread data.
	 * @param keepalive
	 *            if the sender want to use keepalive.
	 * @param isChunked
	 *            if false content is not chunked, if true content is chunked.
	 * @param dataSize
	 *            the contents size or -1 if size is unknown.
	 */
	void httpResponse(HttpHeader response, BufferHandle bufferHandle,
			boolean keepalive, boolean isChunked, long dataSize);

}
