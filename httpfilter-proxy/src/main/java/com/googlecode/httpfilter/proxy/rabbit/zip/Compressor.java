package com.googlecode.httpfilter.proxy.rabbit.zip;

import java.util.zip.CRC32;
import java.util.zip.Deflater;

/**
 * The deflator of gzip packing
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class Compressor implements GZipPackState {
	private final GZipPackListener listener;
	private final CRC32 crc = new CRC32();
	private final Deflater def;
	private boolean finished = false;

	public Compressor(GZipPackListener listener) {
		this.listener = listener;
		def = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
	}

	public boolean needsInput() {
		return def.needsInput();
	}

	public void handleBuffer(GZipPacker packer, byte[] buf, int off, int len) {
		crc.update(buf, off, len);
		def.setInput(buf, off, len);
	}

	public void handleCurrentData(GZipPacker packer) {
		if (def.finished()) {
			GZipPackState t = new TrailerWriter(listener, (int) crc.getValue(),
					def.getTotalIn());
			packer.setState(t);
			t.handleCurrentData(packer);
			def.end();
			return;
		}
		if (!finished && def.needsInput())
			return;
		byte[] packed = listener.getBuffer();
		int len = def.deflate(packed, 0, packed.length);
		listener.packed(packed, 0, len);
	}

	public void finish() {
		if (def.finished())
			return;
		finished = true;
		def.finish();
	}

	public boolean finished() {
		return false;
	}
}
