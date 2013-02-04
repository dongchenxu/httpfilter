package com.googlecode.httpfilter.proxy.rabbit.http;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that parses content range headers.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ContentRangeParser {
	private long start;
	private long end;
	private long total;
	private boolean valid = false;

	/**
	 * Try to parse the given content range.
	 * 
	 * @param cr
	 *            the Content-Range header.
	 */
	public ContentRangeParser(String cr) {
		if (cr != null) {
			if (cr.startsWith("bytes "))
				cr = cr.substring(6);
			StringTokenizer st = new StringTokenizer(cr, "-/");
			if (st.countTokens() == 3) {
				try {
					start = Long.parseLong(st.nextToken());
					end = Long.parseLong(st.nextToken());
					String length = st.nextToken();
					if ("*".equals(length))
						total = -1;
					else
						total = Long.parseLong(length);
					valid = true;
				} catch (NumberFormatException e) {
					Logger logger = Logger.getLogger(getClass().getName());
					logger.log(Level.WARNING, "bad content range: " + e
							+ " for string: '" + cr + "'", e);
				}
			}
		}
	}

	/**
	 * Check if the content range was valid.
	 * 
	 * @return true if the parsed content range was valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Get the start index
	 * 
	 * @return the start index of the range
	 */
	public long getStart() {
		return start;
	}

	/**
	 * Get the end index.
	 * 
	 * @return the end index of the range
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * Get the total size of the resource.
	 * 
	 * @return the resource size if know or -1 if unknown ('*' was used in the
	 *         content range).
	 */
	public long getTotal() {
		return total;
	}
}
