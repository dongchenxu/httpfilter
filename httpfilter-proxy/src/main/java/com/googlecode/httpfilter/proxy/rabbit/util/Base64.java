package com.googlecode.httpfilter.proxy.rabbit.util;

/**
 * This class encodes/decodes stuff to/from the web.
 */
public class Base64 {

	/**
	 * dont construct this
	 */
	private Base64() {
		// nah.
	}

	/**
	 * the base64 characters
	 */
	private static final int pr2six[] = { 64, 64, 64, 64, 64, 64, 64, 64, 64,
			64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
			64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
			62, 64, 64, 64, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 64,
			64, 64, 64, 64, 64, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
			14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 64, 64, 64, 64, 64,
			64, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41,
			42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 64, 64, 64, 64, 64, 64, 64,
			64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
			64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
			64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
			64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
			64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
			64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
			64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
			64, 64, 64, 64, 64, 64, 64 };

	/**
	 * The base64 characters.
	 */
	private static final char uu_base64[] = { 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', '+', '/' };

	/**
	 * uudecode(base64) the given string. useful for decoding basic
	 * Authentication
	 * 
	 * @param base64string
	 *            the String to decode.
	 * @return the decoded string.
	 */
	public static String decode(String base64string) {
		StringBuilder ret = new StringBuilder(base64string.length() * 3 / 4);

		while ((base64string.length() % 4) != 0)
			base64string += "="; // that should be safe.
		int i = 0;
		int c1 = 0, c2 = 0, c3 = 0, c4 = 0;
		while (i < base64string.length()
				&& pr2six[base64string.charAt(i)] <= 63) {
			c1 = pr2six[base64string.charAt(i)];
			c2 = pr2six[base64string.charAt(i + 1)];
			c3 = pr2six[base64string.charAt(i + 2)];
			c4 = pr2six[base64string.charAt(i + 3)];
			ret.append((char) (c1 << 2 | c2 >> 4));
			ret.append((char) ((c2 << 4 | c3 >> 2) % 256));
			ret.append((char) ((c3 << 6 | c4) % 256));
			i += 4;
		}

		if (c3 > 63)
			ret.setLength(ret.length() - 2);
		else if (c4 > 63)
			ret.setLength(ret.length() - 1);
		return ret.toString();
	}

	/**
	 * uuencode(base64) the given String. useful for encoding basic
	 * authentication.
	 * 
	 * @param str
	 *            the String to encode.
	 * @return the encoded string.
	 */
	public static String encode(String str) {
		StringBuilder ret = new StringBuilder(str.length() * 4 / 3);
		char ch, ch1, ch2, ch3;
		int i;

		for (i = 0; i + 2 < str.length(); i += 3) {
			ch1 = str.charAt(i);
			ch2 = str.charAt(i + 1);
			ch3 = str.charAt(i + 2);
			ch = uu_base64[((ch1 >> 2) & 077)];
			ret.append(ch);

			ch = uu_base64[(((ch1 << 4) & 060) | ((ch2 >> 4) & 017))];
			ret.append(ch);

			ch = uu_base64[(((ch2 << 2) & 074) | ((ch3 >> 6) & 03))];
			ret.append(ch);

			ch = uu_base64[(ch3 & 077)];
			ret.append(ch);
		}

		// are we done yet?
		if (i == str.length())
			return ret.toString();

		// no so handle the trailing characters.
		ch1 = str.charAt(i);
		ch2 = str.length() > i + 1 ? str.charAt(i + 1) : (char) 0;

		ch = uu_base64[((ch1 >> 2) & 077)];
		ret.append(ch);

		ch = uu_base64[(((ch1 << 4) & 060) | ((ch2 >> 4) & 017))];
		ret.append(ch);

		if (str.length() > i + 1) {
			ch = uu_base64[((ch2 << 2) & 074)];
			ret.append(ch);
		} else {
			ret.append('=');
		}
		ret.append('=');
		return ret.toString();
	}
}
