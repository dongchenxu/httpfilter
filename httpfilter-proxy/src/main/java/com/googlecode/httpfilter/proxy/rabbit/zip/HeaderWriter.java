package com.googlecode.httpfilter.proxy.rabbit.zip;

import java.util.zip.Deflater;

/**
 * The starting state of gzip packing.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class HeaderWriter implements GZipPackState {
	private final GZipPackListener listener;

	public HeaderWriter(GZipPackListener listener) {
		this.listener = listener;
	}

	public boolean needsInput() {
		return false;
	}

	public void handleBuffer(GZipPacker packer, byte[] buf, int off, int len) {
		throw new IllegalStateException("Does not need input");
	}

	private final static byte[] header = { (byte) 0x1f, (byte) 0x8b,
			Deflater.DEFLATED, 0, 0, 0, 0, 0, 0, 0 };

	public void handleCurrentData(GZipPacker packer) {
		GZipPackState c = new Compressor(listener);
		packer.setState(c);
		listener.packed(header, 0, header.length);
	}

	public void finish() {
		throw new IllegalStateException("Can not finish");
	}

	public boolean finished() {
		return false;
	}
}
