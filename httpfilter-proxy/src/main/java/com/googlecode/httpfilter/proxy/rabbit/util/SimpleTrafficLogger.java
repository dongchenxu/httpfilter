package com.googlecode.httpfilter.proxy.rabbit.util;

/**
 * A class to track of data flows.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SimpleTrafficLogger implements TrafficLogger {
	private long read;
	private long written;
	private long transferFrom;
	private long transferTo;

	public void read(long read) {
		this.read += read;
	}

	public long read() {
		return read;
	}

	public void write(long written) {
		this.written += written;
	}

	public long write() {
		return written;
	}

	public void transferFrom(long transferred) {
		this.transferFrom += transferred;
	}

	public long transferFrom() {
		return transferFrom;
	}

	public void transferTo(long transferred) {
		this.transferTo += transferred;
	}

	public long transferTo() {
		return transferTo;
	}

	public void clear() {
		read = 0;
		written = 0;
		transferFrom = 0;
		transferTo = 0;
	}

	public void addTo(TrafficLogger other) {
		other.read(read);
		other.write(written);
		other.transferFrom(transferFrom);
		other.transferTo(transferTo);
	}
}
