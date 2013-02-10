package com.googlecode.httpfilter.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class FeaturesUtils {

	private static final String SP = ";";
	private static final String SSP = ":";

	private static final String R_SP = "#3A";
	private static final String R_SSP = "#3B";
	
	/**
	 * 通过Map转换成String
	 * @param attrs
	 * @return
	 */
	public static final String toString(Map<String, String> attrs) {
		StringBuilder sb = new StringBuilder();
		if (null != attrs && !attrs.isEmpty()) {
			sb.append(SP);
			Set<Entry<String, String>> entrySet = attrs.entrySet();
			for (Entry<String, String> entry : entrySet) {
			    String key = entry.getKey();
			    String val = entry.getValue();
                if (StringUtils.isNotEmpty(val)) {
                    sb.append(encode(key)).append(SSP).append(encode(val)).append(SP);
                }
            }
		}
		return sb.toString();
	}
	
	/**
	 * 通过字符串解析成attributes
	 * @param str
	 * @return
	 */
	public static final Map<String, String> toMap(String str) {
		Map<String, String> attrs = new HashMap<String, String>();
		if (StringUtils.isNotBlank(str)) {
			String[] arr = StringUtils.split(str, SP);
			for (String kv : arr) {
				if (StringUtils.isNotBlank(kv)) {
					String[] ar = kv.split(SSP);
					if (ar.length == 2) {
						String k = decode(ar[0]);
						String v = decode(ar[1]);
						if (StringUtils.isNotBlank(k)
								&& StringUtils.isNotBlank(v)) {
							attrs.put(k, v);
						}
					}
				}
			}
		}
		return attrs;
	}
	
	private static String encode(String val) {
		return StringUtils.replace(StringUtils.replace(val, SP, R_SP), SSP, R_SSP);
	}

	private static String decode(String val) {
		return StringUtils.replace(StringUtils.replace(val, R_SP, SP), R_SSP, SSP);
	}
	
}
