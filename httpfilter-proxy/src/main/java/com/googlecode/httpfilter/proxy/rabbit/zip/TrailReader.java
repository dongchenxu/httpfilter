package com.googlecode.httpfilter.proxy.rabbit.zip;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * GZipState for reading the gzip trailer
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class TrailReader implements GZipUnpackState {
	private final GZipUnpackListener listener;
	private final long bytesUnpacked;
	private final long crc;
	private int pos = 0;
	private final byte[] data = new byte[8];

	public TrailReader(GZipUnpackListener listener, long bytesUnpacked, long crc) {
		this.listener = listener;
		this.bytesUnpacked = bytesUnpacked;
		this.crc = crc;
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

		while (len > 0 && pos < data.length) {
			data[pos++] = buf[off++];
			len--;
		}
		if (pos < data.length)
			return;

		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		int crc = bb.getInt();
		if ((int) this.crc != crc)
			throw new IllegalStateException("crc does not match: " + crc
					+ " != " + (int) this.crc);

		int isize = bb.getInt();
		if (isize != (bytesUnpacked & 0xffffffffL))
			throw new IllegalStateException("isize does not match; " + isize
					+ " != " + bytesUnpacked);
		unpacker.setState(new AfterEndState());
		listener.finished();
	}
}
