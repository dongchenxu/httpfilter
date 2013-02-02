package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.io.BufferHandle;
import com.googlecode.httpfilter.proxy.rabbit.proxy.ClientResourceHandler;
import com.googlecode.httpfilter.proxy.rabbit.proxy.ClientResourceListener;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * This is a class that prints the Http headers on the standard out stream.
 */
public class HttpSnoop implements HttpFilter {
	private enum SnoopMode {
		NORMAL, REQUEST_LINE, FULL
	}

	private SnoopMode mode;

	public HttpHeader doHttpInFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		if (mode == SnoopMode.REQUEST_LINE) {
			System.out.println(con.getRequestLine());
		} else {
			System.out.print(header.toString());
			if (mode == SnoopMode.FULL) {
				ClientResourceHandler crh = con.getClientResourceHandler();
				if (crh != null)
					crh.addContentListener(new ContentLogger(header));
			}
		}
		return null;
	}

	private static class ContentLogger implements ClientResourceListener {
		private final HttpHeader header;

		public ContentLogger(HttpHeader header) {
			this.header = header;
		}

		public void resourceDataRead(BufferHandle bufHandle) {
			ByteBuffer buf = bufHandle.getBuffer();
			buf = buf.duplicate();
			byte[] data = new byte[buf.remaining()];
			buf.get(data);
			try {
				// TODO: better handling of charset,
				// TODO: for now we use latin-1 since that has no
				// TOdO: invalid characters.
				String s = new String(data, "ISO-8859-1");
				System.out.println(header.getRequestLine()
						+ " request content:\n" + s);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Failed to get charset", e);
			}
		}
	}

	public HttpHeader doHttpOutFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		if (mode == SnoopMode.REQUEST_LINE) {
			System.out.println(con.getRequestLine() + "\n"
					+ header.getStatusLine());
		} else {
			System.out.print(con.getRequestLine() + "\n" + header.toString());
			if (mode == SnoopMode.FULL) {
				ClientResourceHandler crh = con.getClientResourceHandler();
				if (crh != null)
					crh.addContentListener(new ContentLogger(header));
			}
		}
		return null;
	}

	public HttpHeader doConnectFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		if (mode == SnoopMode.REQUEST_LINE) {
			System.out.println(con.getRequestLine());
		} else {
			System.out.print(header.toString());
		}
		return null;
	}

	/**
	 * Setup this class with the given properties.
	 * 
	 * @param properties
	 *            the new configuration of this class.
	 */
	public void setup(SProperties properties, HttpProxy proxy) {
		String smo = properties.getProperty("mode", "NORMAL");
		smo = smo.toUpperCase();
		mode = SnoopMode.valueOf(smo);
	}
}
