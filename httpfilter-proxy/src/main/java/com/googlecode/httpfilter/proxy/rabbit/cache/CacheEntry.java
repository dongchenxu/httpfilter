package com.googlecode.httpfilter.proxy.rabbit.cache;

/**
 * A cached object.
 * 
 * @param <K>
 *            the key type of this cache entry
 * @param <V>
 *            the data resource
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface CacheEntry<K, V> {

	/**
	 * Get the id of the entry.
	 * 
	 * @return the id of the entry.
	 */
	long getId();

	/**
	 * Get the key were holding data for
	 * 
	 * @return the key object
	 */
	K getKey();

	/**
	 * Get the date this object was cached.
	 * 
	 * @return a date (millis since the epoch).
	 */
	long getCacheTime();

	/**
	 * Get the size of our file
	 * 
	 * @return the size of our data
	 */
	long getSize();

	/**
	 * Get the expiry-date of our file
	 * 
	 * @return the expiry date of our data
	 */
	long getExpires();

	/**
	 * Sets the expirydate of our data
	 * 
	 * @param d
	 *            the new expiry-date.
	 */
	void setExpires(long d);

	/**
	 * Get the hooked data.
	 * 
	 * @return the the hooked data.
	 */
	V getDataHook();

	/**
	 * Sets the data hook for this cache object. Since it is not always possible
	 * to make the key hold this...
	 * 
	 * @param o
	 *            the new data.
	 */
	void setDataHook(V o);
}
