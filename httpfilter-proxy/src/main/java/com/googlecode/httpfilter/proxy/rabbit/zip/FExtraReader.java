package com.googlecode.httpfilter.proxy.rabbit.zip;

import static com.googlecode.httpfilter.proxy.rabbit.zip.GZipFlags.*;

/**
 * GZipState for reading the gzip flags
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class FExtraReader implements GZipUnpackState {
	private final GZipUnpackListener listener;
	private final byte flag;
	private final byte[] xlen = new byte[2];
	private int pos = 0;
	private int toSkip = -1;

	public FExtraReader(GZipUnpackListener listener, byte flag) {
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

		if (toSkip == -1) {
			while (len > 0 && pos < xlen.length) {
				xlen[pos++] = buf[off++];
				len--;
			}

			if (pos <= xlen.length)
				return;

			toSkip = (xlen[0] & 0xff) | ((xlen[1] << 8) & 0xff00);
		}

		// TODO: listener.fextra (fextraData);
		while (toSkip > 0) {
			if (len < toSkip) {
				toSkip -= len;
				return;
			}

			len -= toSkip;
			off += toSkip;
		}

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
