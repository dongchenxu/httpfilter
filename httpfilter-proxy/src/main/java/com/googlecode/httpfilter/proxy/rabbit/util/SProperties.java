package com.googlecode.httpfilter.proxy.rabbit.util;

import java.util.HashMap;

/**
 * A simple string properties class.
 */
public class SProperties extends HashMap<String, String> {
	private static final long serialVersionUID = 20050430;

	/**
	 * Get the property for a given key
	 * 
	 * @param key
	 *            the property to get
	 * @return the property or null if the key does not exists in this
	 *         properties.
	 */
	public String getProperty(String key) {
		return get(key);
	}

	/**
	 * Get the property for a given key
	 * 
	 * @param key
	 *            the property to get
	 * @param defaultValue
	 *            the value to use if the key was not found or if the value was
	 *            null.
	 * @return the property value
	 */
	public String getProperty(String key, String defaultValue) {
		String val = get(key);
		if (val == null)
			return defaultValue;
		return val;
	}
}
