package com.googlecode.httpfilter.manager.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.googlecode.httpliar.Configer;
import com.googlecode.httpliar.HttpLiarServer;
import com.googlecode.httpliar.handler.HttpRequestHandler;
import com.googlecode.httpliar.handler.HttpResponseHandler;
import com.googlecode.httpliar.util.JvmUtils;
import com.googlecode.httpliar.util.JvmUtils.ShutdownHook;

@Component
public class HttpLiarManager {
	
	@Autowired
	private HttpRequestHandler reqHandler;
	@Autowired
	private HttpResponseHandler resHandler;

	@PostConstruct
	public void init() throws Exception {

		List<HttpRequestHandler> reqHandlerList = new ArrayList<HttpRequestHandler>();
		reqHandlerList.add(reqHandler);
		List<HttpResponseHandler> resHandlerList = new ArrayList<HttpResponseHandler>();
		resHandlerList.add(resHandler);
		
		final Configer configer = Configer.loadDefaultConfiger();

		final HttpLiarServer server = new HttpLiarServer(configer,reqHandlerList,resHandlerList);

		JvmUtils.registShutdownHook("httpliar-shutdown", new ShutdownHook() {

			@Override
			public void shutdown() throws Throwable {
				server.stopProxy();
			}

		});

		server.startProxy();
	}

}
