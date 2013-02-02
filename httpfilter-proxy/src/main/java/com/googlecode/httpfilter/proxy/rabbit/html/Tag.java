package com.googlecode.httpfilter.proxy.rabbit.html;

import java.util.ArrayList;
import java.util.List;

/**
 * This class describes a HTML tag. That is something like &quot;&lt;tagname
 * key=value key=value key&gt;&quot;
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Tag {

	private String type = null;
	private String lowerCaseType = null;
	private TagType tagtype = null;
	private List<Pair> values = null;
	private Token parent;

	/**
	 * This class is a key value pair.
	 */
	public static class Pair {
		/** The key of this pair. */
		public final String key;
		/** The lowercase key.. */
		public String lcKey;
		/** The value of this pair. */
		public String value;

		/**
		 * Create a new pair with given key and value.
		 * 
		 * @param key
		 *            the key.
		 * @param value
		 *            the value.
		 */
		public Pair(String key, String value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * Get a String representation of this Pair.
		 * 
		 * @return a string representation of this Pair.
		 */
		@Override
		public String toString() {
			if (value == null)
				return key;
			return key + "=" + value;
		}

		/**
		 * Get the lower case key
		 * 
		 * @return the key in lower case
		 */
		public String getLowerCaseKey() {
			if (lcKey == null)
				lcKey = key.toLowerCase();
			return lcKey;
		}
	}

	/**
	 * Create a new Tag without type and with no arguments.
	 */
	public Tag() {
		// empty
	}

	/**
	 * Create a new Tag with given type
	 * 
	 * @param type
	 *            the type of the tag (like &quot;body&quot;).
	 */
	public Tag(String type) {
		setType(type);
	}

	/**
	 * Set the Type of this Tag.
	 * 
	 * @param type
	 *            the new type of this tag.
	 */
	public void setType(String type) {
		this.type = type;
		this.lowerCaseType = type.toLowerCase();
		this.tagtype = TagType.getTagType(lowerCaseType);
	}

	/**
	 * Get the type of this tag.
	 * 
	 * @return the type of this tag.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the lowercase type of this tag.
	 * 
	 * @return the type of this tag.
	 */
	public String getLowerCaseType() {
		return lowerCaseType;
	}

	/**
	 * Get the tagtype of this tag.
	 * 
	 * @return the TagType of this tag.
	 */
	public TagType getTagType() {
		return tagtype;
	}

	/**
	 * Get the parent token of this tag.
	 * 
	 * @return the parent Token for this tag.
	 */
	public Token getToken() {
		return parent;
	}

	/**
	 * Set the parent token of this tag.
	 * 
	 * @param token
	 *            the parent of this tag.
	 */
	public void setToken(Token token) {
		this.parent = token;
	}

	/**
	 * Add a new key/value Pair to this tag.
	 * 
	 * @param key
	 *            the key.
	 * @param value
	 *            the value.
	 */
	public void addArg(String key, String value) {
		addArg(key, value, true);
	}

	/**
	 * Add a new key/value Pair to this tag.
	 * 
	 * @param key
	 *            the key.
	 * @param value
	 *            the value.
	 * @param changed
	 *            if true this tag is changed by this (that is a new parameter
	 *            is added) if false this tag is still considered unchanged
	 *            after this operation (useful while parsing a page).
	 */
	public void addArg(String key, String value, boolean changed) {
		if (values == null)
			values = new ArrayList<Pair>();
		values.add(new Pair(key, value));
		if (changed && parent != null)
			parent.setChanged(true);
	}

	/**
	 * Remove an attribute.
	 * 
	 * @param remover
	 *            the attribute key to remove.
	 */
	public void removeAttribute(String remover) {
		if (values == null)
			return;
		remover = remover.toLowerCase();
		int vsize = values.size();
		for (int i = 0; i < vsize; i++) {
			Pair p = values.get(i);
			if (p.getLowerCaseKey().equals(remover)) {
				values.remove(i--);
				vsize--;
				if (parent != null)
					parent.setChanged(true);
			}
		}
	}

	/**
	 * Get the value of the given key.
	 * 
	 * @param key
	 *            the attribute to get the value from.
	 * @return the value or null (if not found of key has no value).
	 */
	public String getAttribute(String key) {
		if (values != null) {
			key = key.toLowerCase();
			int vsize = values.size();
			for (int i = 0; i < vsize; i++) {
				Pair p = values.get(i);
				if (p.getLowerCaseKey().equals(key))
					return p.value;
			}
		}
		return null;
	}

	/**
	 * Set the attribute given. If the key already exist its value is set
	 * otherwise the Pair is added.
	 * 
	 * @param key
	 *            the key to set.
	 * @param value
	 *            the value to set.
	 */
	public void setAttribute(String key, String value) {
		boolean done = false;
		if (values != null) {
			key = key.toLowerCase();
			int vsize = values.size();
			for (int i = 0; i < vsize; i++) {
				Pair p = values.get(i);
				if (p.getLowerCaseKey().equals(key)) {
					p.value = value;
					done = true;
				}
			}
		}
		if (!done) {
			if (values == null)
				values = new ArrayList<Pair>();
			values.add(new Pair(key, value));
		}
		if (parent != null)
			parent.setChanged(true);
	}

	/**
	 * Get this Tag as a String.
	 * 
	 * @return a String representation of this object.
	 */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder("<");
		if (type != null) {
			res.append(type);
		}
		if (values != null) {
			int vsize = values.size();
			for (int i = 0; i < vsize; i++) {
				res.append(" ");
				res.append(values.get(i));
			}
		}

		res.append(">");
		return res.toString();
	}
}
