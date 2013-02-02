package com.googlecode.httpfilter.proxy.rabbit.http;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import com.googlecode.httpfilter.proxy.rabbit.io.Storable;

/**
 * A class to handle general headers.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class GeneralHeader implements Storable, Iterable<Header> {

	/**
	 * The headers of this Header in order.
	 */
	protected final ArrayList<Header> headers = new ArrayList<Header>();

	/**
	 * Create a new HTTPHeader from scratch
	 */
	public GeneralHeader() {
		// empty
	}

	public Iterator<Header> iterator() {
		return headers.iterator();
	}

	/**
	 * Get the number of headers set in this header.
	 * 
	 * @return the number of header lines
	 */
	public int size() {
		return headers.size();
	}

	/**
	 * Get the text value of this header
	 * 
	 * @return a String describing this GeneralHeader.
	 */
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		fillBuffer(ret);
		return ret.toString();
	}

	/**
	 * Fill the given StringBuilder with text from this header.
	 * 
	 * @param sb
	 *            the StringBuilder this header is written to
	 */
	protected void fillBuffer(StringBuilder sb) {
		int hsize = headers.size();
		for (int i = 0; i < hsize; i++) {
			Header h = headers.get(i);
			sb.append(h.getType());
			sb.append(": ");
			sb.append(h.getValue());
			sb.append(Header.CRLF);
		}
		sb.append(Header.CRLF);
	}

	/**
	 * get the value of header type
	 * 
	 * @param type
	 *            the Header were intrested in.
	 * @return the value of type or null if no value is set.
	 */
	public String getHeader(String type) {
		int s = headers.size();
		for (int i = 0; i < s; i++) {
			Header h = headers.get(i);
			if (h.getType().equalsIgnoreCase(type))
				return h.getValue();
		}
		return null;
	}

	/**
	 * Set or replaces a value for given type.
	 * 
	 * @param type
	 *            the type or category that we want to set.
	 * @param value
	 *            the value we want to set
	 */
	public void setHeader(String type, String value) {
		int s = headers.size();
		for (int i = 0; i < s; i++) {
			Header h = headers.get(i);
			if (h.getType().equalsIgnoreCase(type)) {
				h.setValue(value);
				return;
			}
		}
		Header h = new Header(type, value);
		headers.add(h);
	}

	/**
	 * Set a specified header
	 * 
	 * @param current
	 *            the type or category that we want to set.
	 * @param newValue
	 *            the value we want to set
	 */
	public void setExistingValue(String current, String newValue) {
		int s = headers.size();
		for (int i = 0; i < s; i++) {
			Header h = headers.get(i);
			if (h.getValue().equals(current)) {
				h.setValue(newValue);
				return;
			}
		}
	}

	/**
	 * Add a new header. Old headers of the same type remain. The new header is
	 * placed last.
	 * 
	 * @param type
	 *            the type or category that we want to set.
	 * @param value
	 *            the value we want to set
	 */
	public void addHeader(String type, String value) {
		Header h = new Header(type, value);
		addHeader(h);
	}

	/**
	 * Add a new header. Old headers of the same type remain. The new header is
	 * placed last.
	 * 
	 * @param h
	 *            the Header to add
	 */
	public void addHeader(Header h) {
		headers.add(h);
	}

	/**
	 * removes a headerline from this header
	 * 
	 * @param type
	 *            the type we want to remove
	 */
	public void removeHeader(String type) {
		int s = headers.size();
		for (int i = 0; i < s; i++) {
			Header h = headers.get(i);
			if (h.getType().equalsIgnoreCase(type)) {
				headers.remove(i);
				i--;
				s--;
			}
		}
	}

	/**
	 * removes a header with the specified value
	 * 
	 * @param value
	 *            the value of the header we want to remove
	 */
	public void removeValue(String value) {
		int s = headers.size();
		for (int i = 0; i < s; i++) {
			Header h = headers.get(i);
			if (h.getValue().equals(value)) {
				headers.remove(i);
				return;
			}
		}
	}

	/**
	 * Get all headers of a specified type...
	 * 
	 * @param type
	 *            the type of the headers to get, eg. "Cache-Control".
	 * @return all the headers lines of this header
	 */
	public List<String> getHeaders(String type) {
		List<String> ret = null;
		int s = headers.size();
		for (int i = 0; i < s; i++) {
			Header h = headers.get(i);
			if (h.getType().equalsIgnoreCase(type)) {
				if (ret == null)
					ret = new ArrayList<String>();
				ret.add(h.getValue());
			}
		}
		if (ret == null)
			ret = Collections.emptyList();
		return ret;
	}

	/**
	 * Copy all headers in this header to the given header.
	 * 
	 * @param to
	 *            the GeneralHeader to add headers to.
	 */
	public void copyHeader(GeneralHeader to) {
		for (Header h : headers) {
			to.addHeader(h.getType(), h.getValue());
		}
	}

	public void read(DataInput in) throws IOException {
		int s = in.readInt();
		headers.ensureCapacity(s);
		for (int i = 0; i < s; i++) {
			Header h = new Header();
			h.read(in);
			headers.add(h);
		}
	}

	public void write(DataOutput out) throws IOException {
		out.writeInt(headers.size());
		for (Header h : headers)
			h.write(out);
	}
}
