package com.googlecode.httpfilter.proxy.rabbit.cache.ncache;

import com.googlecode.httpfilter.proxy.rabbit.cache.CacheEntry;

/**
 * A cached object.
 * 
 * @param <K>
 *            the key type of this entry
 * @param <V>
 *            the value type of this entry
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class NCacheEntry<K, V> extends NCacheElementBase implements CacheEntry<K, V> {
	/** @serial The key for the object usually a URL or a filename. */
	private K key = null;
	/** @serial The hooked data of the cached object. */
	private V datahook;

	/**
	 * Create a new CacheEntry for given key and filename
	 * 
	 * @param id
	 *            the identity of this entry
	 * @param cachetime
	 *            the date this entry was cached
	 * @param expires
	 *            the date this entry exipres
	 * @param size
	 *            the number of bytes the actual cached resource is (excluding
	 *            overhead)
	 * @param key
	 *            the key for the object.
	 * @param datahook
	 *            the additional data
	 */
	public NCacheEntry(long id, long cachetime, long expires, long size, K key,
			V datahook) {
		super(id, cachetime, expires, size);
		this.key = key;
		this.datahook = datahook;
	}

	/**
	 * Get the key were holding data for
	 * 
	 * @return the keyobject
	 */
	public K getKey() {
		return key;
	}

	/**
	 * Get the hooked data.
	 * 
	 * @return the the hooked data.
	 */
	public V getDataHook() {
		return datahook;
	}

	/**
	 * Sets the data hook for this cache object. Since it is not always possible
	 * to make the key hold this...
	 * 
	 * @param o
	 *            the new data.
	 */
	public void setDataHook(V o) {
		this.datahook = o;
	}
}
