package com.googlecode.httpfilter.proxy.rabbit.meta;

import com.googlecode.httpfilter.proxy.rabbit.cache.Cache;
import com.googlecode.httpfilter.proxy.rabbit.cache.CacheException;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;

/**
 * Clears the cache completely
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ClearCache extends BaseMetaHandler {
	private boolean timeToClean = false;

	@Override
	protected String getPageHeader() {
		return "Clearing cache";
	}

	/** Add the page information */
	@Override
	protected PageCompletion addPageInformation(StringBuilder sb) {
		// Send the wait message on the first time.
		// Start the cleaning when the wait message has been sent.
		if (!timeToClean) {
			sb.append("Please wait...<br>\n");
			timeToClean = true;
			return PageCompletion.PAGE_NOT_DONE;
		}
		Cache<HttpHeader, HttpHeader> cache = con.getProxy().getCache();
		try {
			cache.clear();
			sb.append("<font color=\"blue\">done!</font>\n");
		} catch (CacheException e) {
			failed(e);
		}
		return PageCompletion.PAGE_DONE;
	}
}
