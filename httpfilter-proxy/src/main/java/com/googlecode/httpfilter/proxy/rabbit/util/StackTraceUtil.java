package com.googlecode.httpfilter.proxy.rabbit.util;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Utility functions when dealing with stack traces.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class StackTraceUtil {
	/**
	 * Print the given Throwable to a String.
	 * 
	 * @param t
	 *            the Throwable to get the stack trace for
	 * @return the formatted stack trace
	 */
	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter sos = new PrintWriter(sw);
		t.printStackTrace(sos);
		return sw.toString();
	}
}
