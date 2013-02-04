package com.googlecode.httpfilter.proxy.rabbit.html;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Escape strings to make them html-safe.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HtmlEscapeUtils {
	/**
	 * Make the gven string html-safe.
	 * 
	 * @param s
	 *            the String to escape
	 * @return the escaped string
	 */
	public static String escapeHtml(String s) {
		return StringEscapeUtils.escapeHtml(s);
	}
}
