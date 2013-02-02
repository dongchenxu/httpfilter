package com.googlecode.httpfilter.proxy.rabbit.cache.ncache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An object that can read and write objects to file.
 * 
 * @param <T>
 *            the type of objects to read and write
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface FileHandler<T> {
	/**
	 * Read a T from the given stream.
	 * 
	 * @param is
	 *            the stream to read from
	 * @return the object read
	 * @throws IOException
	 *             if reading fails
	 */
	T read(InputStream is) throws IOException;

	/**
	 * Write a T to the given stream.
	 * 
	 * @param os
	 *            the stream to write the object to
	 * @param t
	 *            the object to write
	 * @throws IOException
	 *             if writing fails
	 */
	void write(OutputStream os, T t) throws IOException;
}
