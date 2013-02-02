package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.IOException;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.BufferHandler;
import com.googlecode.httpfilter.proxy.org.khelekore.rnio.NioHandler;
import com.googlecode.httpfilter.proxy.rabbit.cache.Cache;
import com.googlecode.httpfilter.proxy.rabbit.cache.CacheEntry;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.httpio.FileResourceSource;

/**
 * A resource that comes from the cache.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class CacheResourceSource extends FileResourceSource {

	/**
	 * Create a new CacheResourceSource.
	 * 
	 * @param cache
	 *            the Cache that has the cached resource
	 * @param entry
	 *            the CacheEntry for the resource
	 * @param tr
	 *            the NioHandler to use for network and background tasks when
	 *            serving this resource
	 * @param bufHandler
	 *            the BufferHandler to use for this resource
	 * @throws IOException
	 *             if the cached resource is not available
	 */
	public CacheResourceSource(Cache<HttpHeader, HttpHeader> cache,
			CacheEntry<HttpHeader, HttpHeader> entry, NioHandler tr,
			BufferHandler bufHandler) throws IOException {
		super(cache.getEntryName(entry.getId(), true, null), tr, bufHandler);
	}
}
