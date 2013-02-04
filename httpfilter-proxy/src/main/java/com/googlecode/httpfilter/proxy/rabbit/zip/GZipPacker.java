package com.googlecode.httpfilter.proxy.rabbit.zip;

/**
 * A class that can pack gzip streams in chunked mode.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class GZipPacker {
	private GZipPackState state;

	/**
	 * Create a gzip packer that sends events to the given listener.
	 * 
	 * @param listener
	 *            the listener that will be notifiec when data has been packed.
	 */
	public GZipPacker(GZipPackListener listener) {
		state = new HeaderWriter(listener);
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
	 * Tell the packer that it has reached the end of data.
	 */
	public void finish() {
		state.finish();
	}

	/**
	 * Check if the packer is finished.
	 * 
	 * @return true if packing has finished
	 */
	public boolean finished() {
		return state.finished();
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
	 *            the new internal state of the gzip packer.
	 */
	public void setState(GZipPackState state) {
		this.state = state;
	}
}
