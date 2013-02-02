package com.googlecode.httpfilter.proxy.rabbit.zip;

/**
 * GZipUnpackState after unpacking has been performed.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class AfterEndState implements GZipUnpackState {
	public void handleBuffer(GZipUnpacker unpacker, byte[] buf, int off, int len) {
		throw new IllegalStateException("gzip handling is already finished");
	}

	public void handleCurrentData(GZipUnpacker unpacker) {
		throw new IllegalStateException("gzip handling is already finished");
	}

	public boolean needsInput() {
		return false;
	}
}
