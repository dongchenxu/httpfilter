package com.googlecode.httpfilter;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ConnectHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.googlecode.httpfilter.handler.FirebugEmbedHttpResponseHander;
import com.googlecode.httpfilter.handler.HttpRequestHandler;
import com.googlecode.httpfilter.handler.HttpResponseHandler;
import com.googlecode.httpfilter.handler.TextHttpResponseHandler;
import com.googlecode.httpfilter.handler.UnCompressHttpResponseHandler;

@Component
public class HttpFilterSpringInitBean {

	private static final Logger logger = LoggerFactory.getLogger("httpfilter");
	
	@Value("${httpfilter.proxy.port}")
	private int proxyPort;
	
	private Server server;
	
	@PostConstruct
	public void init() throws Exception {
		
		logger.info("starting httpfilter...");
		startHttpFilterProxyServer();
		logger.info("httpfilter started. ok!");
		
	}
	
	@PreDestroy
	public void destroy() throws Exception {
		logger.info("stopping httpfilter...");
		stopHttpFilterProxyServer();
		logger.info("httpfilter stoped. ok!");
	}
	
	
	/**
	 * 启动HttpFilter代理服务器
	 * @throws Exception
	 */
	private void startHttpFilterProxyServer() throws Exception {
		server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(proxyPort);
        server.addConnector(connector);

        HandlerCollection handlers = new HandlerCollection();
        server.setHandler(handlers);

        
        // Setup proxy servlet
        ServletContextHandler context = new ServletContextHandler(handlers, "/", ServletContextHandler.SESSIONS);
        ServletHolder proxyServlet = new ServletHolder(HttpFilterProxyServlet.class){

			@Override
			protected Servlet newInstance() throws ServletException,
					IllegalAccessException, InstantiationException {
				final Servlet servlet = super.newInstance();
				if( HttpFilterProxyServlet.class.isAssignableFrom(this._class)  ) {
					final HttpFilterProxyServlet httpFilterProxyServlet = (HttpFilterProxyServlet)servlet;
					
					// inject requestHandlers
					injectHttpRequestHandlers(httpFilterProxyServlet.getHttpRequestHandlers());
					
					// inject responseHandlers
					injectHttpResponseHandlers(httpFilterProxyServlet.getHttpResponseHandlers());
					
				}
				return servlet;
			}
        	
        };
        context.addServlet(proxyServlet, "/*");
        
        // Setup proxy handler to handle CONNECT methods
        handlers.addHandler(new ConnectHandler());

        server.start();
        
	}
	
	/**
	 * 初始HttpRequestHandler处理器
	 * @param handlers
	 */
	private void injectHttpRequestHandlers(List<HttpRequestHandler> handlers) {
	}
	
	/**
	 * 初始化HttpResponseHandler处理器
	 * @param handlers
	 */
	private void injectHttpResponseHandlers(List<HttpResponseHandler> handlers) {
		handlers.add(new UnCompressHttpResponseHandler());
		handlers.add(new TextHttpResponseHandler());
		handlers.add(new FirebugEmbedHttpResponseHander());
	}
	
	/**
	 * 关闭HttpFilter代理服务器
	 * @throws Exception 
	 */
	private void stopHttpFilterProxyServer() throws Exception {
		if( null != server ) {
			server.stop();
			server.destroy();
		}
	}
	
}
