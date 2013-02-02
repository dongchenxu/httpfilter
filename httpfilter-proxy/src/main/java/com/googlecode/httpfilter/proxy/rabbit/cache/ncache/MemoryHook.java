package com.googlecode.httpfilter.proxy.rabbit.cache.ncache;

import java.util.logging.Logger;

/**
 * A key to use when searching the cache.
 * 
 * This class only exists to trick equals/hashCode that we have the same key.
 * 
 * @param <V>
 *            the type of the data stored
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class MemoryHook<V> extends FiledHook<V> {
	private static final long serialVersionUID = 20060606;
	private final V data;

	public MemoryHook(V data) {
		this.data = data;
	}

	@Override
	public <K> V getData(NCache<K, V> cache, NCacheData<K, V> entry,
			Logger logger) {
		return data;
	}
}
