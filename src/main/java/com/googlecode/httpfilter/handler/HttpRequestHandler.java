package com.googlecode.httpfilter.handler;

import com.googlecode.httpfilter.HttpFilterExchange;

/**
 * 请求处理器
 * @author vlinux
 *
 */
public interface HttpRequestHandler {

	/**
	 * 判断是否能处理HTTP应答
	 * @param exchange
	 * @return
	 */
	boolean isHandleRequest(final HttpFilterExchange exchange);
	
	/**
	 * 处理来自浏览器的请求
	 * @param exchange
	 * @return
	 * @throws Exception
	 */
	RequestHandlerResult handleRequest(final HttpFilterExchange exchange) throws Exception;
	
}
