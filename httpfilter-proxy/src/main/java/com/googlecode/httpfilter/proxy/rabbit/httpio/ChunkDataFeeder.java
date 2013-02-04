package com.googlecode.httpfilter.proxy.rabbit.httpio;

/**
 * A feeder of chunked data
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface ChunkDataFeeder {
	/**
	 * The chunk reader needs more data.
	 */
	void register();

	/**
	 * The chunk reader needs to read more data, compact buffer before
	 * registering.
	 */
	void readMore();

	/**
	 * Chunk reading has been completed.
	 */
	void finishedRead();
}
