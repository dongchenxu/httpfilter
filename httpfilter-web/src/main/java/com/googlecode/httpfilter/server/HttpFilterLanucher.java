package com.googlecode.httpfilter.server;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.googlecode.httpfilter.common.Configer;
import com.googlecode.httpfilter.proxy.rabbit.proxy.ProxyStarter;

/**
 * HttpFilter启动器
 * @author vlinux
 *
 */
@Component
public class HttpFilterLanucher {

	@Value("${httpfilter_firebug_path}")
	private String firebugPath;
	
	
	/**
	 * 启动方法
	 * @throws Exception
	 */
	@PostConstruct
	public void init() throws Exception {
		Configer.getDefault().setFirebugPath(firebugPath);
		new ProxyStarter().startProxy();
	}
	
}
