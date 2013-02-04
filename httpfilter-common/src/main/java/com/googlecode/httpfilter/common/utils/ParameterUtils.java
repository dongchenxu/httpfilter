package com.googlecode.httpfilter.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 参数工具类
 * @author vlinux
 *
 */
public class ParameterUtils {

	/**
	 * 从uri中提取单值参数
	 * @param uri
	 * @param key
	 * @return
	 */
	public static String getAsString(String uri, String key) {
		
		if( StringUtils.isBlank(uri) 
				|| StringUtils.isBlank(key)) {
			return StringUtils.EMPTY;
		}
		
		final int lastIdx = uri.lastIndexOf("?");
		if( lastIdx < 0 
				|| lastIdx == uri.length()-1 ) {
			return StringUtils.EMPTY;
		}
		final String paramStrs = uri.substring(lastIdx+1, uri.length());
		final String[] paramStrArray = paramStrs.split("&");
		String value = StringUtils.EMPTY;
		if( ArrayUtils.isNotEmpty(paramStrArray) ) {
			for( String paramStr : paramStrArray ) {
				final String[] kvStrs = paramStr.split("=");
				if( kvStrs.length != 2 ) {
					continue;
				}
				if( kvStrs[0].equals(key) ) {
					value = kvStrs[1];
				}
			}
		}
		return value;
		
	}
	
	/**
	 * 从uri中提取多值参数
	 * @param uri
	 * @param key
	 * @return
	 */
	public static List<String> getAsArray(String uri, String key) {
		
		List<String> values = new ArrayList<String>();
		final String valueStr = getAsString(uri, key);
		final String[] valueArray = valueStr.split(",");
		if( ArrayUtils.isNotEmpty(valueArray) ) {
			for( String value : valueArray ) {
				if( StringUtils.isBlank(value) ) {
					values.add(value);
				}
			}
		}
		
		return values;
		
	}
	
	/**
	 * 给uri中设置单值
	 * @param uri
	 * @param key
	 * @param value
	 * @return
	 */
	public static String setAsString(String uri, String key, String value) {
		
		if( StringUtils.isBlank(uri) 
				|| StringUtils.isBlank(key)) {
			return StringUtils.EMPTY;
		}
		
		final int lastIdx = uri.lastIndexOf("?");
		final String param = key + "=" + value;
		return uri + (lastIdx<0?"?":"&") + param;
		
	}
	
}
