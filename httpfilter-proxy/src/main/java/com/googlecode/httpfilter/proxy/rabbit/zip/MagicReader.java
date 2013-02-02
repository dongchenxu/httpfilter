package com.googlecode.httpfilter.proxy.rabbit.zip;

/**
 * The starting state of gzip unpacking.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class MagicReader implements GZipUnpackState {
	private final GZipUnpackListener listener;

	private int pos = 0;
	private final byte[] GZIP_ID = { 0x1f, (byte) 0x8b };

	public MagicReader(GZipUnpackListener listener) {
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
		while (len > 0 && pos < GZIP_ID.length) {
			if (buf[off] != GZIP_ID[pos]) {
				Exception e = new IllegalArgumentException(
						"gzip header not found: " + pos + ", " + buf[off]
								+ " != " + GZIP_ID[pos]);
				listener.failed(e);
			}
			pos++;
			off++;
			len--;
		}
		if (pos == GZIP_ID.length) {
			GZipUnpackState cmr = new CompressionMethodReader(listener);
			unpacker.setState(cmr);
			cmr.handleBuffer(unpacker, buf, off, len);
		}
	}
}
