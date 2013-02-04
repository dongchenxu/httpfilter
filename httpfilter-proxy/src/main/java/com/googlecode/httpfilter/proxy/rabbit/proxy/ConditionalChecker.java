package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.rabbit.cache.Cache;
import com.googlecode.httpfilter.proxy.rabbit.cache.CacheEntry;
import com.googlecode.httpfilter.proxy.rabbit.cache.CacheException;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpDateParser;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;

/**
 * A class used to check for conditional requests.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class ConditionalChecker {

	boolean checkConditional(Connection con, HttpHeader header,
			RequestHandler rh, boolean mustRevalidate) {
		if (mustRevalidate)
			return setupRevalidation(con, header, rh);
		return (checkVary(con, header, rh) || checkMaxAge(con, header, rh)
				|| checkNoCache(con, header, rh) || checkQuery(con, header, rh)
				|| checkMinFresh(con, header, rh) || checkRevalidation(con,
					header, rh));
	}

	private boolean checkVary(Connection con, HttpHeader req, RequestHandler rh) {
		CacheEntry<HttpHeader, HttpHeader> entry = rh.getEntry();
		if (entry == null)
			return false;
		HttpHeader resp = rh.getDataHook();
		List<String> varies = resp.getHeaders("Vary");
		int s = varies.size();
		for (int i = 0; i < s; i++) {
			String vary = varies.get(i);
			if (vary.equals("*")) {
				con.setMayUseCache(false);
				return false;
			}
			HttpHeader origreq = entry.getKey();
			List<String> vals = origreq.getHeaders(vary);
			List<String> nvals = req.getHeaders(vary);
			if (vals.size() != nvals.size()) {
				return setupRevalidation(con, req, rh);
			}
			for (String val : vals) {
				int k = nvals.indexOf(val);
				if (k == -1) {
					return setupRevalidation(con, req, rh);
				}
			}
		}
		return false;
	}

	private boolean checkMaxAge(Connection con, HttpHeader req,
			RequestHandler rh, String cached) {
		if (cached != null) {
			cached = cached.trim();
			if (cached.startsWith("max-age=0")) {
				return setupRevalidation(con, req, rh);
			}
			Date now = new Date();
			CacheEntry<HttpHeader, HttpHeader> entry = rh.getEntry();
			if (checkMaxAge(cached, "max-age=", entry.getCacheTime(), now)
					|| checkMaxAge(cached, "s-maxage=", entry.getCacheTime(),
							now)) {
				con.setMayUseCache(false);
				return false;
			}
		}
		return false;
	}

	private boolean checkMaxAge(String cached, String type, long cachetime,
			Date now) {
		if (cached.startsWith(type)) {
			String secs = cached.substring(type.length());
			int ci = secs.indexOf(',');
			if (ci >= 0)
				secs = secs.substring(0, ci);
			try {
				long l = Long.parseLong(secs) * 1000;
				long ad = now.getTime() - cachetime;
				if (ad > l)
					return true;
			} catch (NumberFormatException e) {
				Logger log = Logger.getLogger(getClass().getName());
				log.warning("Bad number for max-age: '" + cached.substring(8)
						+ "'");
			}
		}
		return false;
	}

	protected boolean checkMaxAge(Connection con, HttpHeader req,
			RequestHandler rh) {
		CacheEntry<HttpHeader, HttpHeader> entry = rh.getEntry();
		if (entry == null)
			return false;
		List<String> ccs = req.getHeaders("Cache-Control");
		int s = ccs.size();
		for (int i = 0; i < s; i++) {
			String cc = ccs.get(i);
			if (checkMaxAge(con, req, rh, cc))
				return true;
		}
		return false;
	}

	private boolean checkNoCacheHeader(List<String> v) {
		int s = v.size();
		for (int i = 0; i < s; i++)
			if (v.get(i).equals("no-cache"))
				return true;
		return false;
	}

	private boolean checkNoCache(Connection con, HttpHeader header,
			RequestHandler rh) {
		CacheEntry<HttpHeader, HttpHeader> entry = rh.getEntry();
		if (entry == null)
			return false;
		// Only check the response header,
		// request headers with no-cache == refetch.
		HttpHeader resp = rh.getDataHook();
		boolean noCache = checkNoCacheHeader(resp.getHeaders("Cache-Control"));
		return noCache && setupRevalidation(con, header, rh);
	}

	private boolean checkQuery(Connection con, HttpHeader header,
			RequestHandler rh) {
		CacheEntry<HttpHeader, HttpHeader> entry = rh.getEntry();
		if (entry == null)
			return false;
		String uri = header.getRequestURI();
		int i = uri.indexOf('?');
		if (i >= 0) {
			return setupRevalidation(con, header, rh);
		}
		return false;
	}

	protected long getCacheControlValue(HttpHeader header, String cc) {
		List<String> nccs = header.getHeaders("Cache-Control");
		int s = nccs.size();
		for (int i = 0; i < s; i++) {
			String[] sts = nccs.get(i).split(",");
			for (String nc : sts) {
				nc = nc.trim();
				if (nc.startsWith(cc))
					return Long.parseLong(nc.substring(cc.length()));
			}
		}
		return -1;
	}

	private boolean checkMinFresh(Connection con, HttpHeader header,
			RequestHandler rh) {
		CacheEntry<HttpHeader, HttpHeader> entry = rh.getEntry();
		if (entry == null)
			return false;
		long minFresh = getCacheControlValue(header, "min-fresh=");
		if (minFresh == -1)
			return false;
		long maxAge = getCacheControlValue(rh.getDataHook(), "max-age=");
		if (maxAge == -1)
			return false;
		long currentAge = (System.currentTimeMillis() - entry.getCacheTime()) / 1000;
		if ((maxAge - currentAge) < minFresh)
			return setupRevalidation(con, header, rh);
		return false;
	}

	private boolean checkRevalidation(Connection con, HttpHeader header,
			RequestHandler rh) {
		CacheEntry<HttpHeader, HttpHeader> entry = rh.getEntry();
		if (entry == null)
			return false;

		HttpHeader resp = rh.getDataHook();
		for (String ncc : resp.getHeaders("Cache-Control")) {
			String[] sts = ncc.split(",");
			for (String nc : sts) {
				nc = nc.trim();
				if (nc.equals("must-revalidate")
						|| nc.equals("proxy-revalidate")) {
					con.setMustRevalidate();
					long maxAge = getCacheControlValue(rh.getDataHook(),
							"max-age=");
					if (maxAge >= 0) {
						long currentAge = (System.currentTimeMillis() - entry
								.getCacheTime()) / 1000;
						if (maxAge == 0 || currentAge > maxAge) {
							return setupRevalidation(con, header, rh);
						}
					}
				} else if (nc.startsWith("s-maxage=")) {
					con.setMustRevalidate();
					long sm = Long
							.parseLong(nc.substring("s-maxage=".length()));
					if (sm >= 0) {
						long currentAge = (System.currentTimeMillis() - entry
								.getCacheTime()) / 1000;
						if (sm == 0 || currentAge > sm) {
							return setupRevalidation(con, header, rh);
						}
					}
				}
			}
		}
		return false;
	}

	// Return true if we are sending If-None-Match, or If-Modified-Since
	private boolean setupRevalidation(Connection con, HttpHeader req,
			RequestHandler rh) {
		CacheEntry<HttpHeader, HttpHeader> entry = rh.getEntry();
		con.setMayUseCache(false);
		String method = req.getMethod();
		// if we can not filter (noproxy-request) we can not revalidate...
		if (method.equals("GET") && entry != null && con.getMayFilter()) {
			HttpHeader resp = rh.getDataHook();
			String etag = resp.getHeader("ETag");
			String lmod = resp.getHeader("Last-Modified");
			if (etag != null) {
				String inm = req.getHeader("If-None-Match");
				if (inm == null) {
					req.setHeader("If-None-Match", etag);
					con.setAddedINM();
				}
				return true;
			} else if (lmod != null) {
				String ims = req.getHeader("If-Modified-Since");
				if (ims == null) {
					req.setHeader("If-Modified-Since", lmod);
					con.setAddedIMS();
				}
				return true;
			} else {
				con.setMayUseCache(false);
				return false;
			}
		}
		return false;
	}

	boolean checkMaxStale(HttpHeader req, RequestHandler rh) {
		for (String cc : req.getHeaders("Cache-Control")) {
			cc = cc.trim();
			if (cc.equals("max-stale")) {
				if (rh.getEntry() != null) {
					HttpHeader resp = rh.getDataHook();
					long maxAge = rh.getCond().getCacheControlValue(resp,
							"max-age=");
					if (maxAge >= 0) {
						long now = System.currentTimeMillis();
						long currentAge = (now - rh.getEntry().getCacheTime()) / 1000;
						String age = resp.getHeader("Age");
						if (age != null)
							currentAge += Long.parseLong(age);
						if (currentAge > maxAge) {
							resp.addHeader("Warning",
									"110 RabbIT \"Response is stale\"");
						}
					}
				}
				return true;
			}
		}
		return false;
	}

	// Remove the cache entry if it is stale according to header 'str'
	private void checkStaleHeader(HttpHeader header, HttpHeader webHeader,
			HttpHeader cachedWebHeader, String str,
			Cache<HttpHeader, HttpHeader> cache) throws CacheException {
		String cln = webHeader.getHeader(str);
		String clo = cachedWebHeader.getHeader(str);
		// if the headers are not equal, remove cache entry
		if (clo != null) {
			if (!clo.equals(cln))
				cache.remove(header);
		} else {
			// if the header exists for one but not the other, remove cache
			// entry
			if (cln != null)
				cache.remove(header);
		}
	}

	// Returns false if the cached Date header is newer,
	// indicating that we should not cache
	protected boolean checkStaleCache(HttpHeader requestHeader, Connection con,
			RequestHandler rh) throws CacheException {
		if (rh.getEntry() == null)
			return true;
		if (rh.getWebHeader().getStatusCode().trim().equals("304"))
			return true;
		HttpHeader cachedWebHeader = rh.getDataHook();

		String sd = rh.getWebHeader().getHeader("Date");
		String cd = cachedWebHeader.getHeader("Date");
		if (sd != null && cd != null) {
			Date d1 = HttpDateParser.getDate(sd);
			Date d2 = HttpDateParser.getDate(cd);
			// if we get a response with a date older than we have,
			// we keep our cache.
			if (d1 != null && d1.before(d2))
				return false;
		}
		Cache<HttpHeader, HttpHeader> cache = con.getProxy().getCache();
		// check that some headers are equal
		if (rh.getWebHeader().getStatusCode().equals("200"))
			checkStaleHeader(requestHeader, rh.getWebHeader(), cachedWebHeader,
					"Content-Length", cache);
		checkStaleHeader(requestHeader, rh.getWebHeader(), cachedWebHeader,
				"Content-MD5", cache);
		checkStaleHeader(requestHeader, rh.getWebHeader(), cachedWebHeader,
				"ETag", cache);
		checkStaleHeader(requestHeader, rh.getWebHeader(), cachedWebHeader,
				"Last-Modified", cache);
		return true;
	}
}
