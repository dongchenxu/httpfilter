package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.ReadHandler;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.util.TrafficLogger;

/**
 * A handler that reads http headers
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HttpHeaderReader extends BaseSocketHandler implements ReadHandler {

	private final HttpHeaderListener reader;
	private final HttpHeaderParser headerParser;

	// State variables.
	private boolean keepalive = true;
	private boolean ischunked = false;
	private long dataSize = -1; // -1 for unknown.
	private int startParseAt = 0;

	private final TrafficLogger tl;

	/**
	 * @param channel
	 *            the SocketChannel to read from
	 * @param bh
	 *            the BufferHandle to use to get ByteBuffers
	 * @param nioHandler
	 *            the NioHandler to use to wait for more data
	 * @param tl
	 *            the TrafficLogger to update with network read Statistics
	 * @param request
	 *            true if a request is read, false if a response is read.
	 *            Servers may respond without header (HTTP/0.9) so try to handle
	 *            that.
	 * @param strictHttp
	 *            if true http headers will be strictly parsed, if false http
	 *            newlines may be single \n
	 * @param reader
	 *            the listener for http headers
	 */
	public HttpHeaderReader(SocketChannel channel, BufferHandle bh,
			NioHandler nioHandler, TrafficLogger tl, boolean request,
			boolean strictHttp, HttpHeaderListener reader) {
		super(channel, bh, nioHandler);
		this.tl = tl;
		headerParser = new HttpHeaderParser(request, strictHttp);
		this.reader = reader;
	}

	/**
	 * Try to read a http header
	 * 
	 * @throws IOException
	 *             if a header can not be parsed
	 */
	public void readHeader() throws IOException {
		headerParser.reset();
		if (!getBufferHandle().isEmpty()) {
			ByteBuffer buffer = getBuffer();
			startParseAt = buffer.position();
			parseBuffer(buffer);
		} else {
			releaseBuffer();
			waitForRead(this);
		}
	}

	@Override
	public String getDescription() {
		HttpHeader header = headerParser.getHeader();
		return "HttpHeaderReader: channel: " + getChannel()
				+ ", current header lines: "
				+ (header == null ? 0 : header.size());
	}

	@Override
	public void closed() {
		releaseBuffer();
		reader.closed();
	}

	@Override
	public void timeout() {
		// If buffer exists it only holds a partial http header.
		// We relase the buffer and discard that partial header.
		releaseBuffer();
		reader.timeout();
	}

	public void read() {
		Logger logger = getLogger();
		logger.finest("HttpHeaderReader reading data");
		try {
			// read http request
			// make sure we have room for reading.
			ByteBuffer buffer = getBuffer();
			int dataLimit = buffer.limit();
			if (dataLimit == buffer.capacity())
				dataLimit = buffer.position();
			buffer.limit(buffer.capacity());
			buffer.position(dataLimit);
			int read = getChannel().read(buffer);
			if (read == -1) {
				buffer.position(buffer.limit());
				closeDown();
				reader.closed();
				return;
			}
			if (read == 0) {
				closeDown();
				reader.failed(new IOException("read 0 bytes, shutting "
						+ "down connection"));
				return;
			}
			tl.read(read);
			buffer.position(startParseAt);
			buffer.limit(read + dataLimit);
			parseBuffer(buffer);
		} catch (BadHttpHeaderException e) {
			closeDown();
			reader.failed(e);
		} catch (IOException e) {
			closeDown();
			reader.failed(e);
		}
	}

	private void parseBuffer(ByteBuffer buffer) throws IOException {
		int startPos = buffer.position();
		buffer.mark();
		boolean done = headerParser.handleBuffer(buffer);
		Logger logger = getLogger();
		if (logger.isLoggable(Level.FINEST))
			logger.finest("HttpHeaderReader.parseBuffer: done " + done);
		if (!done) {
			int pos = buffer.position();
			buffer.reset();
			if (buffer.position() > 0) {
				// ok, some data handled, make space for more.
				buffer.compact();
				startParseAt = 0;
			} else {
				// ok, we did not make any progress, did we only read
				// a partial long line (cookie or whatever).
				if (buffer.limit() < buffer.capacity()) {
					// try to read some more
				} else if (isUsingSmallBuffer(buffer)) {
					// try to expand buffer
					buffer = getLargeBuffer();
					buffer.position(pos - startPos);
					startParseAt = 0;
				} else {
					releaseBuffer();
					// ok, we did no progress, abort, client is sending
					// too long lines.
					throw new RequestLineTooLongException();
				}
			}
			waitForRead(this);
		} else {
			HttpHeader header = headerParser.getHeader();
			setState(header);
			releaseBuffer();
			reader.httpHeaderRead(header, getBufferHandle(), keepalive,
					ischunked, dataSize);
		}
	}

	private void setState(HttpHeader header) {
		dataSize = -1;
		String cl = header.getHeader("Content-Length");
		if (cl != null) {
			try {
				dataSize = Long.parseLong(cl);
			} catch (NumberFormatException e) {
				dataSize = -1;
			}
		}
		String con = header.getHeader("Connection");
		// Netscape specific header...
		String pcon = header.getHeader("Proxy-Connection");
		if (con != null && con.equalsIgnoreCase("close"))
			setKeepAlive(false);
		if (keepalive && pcon != null && pcon.equalsIgnoreCase("close"))
			setKeepAlive(false);

		if (header.isResponse()) {
			if (header.getResponseHTTPVersion().equals("HTTP/1.1")) {
				String chunked = header.getHeader("Transfer-Encoding");
				setKeepAlive(true);
				ischunked = false;

				if (chunked != null && chunked.equalsIgnoreCase("chunked")) {
					/*
					 * If we handle chunked data we must read the whole page
					 * before continuing, since the chunk footer must be
					 * appended to the header (read the RFC)...
					 * 
					 * As of RFC 2616 this is not true anymore... this means
					 * that we throw away footers and it is legal.
					 */
					ischunked = true;
					header.removeHeader("Content-Length");
					dataSize = -1;
				}
			} else {
				setKeepAlive(false);
			}

			if (!(dataSize > -1 || ischunked))
				setKeepAlive(false);
		} else {
			String httpVersion = header.getHTTPVersion();
			if (httpVersion != null) {
				if (httpVersion.equals("HTTP/1.1")) {
					String chunked = header.getHeader("Transfer-Encoding");
					if (chunked != null && chunked.equalsIgnoreCase("chunked")) {
						ischunked = true;
						header.removeHeader("Content-Length");
						dataSize = -1;
					}
				} else if (httpVersion.equals("HTTP/1.0")) {
					String ka = header.getHeader("Connection");
					if (ka == null || !ka.equalsIgnoreCase("Keep-Alive"))
						setKeepAlive(false);
				}
			}
		}
	}

	/**
	 * Set the keep alive value to currentkeepalive & keepalive
	 * 
	 * @param keepalive
	 *            the new keepalive value.
	 */
	private void setKeepAlive(boolean keepalive) {
		this.keepalive = (this.keepalive && keepalive);
	}
}
