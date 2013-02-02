package com.googlecode.httpfilter.proxy.rabbit.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * An object that can be read to a DataOutput and read from a DataInput.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface Storable {
	/**
	 * Write this object to the given output.
	 * 
	 * @param out
	 *            the output to write to
	 * @throws IOException
	 *             if writing fails
	 */
	void write(DataOutput out) throws IOException;

	/**
	 * Fill in this object with data from the given input.
	 * 
	 * @param in
	 *            the input to read from
	 * @throws IOException
	 *             if reading fails
	 */
	void read(DataInput in) throws IOException;
}
