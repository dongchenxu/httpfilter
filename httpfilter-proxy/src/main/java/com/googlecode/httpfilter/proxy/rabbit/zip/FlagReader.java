package com.googlecode.httpfilter.proxy.rabbit.zip;

import static com.googlecode.httpfilter.proxy.rabbit.zip.GZipFlags.*;

/**
 * GZipState for reading the gzip flags
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class FlagReader implements GZipUnpackState {
	private final GZipUnpackListener listener;
	private int pos = 0;
	private byte flag = -1;
	private final byte[] flags = new byte[6];

	public FlagReader(GZipUnpackListener listener) {
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

		if (flag == -1) {
			flag = buf[off++];
			len--;
		}
		while (len > 0 && pos < flags.length) {
			flags[pos++] = buf[off++];
			len--;
		}
		if (pos < flags.length)
			return;

		/*
		 * at the moment we do not care about mtime or xfl or os, but if we did
		 * we would find the data as
		 */
		/*
		 * int mtime = (flags[3] << 24) | (flags[2] << 16) | (flags[1] << 8) |
		 * flags[0]; byte xfl = flags[4]; byte os = flags[5];
		 */

		if ((flag & FEXTRA) == FEXTRA)
			useNewState(unpacker, new FExtraReader(listener, flag), buf, off,
					len);

		if ((flag & FNAME) == FNAME)
			useNewState(unpacker, new NameReader(listener, flag), buf, off, len);

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
