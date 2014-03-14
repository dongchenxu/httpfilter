package com.googlecode.httpfilter.util;

import org.apache.commons.lang.StringUtils;
import org.h2.jdbc.JdbcSQLException;

public class ExceptionUtils {

	/**
	 * 判断当前异常是否室H2的唯一索引异常
	 * @param t
	 * @return
	 */
	public static boolean isH2UniqueIndexException(Throwable t) {
		
		if( null == t ) {
			return false;
		}
		
		if( !(t instanceof JdbcSQLException ) ) {
			return false;
		}
		
		return StringUtils.containsIgnoreCase(t.getMessage(), "Unique index or primary key violation");
		
	}
	
}
