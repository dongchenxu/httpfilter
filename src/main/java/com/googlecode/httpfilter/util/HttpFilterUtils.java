package com.googlecode.httpfilter.util;

import java.util.ArrayList;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpFields;

import com.googlecode.httpliar.util.HttpUtils;

public class HttpFilterUtils {

	public static String getReferer(HttpFields fields) {
		final String ct = fields.getStringField("referer");
		if (StringUtils.isEmpty(ct))
			return null;
		return ct.toLowerCase();
	}

	public static String getTraceId(HttpFields fields, String url) {
		if (MapUtils.isNotEmpty(HttpUtils.parseRequestParamters(url))) {
			String[] urlTraceIds = HttpUtils.parseRequestParamters(url)
					.get("trace_id");
			if( ArrayUtils.isNotEmpty(urlTraceIds) ){
				return urlTraceIds[0].toLowerCase();
			}
		}
		final String referer = fields.getStringField("referer");
		if (StringUtils.isNotEmpty(referer)){
			String[] refererTraceIds = HttpUtils.parseRequestParamters(referer)
					.get("trace_id");
			if (ArrayUtils.isNotEmpty(refererTraceIds)) {
				String refererTraceId = refererTraceIds[0];
				if (!StringUtils.isEmpty(refererTraceId)) {
					return refererTraceId.toLowerCase();
				}
			}
		}
		return null;
	}
}
