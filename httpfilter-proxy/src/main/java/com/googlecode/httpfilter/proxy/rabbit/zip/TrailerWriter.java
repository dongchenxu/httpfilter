package com.googlecode.httpfilter.proxy.rabbit.zip;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * The ending state of gzip packing.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class TrailerWriter implements GZipPackState {
	private final GZipPackListener listener;
	private final int crc;
	private final int totalIn;
	private boolean finished = false;

	public TrailerWriter(GZipPackListener listener, int crc, int totalIn) {
		this.listener = listener;
		this.crc = crc;
		this.totalIn = totalIn;
	}

	public boolean needsInput() {
		return false;
	}

	public void handleBuffer(GZipPacker packer, byte[] buf, int off, int len) {
		throw new IllegalStateException("Does not need input");
	}

	public void handleCurrentData(GZipPacker packer) {
		finished = true;
		byte[] packed = listener.getBuffer();
		ByteBuffer bb = ByteBuffer.wrap(packed);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(crc);
		bb.putInt(totalIn);
		listener.packed(packed, 0, 8);
		listener.finished();
	}

	public void finish() {
		// ignore
	}

	public boolean finished() {
		return finished;
	}
}
