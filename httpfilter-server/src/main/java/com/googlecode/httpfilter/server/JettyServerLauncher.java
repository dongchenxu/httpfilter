package com.googlecode.httpfilter.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * 服务器启动器
 * 
 * @author luanjia
 * 
 */
public class JettyServerLauncher {

	private final Server server;

	/**
	 * @param resource webapp路径
	 * @param port 启动端口
	 */
	public JettyServerLauncher(final String resource, final int port) {
		server = new Server(port);
		WebAppContext context = new WebAppContext();
		context.setResourceBase(resource);
		context.setContextPath("/httpfilter");
		context.setParentLoaderPriority(true);

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { context, new DefaultHandler() });
		server.setHandler(handlers);

	}

	/**
	 * 启动
	 * @throws Exception
	 */
	public void startup() throws Exception {
		server.start();
		server.join();
	}

	public static void main(String... args) throws Exception {
		new JettyServerLauncher("./webapp",8080).startup();
	}

}
