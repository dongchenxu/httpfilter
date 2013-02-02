package com.googlecode.httpfilter.proxy.rabbit.util;

import java.net.InetAddress;

/**
 * A class to handle access to ip ranges.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class IPAccess {
	private long lowip; // lowest ip in range
	private long highip; // highest ip in range

	/**
	 * Create a new IPAccess with given ip-range.
	 * 
	 * @param lowip
	 *            the lowest ip in the range
	 * @param highip
	 *            the highest ip in the range
	 */
	public IPAccess(InetAddress lowip, InetAddress highip) {
		if (lowip == null || highip == null)
			return;
		setup(lowip, highip);
	}

	/**
	 * transform to suitable variabletypes
	 * 
	 * @param lowipa
	 *            the lowest ip in the range
	 * @param highipa
	 *            the highest ip in the range
	 */
	private void setup(InetAddress lowipa, InetAddress highipa) {
		lowip = getLongFromIP(lowipa);
		highip = getLongFromIP(highipa);

		if (lowip > highip) {
			long t = lowip;
			lowip = highip;
			highip = t;
		}
	}

	/**
	 * make an long from the ip so we can do simple test later on
	 * 
	 * @param ia
	 *            the InetAddress to convert to a long
	 * @return the long value
	 */
	private long getLongFromIP(InetAddress ia) {
		byte[] address = ia.getAddress();
		long ret = 0;
		for (byte addres : address) {
			ret <<= 8;
			ret |= (addres & 0xffL); // byte is signed, sign extension is evil.
		}
		return ret;
	}

	/**
	 * check if a given ip is in this accessrange
	 * 
	 * @param ia
	 *            the ip we are testing.
	 * @return true if ia is in the range (inclusive), false otherwise
	 */
	public boolean inrange(InetAddress ia) {
		long check = getLongFromIP(ia);
		return (check >= lowip && check <= highip);
	}

	/**
	 * get the string representation of this access.
	 */
	@Override
	public String toString() {
		String low = getIP(lowip);
		String high = getIP(highip);

		return "" + low + "\t" + high;
	}

	private String getIP(long ip) {
		return "" + (ip >> 24) + "." + ((ip >> 16) & 0xff) + "."
				+ ((ip >> 8) & 0xff) + "." + (ip & 0xff);
	}
}
