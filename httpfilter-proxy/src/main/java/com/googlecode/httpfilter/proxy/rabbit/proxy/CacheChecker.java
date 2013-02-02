package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.httpfilter.proxy.rabbit.cache.Cache;
import com.googlecode.httpfilter.proxy.rabbit.cache.CacheEntry;
import com.googlecode.httpfilter.proxy.rabbit.cache.CacheException;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpDateParser;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;

/**
 * A class to verify if a cache entry can be used.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class CacheChecker {

	private static final String EXP_ERR = "No expected header found";

	HttpHeader checkExpectations(Connection con, HttpHeader header,
			HttpHeader webheader) {
		String exp = header.getHeader("Expect");
		if (exp == null)
			return null;
		if (exp.equals("100-continue")) {
			String status = webheader.getStatusCode();
			if (status.equals("200") || status.equals("304"))
				return null;
			return con.getHttpGenerator().get417(exp);
		}

		String[] sts = exp.split(";");
		for (String e : sts) {
			int i = e.indexOf('=');
			if (i == -1 || i == e.length() - 1)
				return con.getHttpGenerator().get417(e);
			String type = e.substring(0, i);
			String value = e.substring(i + 1);
			if (type.equals("expect")) {
				String h = webheader.getHeader(value);
				if (h == null)
					return con.getHttpGenerator().get417(EXP_ERR);
			}
		}

		return con.getHttpGenerator().get417(exp);
	}

	private HttpHeader checkIfMatch(Connection con, HttpHeader header,
			RequestHandler rh) {
		CacheEntry<HttpHeader, HttpHeader> entry = rh.getEntry();
		if (entry == null)
			return null;
		HttpHeader oldresp = rh.getDataHook();
		HttpHeader expfail = checkExpectations(con, header, oldresp);
		if (expfail != null)
			return expfail;
		String im = header.getHeader("If-Match");
		if (im == null)
			return null;
		String et = oldresp.getHeader("Etag");
		if (!ETagUtils.checkStrongEtag(et, im))
			return con.getHttpGenerator().get412();
		return null;
	}

	/**
	 * Check if we can use the cached entry.
	 * 
	 * @param con
	 *            the Connection handling the request
	 * @param header
	 *            the reques.
	 * @param rh
	 *            the RequestHandler
	 * @return true if the request was handled, false otherwise.
	 */
	public boolean checkCachedEntry(Connection con, HttpHeader header,
			RequestHandler rh) {
		con.getCounter().inc("Cache hits");
		con.setKeepalive(true);
		HttpHeader resp = checkIfMatch(con, header, rh);
		if (resp == null) {
			NotModifiedHandler nmh = new NotModifiedHandler();
			resp = nmh.is304(header, con.getHttpGenerator(), rh);
		}
		if (resp != null) {
			con.sendAndTryRestart(resp);
			return true;
		}
		con.setMayCache(false);
		try {
			resp = con.setupCachedEntry(rh);
			if (resp != null) {
				con.sendAndClose(resp);
				return true;
			}
		} catch (FileNotFoundException e) {
			// ignore sorta, to pull resource from the web.
			rh.setContent(null);
			rh.setEntry(null);
		} catch (IOException e) {
			rh.setContent(null);
			rh.setEntry(null);
		}
		return false;
	}

	/*
	 * If-None-Match: "tag-hbhpjfvtsy"\r\n If-Modified-Since: Thu, 11 Apr 2002
	 * 20:56:16 GMT\r\n If-Range: "tag-hbhpjfvtsy"\r\n
	 * 
	 * -----------------------------------
	 * 
	 * If-Unmodified-Since: Thu, 11 Apr 2002 20:56:16 GMT\r\n If-Match:
	 * "tag-ajbqyucqaf"\r\n If-Range: "tag-ajbqyucqaf"\r\n
	 */
	public boolean checkConditions(HttpHeader header, HttpHeader webheader) {
		String inm = header.getHeader("If-None-Match");
		if (inm != null) {
			String etag = webheader.getHeader("ETag");
			if (!ETagUtils.checkWeakEtag(inm, etag))
				return false;
		}
		Date dm = null;
		String sims = header.getHeader("If-Modified-Since");
		if (sims != null) {
			Date ims = HttpDateParser.getDate(sims);
			String lm = webheader.getHeader("Last-Modified");
			if (lm != null) {
				dm = HttpDateParser.getDate(lm);
				if (dm.getTime() - ims.getTime() < 60000) // dm.after (ims))
					return false;
			}
		}
		String sums = header.getHeader("If-Unmodified-Since");
		if (sums != null) {
			Date ums = HttpDateParser.getDate(sums);
			if (dm != null) {
				if (dm.after(ums))
					return false;
			} else {
				String lm = webheader.getHeader("Last-Modified");
				if (lm != null) {
					dm = HttpDateParser.getDate(lm);
					if (dm.after(ums))
						return false;
				}
			}
		}
		return true;
	}

	private void removeCaches(HttpHeader request, HttpHeader webHeader,
			String type, Cache<HttpHeader, HttpHeader> cache) {
		String loc = webHeader.getHeader(type);
		if (loc == null)
			return;
		try {
			URL u = new URL(request.getRequestURI());
			URL u2 = new URL(u, loc);
			String host1 = u.getHost();
			String host2 = u.getHost();
			if (!host1.equals(host2))
				return;
			int port1 = u.getPort();
			if (port1 == -1)
				port1 = 80;
			int port2 = u2.getPort();
			if (port2 == -1)
				port2 = 80;
			if (port1 != port2)
				return;
			HttpHeader h = new HttpHeader();
			h.setRequestURI(u2.toString());
			cache.remove(h);
		} catch (CacheException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(
					Level.WARNING,
					"RemoveCaches failed to remove cache entry: "
							+ request.getRequestURI() + ", " + loc, e);
		} catch (MalformedURLException e) {
			Logger logger = Logger.getLogger(getClass().getName());
			logger.log(Level.WARNING,
					"RemoveCaches got bad url: " + request.getRequestURI()
							+ ", " + loc, e);
		}
	}

	private void removeCaches(HttpHeader request, HttpHeader webHeader,
			Cache<HttpHeader, HttpHeader> cache) {
		removeCaches(request, webHeader, "Location", cache);
		removeCaches(request, webHeader, "Content-Location", cache);
	}

	void removeOtherStaleCaches(HttpHeader request, HttpHeader webHeader,
			Cache<HttpHeader, HttpHeader> cache) {
		String method = request.getMethod();
		String status = webHeader.getStatusCode();
		if ((method.equals("PUT") || method.equals("POST"))
				&& status.equals("201")) {
			removeCaches(request, webHeader, cache);
		} else if (method.equals("DELETE") && status.equals("200")) {
			removeCaches(request, webHeader, cache);
		}
	}
}
