package com.googlecode.httpfilter.proxy.rabbit.cache.ncache;

/**
 * A key to use when searching the cache.
 * 
 * This class only exists to trick equals/hashCode that we have the same key.
 * 
 * @param <K>
 *            the type of keys stored.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class MemoryKey<K> extends FiledKey<K> {
	private static final long serialVersionUID = 20060606;
	private final K data;

	public MemoryKey(K data) {
		this.data = data;
		hashCode = data.hashCode();
	}

	@Override
	public K getData() {
		return data;
	}

	@Override
	public int hashCode() {
		// just do what FiledKey does
		return super.hashCode();
	}

	@Override
	public boolean equals(Object data) {
		// just do what FiledKey does
		return super.equals(data);
	}
}
