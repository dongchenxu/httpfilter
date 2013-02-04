package com.googlecode.httpfilter.proxy.rabbit.zip;

/**
 * GZipState for validating the compression method.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class CompressionMethodReader implements GZipUnpackState {
	private final GZipUnpackListener listener;
	private static final int GZIP_DEFLATE = 8;

	public CompressionMethodReader(GZipUnpackListener listener) {
		this.listener = listener;
	}

	public void handleCurrentData(GZipUnpacker unpacker) {
		throw new IllegalStateException("need more input");
	}

	public boolean needsInput() {
		return true;
	}

	public void handleBuffer(GZipUnpacker unpacker, byte[] buf, int off, int len) {
		if (len <= 0)
			return;
		byte b = buf[off];
		if (b != GZIP_DEFLATE) {
			String err = "unknown compression method: " + b;
			Exception e = new IllegalArgumentException(err);
			listener.failed(e);
		}
		GZipUnpackState fr = new FlagReader(listener);
		unpacker.setState(fr);
		fr.handleBuffer(unpacker, buf, off + 1, len - 1);
	}
}
