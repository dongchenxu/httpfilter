package com.googlecode.httpfilter.proxy.rabbit.zip;

import static com.googlecode.httpfilter.proxy.rabbit.zip.GZipFlags.*;

/**
 * GZipState for reading the gzip headers file name
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class NameReader implements GZipUnpackState {
	private final GZipUnpackListener listener;
	private final byte flag;

	public NameReader(GZipUnpackListener listener, byte flag) {
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
		// TODO: listener.name (someString);

		if ((flag & FCOMMENT) == FCOMMENT)
			useNewState(unpacker, new CommentReader(listener, flag), buf, off,
					len);
		if ((flag & FHCRC) == FHCRC)
			useNewState(unpacker, new HCRCReader(listener, flag), buf, off, len);
		useNewState(unpacker, new UnCompressor(listener, true), buf, off, len);
	}

	private void useNewState(GZipUnpacker unpacker, GZipUnpackState state,
			byte[] buf, int off, int len) {
		unpacker.setState(state);
		state.handleBuffer(unpacker, buf, off, len);
	}
}
