package com.googlecode.httpfilter.proxy.rabbit.cache.utils;

import java.io.File;

/**
 * Helper methods for file based caches.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class CacheUtils {
	/** The maximum number of files per directory */
	public static final int FILES_PER_DIR = 256; // reasonable?
	/** The name of the temporary cache files directory */
	public static final String TEMPDIR = "temp";

	/**
	 * Get the file name for a cache entry.
	 * 
	 * @param baseDir
	 *            the base directory for the cache
	 * @param id
	 *            the id of the cache entry
	 * @param real
	 *            false if this is a temporary cache file, true if it is a
	 *            realized entry.
	 * @param extension
	 *            the file extension to use
	 * @return the file to use for the cached object
	 */
	public static File getEntryName(File baseDir, long id, boolean real,
			String extension) {
		File f;
		if (!real) {
			f = new File(baseDir, TEMPDIR);
		} else {
			long fdir = id / FILES_PER_DIR;
			f = new File(baseDir, Long.toString(fdir));
		}
		String name = Long.toString(id);
		if (extension != null)
			name = name + "." + extension;
		return new File(f, name);
	}
}