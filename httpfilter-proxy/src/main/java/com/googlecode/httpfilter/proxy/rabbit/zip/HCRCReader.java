package com.googlecode.httpfilter.proxy.rabbit.zip;

/**
 * GZipState for reading the gzip headers crc
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class HCRCReader implements GZipUnpackState {
	private final GZipUnpackListener listener;
	private final byte flag;
	private int pos = 0;
	private final byte[] crc = new byte[2];

	public HCRCReader(GZipUnpackListener listener, byte flag) {
		this.listener = listener;
		this.flag = flag;
	}

	public void handleCurrentData(GZipUnpacker unpacker) {
		throw new IllegalStateException("need more input");
	}

	public boolean needsInput() {
		return true;
	}

	public byte getFlag() {
		return flag;
	}

	public void handleBuffer(GZipUnpacker unpacker, byte[] buf, int off, int len) {
		if (len <= 0)
			return;

		while (len > 0 && pos < crc.length) {
			crc[pos++] = buf[off++];
			len--;
		}

		if (pos < crc.length)
			return;

		// TODO: how to validate the crc?

		GZipUnpackState uc = new UnCompressor(listener, true);
		unpacker.setState(uc);
		uc.handleBuffer(unpacker, buf, off, len);
	}
}
