package com.googlecode.httpfilter.proxy.rabbit.http;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A utility class that parses date in the http headers. A date in http may be
 * written in many different formats so try them all.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HttpDateParser {
	private static final SimpleDateFormat sdf1 = new SimpleDateFormat(
			"EE',' dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat(
			"EEEE, dd-MMM-yy HH:mm:ss 'GMT'", Locale.US);
	private static final SimpleDateFormat sdf3 = new SimpleDateFormat(
			"EE MMM d HH:mm:ss yyyy", Locale.US);
	private static final SimpleDateFormat sdf4 = new SimpleDateFormat(
			"EE MMM  d HH:mm:ss yyyy", Locale.US);

	private static long offset;

	/**
	 * The default constructor.
	 */
	public HttpDateParser() {
		// empty
	}

	/**
	 * Set the time offset relative GMT.
	 * 
	 * @param offset
	 *            the time difference in millis
	 */
	public static void setOffset(long offset) {
		HttpDateParser.offset = offset;
	}

	/**
	 * Try to get a date from the given string. According to RFC 2068 We have to
	 * read 3 formats.
	 * 
	 * @param date
	 *            the String we are trying to parse.
	 * @return a Date or null if parsing was not possible.
	 */
	public static Date getDate(String date) {
		if (date == null)
			return null;

		Date d = getDate(date, sdf1, offset);
		if (d == null) {
			d = getDate(date, sdf2, offset);
			if (d == null) {
				d = getDate(date, sdf3, offset);
				if (d == null) {
					d = getDate(date, sdf4, offset);
				}
			}
		}
		return d;
	}

	private static Date getDate(String date, DateFormat sdf, long offsetUsed) {
		try {
			ParsePosition pos = new ParsePosition(0);
			Date d;
			synchronized (sdf) {
				d = sdf.parse(date, pos);
			}
			if (pos.getIndex() == 0 || pos.getIndex() != date.length())
				return null;
			d.setTime(d.getTime() + offsetUsed);
			return d;
		} catch (NumberFormatException e) {
			// ignore...
		}
		return null;
	}

	/**
	 * Get a String from the date.
	 * 
	 * @param d
	 *            the Date to format.
	 * @return a String describing the date in the right way.
	 */
	public static String getDateString(Date d) {
		synchronized (sdf1) {
			return sdf1.format(d);
		}
	}

	private static void compare(String d1, String d2) {
		Date dd1 = getDate(d1);
		Date dd2 = getDate(d2);
		if (dd1 != null) {
			System.out.println("dd1: " + dd1 + "\ndd2: " + dd2);
			if (dd2 != null)
				System.out.println("diff: " + (dd1.getTime() - dd2.getTime()));
		}
	}

	/**
	 * Simple self test method.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		String d1 = "Sat, 07 Feb 2004 22:14:05 GMT";
		String d2 = "Sun, 06 Nov 2043 08:49:37 GMT - 49 years (1994)";
		String d3 = "Tue, 18 Feb 2003 12:32:40 GMT";
		String d4 = "Tue, 18 Feb 2003 13:32:40 GMT";
		String d5 = "Sun Mar 12 13:37:23 2003";
		String d6 = "Sun Mar  2 13:47:48 2003";

		String d7 = "Sun, 09 Mar 2003 10:54:32 GMT";
		String d8 = "Sunday, 09-Mar-103 10:54:32 GMT";

		String d9 = "Sun, 09 Mar 2003 10:54:34 GMT";
		String d10 = "Sun Mar  9 10:54:34 2003";

		Date date1 = getDate(d1);
		System.out.println("date1: " + date1);
		Date date2 = getDate(d2);
		System.out.println("date2: " + date2);
		Date date3 = getDate(d3);
		System.out.println("date3: " + date3);
		Date date4 = getDate(d4);
		System.out.println("date4: " + date4);
		Date date5 = getDate(d5);
		System.out.println("date5: " + date5);
		Date date6 = getDate(d6);
		System.out.println("date6: " + date6);

		compare(d7, d8);
		compare(d9, d10);
	}
}
