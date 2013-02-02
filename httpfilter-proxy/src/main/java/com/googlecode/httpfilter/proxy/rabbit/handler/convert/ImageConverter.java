package com.googlecode.httpfilter.proxy.rabbit.handler.convert;

import java.io.File;
import java.io.IOException;

/**
 * An image converter.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface ImageConverter {

	/**
	 * Check if this image converter can do any work.
	 * 
	 * @return true if this image converter can convert.
	 */
	boolean canConvert();

	/**
	 * Convert an image.
	 * 
	 * @param from
	 *            the File that holds the source image
	 * @param to
	 *            the File to store the converted image in
	 * @param info
	 *            some identifier for the image (typically the uri)
	 * @throws IOException
	 *             if the image conversion fails
	 */
	void convertImage(File from, File to, String info) throws IOException;
}
