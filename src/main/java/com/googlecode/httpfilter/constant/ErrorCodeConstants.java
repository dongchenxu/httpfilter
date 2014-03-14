package com.googlecode.httpfilter.constant;

import com.googlecode.httpfilter.util.ResultUtils.ErrorMessage;

public class ErrorCodeConstants {

	@ErrorMessage(errorMessage="通过batchNo获取NAS文件失败")
	public static final String NAS_QUERY_FAILED_BY_BATCHNO = "NAS_QUERY_FAILED_BY_BATCHNO";
	
	@ErrorMessage(errorMessage="查询过滤器(id=%s)失败！")
	public static final String GET_FILTER_BY_ID_ERROR = "GET_FILTER_BY_ID_ERROR";
	
	@ErrorMessage(errorMessage="查询连接(id=%s)失败！")
	public static final String GET_CONNECTION_BY_ID_ERROR = "GET_CONNECTION_BY_ID_ERROR";
	
	@ErrorMessage(errorMessage="增加并查询连接(traceId=%s)失败！")
	public static final String FETCH_CONNECTION_BY_ID_ERROR = "FETCH_CONNECTION_BY_ID_ERROR";
	
	@ErrorMessage(errorMessage="通过traceId查询连接(traceId=%s)失败！")
	public static final String GET_CONNECTION_BY_TRACE_ID_ERROR = "GET_CONNECTION_BY_TRACE_ID_ERROR";
	
	@ErrorMessage(errorMessage="创建ConnectionDo失败！")
	public static final String CREATE_CONNECTION_ERROR = "CREATE_CONNECTION_ERROR";
	
	@ErrorMessage(errorMessage="通过traceId查询会话(traceId=%s)失败！")
	public static final String GET_COMMUNICATION_BY_TRACE_ID_ERROR = "GET_COMMUNICATION_BY_TRACE_ID_ERROR";
	
	@ErrorMessage(errorMessage="查询会话(id=%s)失败！")
	public static final String GET_COMMUNICATION_BY_ID_ERROR = "GET_COMMUNICATION_BY_ID_ERROR";
	
	@ErrorMessage(errorMessage="创建会话失败！")
	public static final String CREATE_COMMUNICATION_ERROR = "CREATE_COMMUNICATION_ERROR";
	
	@ErrorMessage(errorMessage="查询规则(id=%s)失败！")
	public static final String GET_RULE_BY_ID_ERROR = "GET_RULE_BY_ID_ERROR";
	
	@ErrorMessage(errorMessage="查询待校验的规则(id=%s)失败！")
	public static final String GET_TOBECHECK_BY_ID_ERROR = "GET_TOBECHECK_BY_ID_ERROR";
	
	@ErrorMessage(errorMessage="查询运行版本(id=%s)失败！")
	public static final String GET_VERSION_BY_ID_ERROR = "GET_VERSION_BY_ID_ERROR";
	
	@ErrorMessage(errorMessage="校验请求中无关键词为：(keyWords=%s)的请求！")
	public static final String MAIN_REQUEST_NOT_CONTAINT_KEYWORDS_ERROR = "MAIN_REQUEST_NOT_CONTAINT_KEYWORDS_ERROR";
	
	@ErrorMessage(errorMessage="被校验请求中无关键词为：(keyWords=%s)的请求！")
	public static final String CHECK_REQUEST_NOT_CONTAINT_KEYWORDS_ERROR = "CHECK_REQUEST_NOT_CONTAINT_KEYWORDS_ERROR";
	
	@ErrorMessage(errorMessage="所有请求中无关键词为：(keyWords=%s)的请求！")
	public static final String CHECK_AND_MAIN_REQUEST_NOT_CONTAINT_KEYWORDS_ERROR = "CHECK_AND_MAIN_REQUEST_NOT_CONTAINT_KEYWORDS_ERROR";
	
	@ErrorMessage(errorMessage="待校验请求中请求参数：(param=%s)所对应的值不同！")
	public static final String CHECK_PARAM_VALUE_NOE_EQUAL = "CHECK_PARAM_VALUE_NOE_EQUAL";
	
	@ErrorMessage(errorMessage="待校验请求中缺少参数：(param=%s)")
	public static final String CHECK_NOT_CONTAIN_PARAM = "CHECK_NOT_CONTAIN_PARAM";
	
	@ErrorMessage(errorMessage="待校验response与期待的response内容不符，请求关键字为：(keyWords=%s)")
	public static final String CHECK_RESPONSE_CONTENT_NOT_SAME = "CHECK_RESPONSE_CONTENT_NOT_SAME";
}
