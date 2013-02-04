package com.googlecode.httpfilter.proxy.rabbit.httpio;

import java.nio.ByteBuffer;

/**
 * A class that reads lines from a ByteBuffer.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class LineReader {
	private final boolean strictHttp;

	/**
	 * Create a new LineReader.
	 * <p>
	 * There are many web servers on the internet that still use broken HTTP and
	 * send simple \n\n for end of header. Be careful of how you set the strict
	 * flag.
	 * 
	 * @param strictHttp
	 *            if true only CRLF will be accepted as newlines, if false
	 *            single LF will also be accepted.
	 */
	public LineReader(boolean strictHttp) {
		this.strictHttp = strictHttp;
	}

	/**
	 * Try to read a line. If a line ending is found the buffers mark is set to
	 * the next position.
	 * 
	 * @param buffer
	 *            the ByteBuffer to read bytes from.
	 * @param listener
	 *            the line listener.
	 */
	public void readLine(ByteBuffer buffer, LineListener listener) {
		StringBuilder sb = new StringBuilder(200);
		int l = -1;
		boolean lineEnding = false;
		while (buffer.hasRemaining()) {
			byte c = buffer.get();
			if (c == '\n') {
				if (l == '\r') {
					lineEnding = true;
					sb.setLength(sb.length() - 1);
				}
				if (lineEnding || !strictHttp) {
					buffer.mark();
					listener.lineRead(sb.toString());
					return;
				}
			}
			sb.append((char) c);
			l = c;
		}
	}
}
