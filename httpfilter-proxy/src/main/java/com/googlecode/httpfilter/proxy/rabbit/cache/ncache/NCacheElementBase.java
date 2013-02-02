package com.googlecode.httpfilter.proxy.rabbit.cache.ncache;

/**
 * A base class for the cache elements
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class NCacheElementBase {
	/** @serial The unique id of the object. */
	private long id = 0;
	/** @serial The date this entry was cached. */
	private long cachetime = -1;
	/** @serial The date this entry expires. */
	private long expires = Long.MAX_VALUE;
	/** @serial The number of bytes this object is. */
	private long size = 0;

	public NCacheElementBase(long id, long cachetime, long expires, long size) {
		this.id = id;
		this.cachetime = cachetime;
		this.expires = expires;
		this.size = size;
	}

	/**
	 * Get the id of our entry.
	 * 
	 * @return the id of the entry.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Get the date this object was cached.
	 * 
	 * @return a date.
	 */
	public long getCacheTime() {
		return cachetime;
	}

	/**
	 * Get the expiry-date of our file
	 * 
	 * @return the expiry date of our data
	 */
	public long getExpires() {
		return expires;
	}

	/**
	 * Sets the expirydate of our data
	 * 
	 * @param d
	 *            the new expiry-date.
	 */
	public void setExpires(long d) {
		this.expires = d;
	}

	/**
	 * Get the size of our file
	 * 
	 * @return the size of our data
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Set the values, only to be used for externalizing.
	 * 
	 * @param id
	 *            the id of this entry
	 * @param cachetime
	 *            the date this resource was cached
	 * @param expires
	 *            the date this resource expires
	 * @param size
	 *            the number of bytes of the resource
	 */
	public void setValues(long id, long cachetime, long expires, long size) {
		this.id = id;
		this.cachetime = cachetime;
		this.expires = expires;
		this.size = size;
	}
}
