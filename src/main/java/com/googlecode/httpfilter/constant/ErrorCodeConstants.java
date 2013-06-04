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
}
