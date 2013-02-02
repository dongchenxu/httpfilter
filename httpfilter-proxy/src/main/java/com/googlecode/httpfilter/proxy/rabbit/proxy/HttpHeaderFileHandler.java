package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.googlecode.httpfilter.proxy.rabbit.cache.ncache.FileHandler;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;

/**
 * A FileHandler for HttpHeader
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HttpHeaderFileHandler implements FileHandler<HttpHeader> {
	public HttpHeader read(InputStream is) throws IOException {
		DataInputStream dos = new DataInputStream(is);
		HttpHeader h = new HttpHeader();
		h.read(dos);
		return h;
	}

	public void write(OutputStream os, HttpHeader t) throws IOException {
		DataOutputStream dos = new DataOutputStream(os);
		t.write(dos);
	}
}
