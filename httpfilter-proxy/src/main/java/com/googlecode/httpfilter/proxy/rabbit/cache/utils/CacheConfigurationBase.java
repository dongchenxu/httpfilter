package com.googlecode.httpfilter.proxy.rabbit.cache.utils;

import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.rabbit.cache.CacheConfiguration;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * A base implementation of cache configuration.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public abstract class CacheConfigurationBase implements CacheConfiguration {
	private long maxSize = 0;
	private long cacheTime = 0;

	private static final String DEFAULT_SIZE = "10"; // 10 MB.
	private static final String DEFAULT_CACHE_TIME = "24"; // 1 day.

	public synchronized long getMaxSize() {
		return maxSize;
	}

	public synchronized void setMaxSize(long newMaxSize) {
		maxSize = newMaxSize;
	}

	public synchronized long getCacheTime() {
		return cacheTime;
	}

	public synchronized void setCacheTime(long newCacheTime) {
		cacheTime = newCacheTime;
	}

	public void setup(Logger logger, SProperties config) {
		String cmsize = config.getProperty("maxsize", DEFAULT_SIZE);
		try {
			// size is in MB
			setMaxSize(Long.parseLong(cmsize) * 1024 * 1024);
		} catch (NumberFormatException e) {
			logger.warning("Bad number for cache maxsize: '" + cmsize + "'");
		}

		String ctime = config.getProperty("cachetime", DEFAULT_CACHE_TIME);
		try {
			// time is given in hours
			setCacheTime(Long.parseLong(ctime) * 1000 * 60 * 60);
		} catch (NumberFormatException e) {
			logger.warning("Bad number for cache cachetime: '" + ctime + "'");
		}
	}
}
