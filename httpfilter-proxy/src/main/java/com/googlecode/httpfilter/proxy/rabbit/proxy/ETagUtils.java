package com.googlecode.httpfilter.proxy.rabbit.proxy;

import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;

/**
 * Methods dealing with etags
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ETagUtils {
	/**
	 * Check if the given etag is weak.
	 * 
	 * @param t
	 *            the String to check
	 * @return true if the given token starts with the weak identifier "W/"
	 */
	public static boolean isWeak(String t) {
		return t.startsWith("W/");
	}

	/**
	 * Check if we have a strong etag match.
	 * 
	 * @param et
	 *            the current etag
	 * @param im
	 *            the if-modified tag
	 * @return true if we have a strong matching
	 */
	public static boolean checkStrongEtag(String et, String im) {
		return !isWeak(im) && im.equals(et);
	}

	/**
	 * Remove any W/ prefix then check if etags are equal. Inputs can be in any
	 * order.
	 * 
	 * @param h1
	 *            the first header to get an etag from
	 * @param h2
	 *            the second header to get an etag from
	 * @return true if the etags match or at least one of the etag headers do
	 *         not exist.
	 */
	public static boolean checkWeakEtag(HttpHeader h1, HttpHeader h2) {
		String et1 = h1.getHeader("Etag");
		String et2 = h2.getHeader("Etag");
		if (et1 == null || et2 == null)
			return true;
		return checkWeakEtag(et1, et2);
	}

	/**
	 * Remove any W/ prefix from the inputs then check if they are equal. Inputs
	 * can be in any order.
	 * 
	 * @param et
	 *            an etag header
	 * @param im
	 *            an if-modified header
	 * @return true if equal.
	 */
	public static boolean checkWeakEtag(String et, String im) {
		if (et == null || im == null)
			return false;
		if (isWeak(et))
			et = et.substring(2);
		if (isWeak(im))
			im = im.substring(2);
		return im.equals(et);
	}
}