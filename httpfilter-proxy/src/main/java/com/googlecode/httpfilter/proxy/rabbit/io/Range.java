package com.googlecode.httpfilter.proxy.rabbit.io;

/**
 * A class to handle a range.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Range {
	private final long start;
	private final long end;

	/**
	 * Create a range that spans the given values (inclusive).
	 * 
	 * @param start
	 *            the lower value of the range
	 * @param end
	 *            the upper value of the range
	 */
	public Range(long start, long end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * @return the lower bound of this range
	 */
	public long getStart() {
		return start;
	}

	/**
	 * @return the upper bound of this range
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * @return the number of bytes in this range
	 */
	public long size() {
		// range is inclusive 1-5 has 5 bytes.
		return end - start + 1;
	}
}
