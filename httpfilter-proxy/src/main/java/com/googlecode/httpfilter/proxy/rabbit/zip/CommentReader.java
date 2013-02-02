package com.googlecode.httpfilter.proxy.rabbit.zip;

import static com.googlecode.httpfilter.proxy.rabbit.zip.GZipFlags.*;

/**
 * GZipState for reading the gzip headers comment
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class CommentReader implements GZipUnpackState {
	private final GZipUnpackListener listener;
	private final byte flag;

	public CommentReader(GZipUnpackListener listener, byte flag) {
		this.listener = listener;
		this.flag = flag;
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

		byte b = -1;
		while (len > 0 && (b = buf[off++]) != 0)
			len--;
		if (b != 0)
			return;
		// TODO: listener.comment (someString);

		if ((flag & FHCRC) == FHCRC) {
			GZipUnpackState crc = new HCRCReader(listener, flag);
			unpacker.setState(crc);
			crc.handleBuffer(unpacker, buf, off, len);
		}
		GZipUnpackState uc = new UnCompressor(listener, true);
		unpacker.setState(uc);
		uc.handleBuffer(unpacker, buf, off, len);
	}
}
