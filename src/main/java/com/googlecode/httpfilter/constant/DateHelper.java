package com.googlecode.httpfilter.constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
	private final static String DATE_FORMAT = "yyyyMMdd";

	/**
	 * 将日期的精度降低为秒级
	 * 
	 * @param date
	 * @return
	 */
	public static Date $SEC(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String fixStr = sdf.format(date);
		try {
			return sdf.parse(fixStr);
		} catch (ParseException e) {
			throw new IllegalArgumentException("fix date failed", e);
		}// try
	}

	public static String $STR(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(date);
	}

	public static Date $DATE(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			throw new IllegalArgumentException("covert date failed", e);
		}// try
	}
}
