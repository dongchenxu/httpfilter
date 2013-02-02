package com.googlecode.httpfilter.proxy.rabbit.http;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import com.googlecode.httpfilter.proxy.rabbit.io.Storable;
import com.googlecode.httpfilter.proxy.rabbit.util.StringCache;

/**
 * This class holds a single header value, that is a &quot;type: some text&quot;
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Header implements Storable {
	private String type;
	private String value;

	/** The String consisting of \r and \n */
	public static final String CRLF = "\r\n";

	/** The string cache we are using. */
	private static final StringCache stringCache = StringCache
			.getSharedInstance();

	private static String getCachedString(String s) {
		return stringCache.getCachedString(s);
	}

	/** Used for externalization. */
	public Header() {
		// empty
	}

	/**
	 * Create a new header
	 * 
	 * @param type
	 *            the type of this header
	 * @param value
	 *            the actual value
	 */
	public Header(String type, String value) {
		this.type = getCachedString(type);
		this.value = getCachedString(value);
	}

	/**
	 * Get the type of this header.
	 * 
	 * @return the type of this header
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the value of this header.
	 * 
	 * @return the value of this header
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value of this header to the new value given.
	 * 
	 * @param newValue
	 *            the new value
	 */
	public void setValue(String newValue) {
		value = newValue;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Header) {
			return (((Header) o).type.equalsIgnoreCase(type));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	/**
	 * Update the value by appending the given string to it.
	 * 
	 * @param s
	 *            the String to append to the current value
	 */
	public void append(String s) {
		value += CRLF + s;
		value = getCachedString(value);
	}

	public void write(DataOutput out) throws IOException {
		out.writeUTF(type);
		out.writeUTF(value);
	}

	public void read(DataInput in) throws IOException {
		type = getCachedString(in.readUTF());
		value = getCachedString(in.readUTF());
	}
}
