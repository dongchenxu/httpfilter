package com.googlecode.httpfilter.handler;

import com.googlecode.httpfilter.HttpFilterExchange;

/**
 * HTTP应答处理
 * @author vlinux
 *
 */
public interface HttpResponseHandler {

	/**
	 * 判断是否能处理HTTP应答
	 * @param exchange
	 * @return
	 */
	boolean isHandleResponse(final HttpFilterExchange exchange);
	
	/**
	 * 处理HTTP应答
	 * @param exchange
	 * @param block
	 * @return
	 * @throws Exception
	 */
	ResponseHandlerResult handleResponse(
			final HttpFilterExchange exchange,
			final DataBlock block) throws Exception;
	
}
