package com.googlecode.httpfilter.proxy.rabbit.util;

/**
 * A class that tries to guess mime types based on file extensions.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class MimeTypeMapper {
	/**
	 * Try to guess the mime type based on the given filename.
	 * 
	 * @param filename
	 *            the name of the file to guess the mime type for
	 * @return a mime type
	 */
	public static String getMimeType(String filename) {
		filename = filename.toLowerCase();
		if (filename.endsWith("gif"))
			return "image/gif";
		else if (filename.endsWith("png"))
			return "image/png";
		else if (filename.endsWith("jpeg") || filename.endsWith("jpg"))
			return "image/jpeg";
		else if (filename.endsWith("txt"))
			return "text/plain";
		else if (filename.endsWith("html"))
			return "text/html";
		return null;
	}
}