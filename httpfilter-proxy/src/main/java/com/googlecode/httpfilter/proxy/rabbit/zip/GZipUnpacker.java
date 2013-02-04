package com.googlecode.httpfilter.proxy.rabbit.zip;

/**
 * A class that can unpack gzip streams in chunked mode.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class GZipUnpacker {
	private GZipUnpackState state;

	/**
	 * Create a new gzip or compress unpacker.
	 * 
	 * @param listener
	 *            the listener that will get the generated events.
	 * @param deflate
	 *            if true use plain deflate, if false use gzip.
	 */
	public GZipUnpacker(GZipUnpackListener listener, boolean deflate) {
		if (deflate)
			state = new UnCompressor(listener, false);
		else
			state = new MagicReader(listener);
	}

	/**
	 * Check if the unpacker currently needs more data
	 * 
	 * @return true if more input data is currently needed
	 */
	public boolean needsInput() {
		return state.needsInput();
	}

	/**
	 * Add more compressed data to the unpacker.
	 * 
	 * @param buf
	 *            the array holding the new data
	 * @param off
	 *            the start offset of the data to use
	 * @param len
	 *            the length of the data
	 */
	public void setInput(byte[] buf, int off, int len) {
		state.handleBuffer(this, buf, off, len);
	}

	/**
	 * Handle the next block of the current data.
	 */
	public void handleCurrentData() {
		state.handleCurrentData(this);
	}

	/**
	 * Change the internal gzip state to the given state.
	 * 
	 * @param state
	 *            the new internal state of the gzip unpacker.
	 */
	public void setState(GZipUnpackState state) {
		this.state = state;
	}
}
