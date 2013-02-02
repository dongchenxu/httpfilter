package com.googlecode.httpfilter.proxy.rabbit.util;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * A cache for strings. The cache is weak so GC can happen quickly.
 * 
 * String.intern may seem similar, but String.intern is a hard cache, that is no
 * GC will remove interned strings.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class StringCache extends WeakHashMap<String, WeakReference<String>> {

	private static StringCache instance;

	/**
	 * Get the shared instance of the string caches.
	 * 
	 * @return the StringCache
	 */
	public static synchronized StringCache getSharedInstance() {
		if (instance == null)
			instance = new StringCache();
		return instance;
	}

	/**
	 * Get a cached string with the same contents as the given string. If the
	 * string given is not null then the cache will hold one entry with the same
	 * value as the given string after this method has completed.
	 * 
	 * @param s
	 *            the string to get a shared string for.
	 * @return the shared string
	 */
	public String getCachedString(String s) {
		if (s == null)
			return null;
		synchronized (this) {
			WeakReference<String> wr = get(s);
			String k;
			if (wr != null && ((k = wr.get()) != null))
				return k;
			wr = new WeakReference<String>(s);
			put(s, wr);
			return s;
		}
	}
}
