package com.googlecode.httpfilter.proxy.rabbit.cache.ncache;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * A class that stores cache keys in compressed form.
 * 
 * @param <K>
 *            they key object type
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class FiledKey<K> extends FileData<K> {
	private static final long serialVersionUID = 20050430;

	protected int hashCode; // the hashCode for the contained object.
	private long id;
	protected transient NCache<K, ?> cache;

	protected String getExtension() {
		return "key";
	}

	protected <V> void setCache(NCache<K, V> cache) {
		this.cache = cache;
	}

	protected <V> long storeKey(NCache<K, V> cache, long id, K key,
			Logger logger) throws IOException {
		setCache(cache);
		hashCode = key.hashCode();
		this.id = id;
		return writeData(getFileName(), cache.getKeyFileHandler(), key, logger);
	}

	private File getFileName() {
		return cache.getEntryName(id, true, getExtension());
	}

	/** Get the hashCode for the contained key object. */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/** Check if the given object is equal to the contained key. */
	@Override
	public boolean equals(Object data) {
		if (data == null)
			return false;
		try {
			K myData = getData();
			if (data instanceof FiledKey) {
				data = ((FiledKey<?>) data).getData();
			}
			if (myData != null) {
				return myData.equals(data);
			}
			return data == null;
		} catch (IOException e) {
			throw new RuntimeException("Failed to read contents", e);
		}
	}

	/**
	 * Get the actual key object.
	 * 
	 * @return the key object
	 * @throws IOException
	 *             if reading the data fails
	 */
	public K getData() throws IOException {
		return readData(getFileName(), cache.getKeyFileHandler(),
				cache.getLogger());
	}

	/**
	 * Get the unique id for this object.
	 * 
	 * @return the id of this object
	 */
	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "FiledKey: " + hashCode + ", " + getFileName();
	}
}
