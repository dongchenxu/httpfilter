package com.googlecode.httpfilter.proxy.rabbit.cache.ncache;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import com.googlecode.httpfilter.proxy.rabbit.cache.CacheEntry;

/**
 * The cached value and metadata
 * 
 * @param <K>
 *            the key type of this entry
 * @param <V>
 *            the value type of this entry
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class NCacheData<K, V> extends NCacheElementBase implements Externalizable {
	private static final long serialVersionUID = 20120113;
	/** The filed key object */
	private FiledKey<K> key;
	/** @serial The number of bytes the cached key is. */
	private long keySize = 0;
	/** @serial The hooked data of the cached object. */
	private FiledHook<V> datahook;
	/** @serial The number of bytes the cached hook is. */
	private long hookSize = 0;

	/** Not to be used, for externalizable only. */
	public NCacheData() {
		super(0, 0, 0, 0);
	}

	/**
	 * Create a new CacheEntry for given key and filename
	 * 
	 * @param id
	 *            the identity of this entry
	 * @param cachetime
	 *            the date this entry was cached
	 * @param expires
	 *            the date this entry expires
	 * @param size
	 *            the number of bytes of the cached data
	 * @param key
	 *            the key object
	 * @param keySize
	 *            the number of bytes of the key object
	 * @param datahook
	 *            the hooked data
	 * @param hookSize
	 *            the number of bytes of the hooked data
	 */
	public NCacheData(long id, long cachetime, long expires, long size,
			FiledKey<K> key, long keySize, FiledHook<V> datahook, long hookSize) {
		super(id, cachetime, expires, size);
		this.key = key;
		this.keySize = keySize;
		this.datahook = datahook;
		this.hookSize = hookSize;
	}

	/**
	 * Get the size of the key file
	 * 
	 * @return the size in bytes
	 */
	public long getKeySize() {
		return keySize;
	}

	/**
	 * Get the size of the hook
	 * 
	 * @return the size in bytes
	 */
	public long getHookSize() {
		return hookSize;
	}

	/**
	 * Get the key.
	 * 
	 * @return the FiledKey for the key data
	 */
	protected FiledKey<K> getKey() {
		return key;
	}

	/**
	 * Change the key to the new one.
	 * 
	 * @param key
	 *            they new FiledKey
	 * @param keySize
	 *            the number of bytes of the key object
	 */
	protected void setKey(FiledKey<K> key, long keySize) {
		this.key = key;
		this.keySize = keySize;
	}

	/**
	 * Get the data hook.
	 * 
	 * @return the FiledHook for the value
	 */
	protected FiledHook<V> getDataHook() {
		return datahook;
	}

	/**
	 * Set the real data hook
	 * 
	 * @param datahook
	 *            the new hooked data
	 * @param hookSize
	 *            the number of bytes of the hooked data
	 */
	protected void setDataHook(FiledHook<V> datahook, long hookSize) {
		this.datahook = datahook;
		this.hookSize = hookSize;
	}

	/**
	 * Update the expire and size fields with the values from the given cache
	 * entry
	 * 
	 * @param ne
	 *            the CacheEntry to get values from
	 */
	public void updateExpireAndSize(CacheEntry<K, V> ne) {
		setValues(getId(), getCacheTime(), ne.getExpires(), ne.getSize());
	}

	/**
	 * Read the cache entry from the object input.
	 */
	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		setValues(in.readLong(), in.readLong(), in.readLong(), in.readLong());
		key = (FiledKey<K>) in.readObject();
		keySize = in.readLong();
		datahook = (FiledHook<V>) in.readObject();
		hookSize = in.readLong();
	}

	/**
	 * Write the object to the object output.
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(getId());
		out.writeLong(getCacheTime());
		out.writeLong(getExpires());
		out.writeLong(getSize());
		out.writeObject(key);
		out.writeLong(keySize);
		out.writeObject(datahook);
		out.writeLong(hookSize);
	}
}
