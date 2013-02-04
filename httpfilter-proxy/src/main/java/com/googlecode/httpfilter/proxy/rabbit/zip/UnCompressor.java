package com.googlecode.httpfilter.proxy.rabbit.zip;

import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * GZipState for uncompressing the gzip or deflate data
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class UnCompressor implements GZipUnpackState {
	private final GZipUnpackListener listener;
	private final Inflater inf;
	private CRC32 crc;
	private byte[] buf;
	private int off;
	private int len;
	private final boolean gzip;

	/**
	 * @param listener
	 *            the data listener
	 * @param gzip
	 *            if true use gzip handling with crc and trailers, if false use
	 *            simple deflate handling.
	 */
	public UnCompressor(GZipUnpackListener listener, boolean gzip) {
		this.listener = listener;
		inf = new Inflater(true);
		this.gzip = gzip;
		if (gzip)
			crc = new CRC32();
	}

	public void handleCurrentData(GZipUnpacker unpacker) {
		byte[] unpacked = listener.getBuffer();
		int num;
		try {
			if ((num = inf.inflate(unpacked)) > 0) {
				if (gzip)
					crc.update(unpacked, 0, num);
				listener.unpacked(unpacked, 0, num);
			}
		} catch (DataFormatException e) {
			listener.failed(e);
			return;
		}
		if (num == 0) {
			// this path is not allways followed (especially deflate + chunk)
			// ChunkHandler 56 need to call handleChunkData a last time to
			// wrap up or listener.finishedRead (); /rick
			if (inf.finished() || inf.needsDictionary()) {
				GZipUnpackState tr;
				int remaining = inf.getRemaining();
				if (gzip) {
					long length = inf.getBytesWritten();
					long cs = crc.getValue();
					tr = new TrailReader(listener, length, cs);
				} else {
					tr = new AfterEndState();
				}
				inf.end();
				unpacker.setState(tr);
				if (remaining > 0)
					tr.handleBuffer(unpacker, buf, off + len - remaining,
							remaining);
			}
		}
	}

	public boolean needsInput() {
		return inf.needsInput();
	}

	public void handleBuffer(GZipUnpacker unpacker, byte[] buf, int off, int len) {
		inf.setInput(buf, off, len);
		this.buf = buf;
		this.off = off;
		this.len = len;
		if (len > 0)
			handleCurrentData(unpacker);
	}
}
