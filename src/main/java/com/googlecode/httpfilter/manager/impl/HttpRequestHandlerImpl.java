package com.googlecode.httpfilter.manager.impl;

import org.springframework.stereotype.Service;

import com.googlecode.httpliar.HttpLiarExchange;
import com.googlecode.httpliar.handler.HttpRequestHandler;
import com.googlecode.httpliar.handler.RequestHandlerResult;

@Service
public class HttpRequestHandlerImpl implements HttpRequestHandler {

	@Override
	public boolean isHandleRequest(HttpLiarExchange exchange) {
		return false;
	}

	@Override
	public RequestHandlerResult handleRequest(HttpLiarExchange exchange)
			throws Exception {
		return new RequestHandlerResult();
	}

}
