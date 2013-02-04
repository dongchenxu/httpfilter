package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * A writer for log files.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class LogWriter extends PrintWriter {
	private final OutputStream os;

	public LogWriter(OutputStream os, boolean autoFlush) {
		super(os, autoFlush);
		this.os = os;
	}

	public LogWriter(Writer w, boolean autoFlush) {
		super(w, autoFlush);
		os = null;
	}

	public boolean isSystemWriter() {
		return (os == System.out || os == System.err);
	}
}
