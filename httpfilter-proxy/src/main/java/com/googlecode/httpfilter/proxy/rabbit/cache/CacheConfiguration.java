package com.googlecode.httpfilter.proxy.rabbit.cache;

import java.net.URL;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * A description of cache configuration.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface CacheConfiguration {
	/**
	 * Get the maximum size for this cache.
	 * 
	 * @return the maximum size in bytes this cache.
	 */
	long getMaxSize();

	/**
	 * Set the maximum size for this cache.
	 * 
	 * @param newMaxSize
	 *            the new maximum size for the cache.
	 */
	void setMaxSize(long newMaxSize);

	/**
	 * Get the number of miliseconds the cache stores things usually. This is
	 * the standard expiretime for objects, but you can set it for CacheEntries
	 * individially if you want to. NOTE 1: dont trust that an object will be in
	 * the cache this long. NOTE 2: dont trust that an object will be removed
	 * from the cache when it expires.
	 * 
	 * @return the number of miliseconds objects are stored normally.
	 */
	long getCacheTime();

	/**
	 * Set the standard expiry-time for CacheEntries
	 * 
	 * @param newCacheTime
	 *            the number of miliseconds to keep objects normally.
	 */
	void setCacheTime(long newCacheTime);

	/**
	 * Get the location where this cache stores its files.
	 * 
	 * @return the location, null if no physical location is used.
	 */
	URL getCacheDir();

	/**
	 * Set the internal state from the given properties
	 * 
	 * @param logger
	 *            the Logger to use for warnings or errors
	 * @param config
	 *            the properties to use
	 */
	void setup(Logger logger, SProperties config);
}