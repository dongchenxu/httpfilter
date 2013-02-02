package com.googlecode.httpfilter.common.utils;

/**
 * 配置信息
 * 
 * @author vlinux
 * 
 */
public class Configer {

	// HttpServer端口号
	private int httpServerPort;

	// HttpServer webapp路径
	private String httpServerWebapp;

	// HttpServer context路径
	private String httpServerContext;

	// Proxy配置文件路径
	private String proxyConfig;

	/**
	 * 设置Proxy配置文件路径
	 * @return
	 */
	public String getProxyConfig() {
		return proxyConfig;
	}

	/**
	 * 设置Proxy配置文件路径
	 * @param proxyConfig
	 */
	public void setProxyConfig(String proxyConfig) {
		this.proxyConfig = proxyConfig;
	}

	/**
	 * 获取HttpServer context路径
	 * 
	 * @return
	 */
	public String getHttpServerContext() {
		return httpServerContext;
	}

	/**
	 * 设置HttpServer context
	 * 
	 * @param httpServerContext
	 */
	public void setHttpServerContext(String httpServerContext) {
		this.httpServerContext = httpServerContext;
	}

	/**
	 * 获取HttpServer webapp路径
	 * 
	 * @return
	 */
	public String getHttpServerWebapp() {
		return httpServerWebapp;
	}

	/**
	 * 设置HttpServer webapp路径
	 * 
	 * @param httpServerWebapp
	 */
	public void setHttpServerWebapp(String httpServerWebapp) {
		this.httpServerWebapp = httpServerWebapp;
	}

	/**
	 * 获取HttpServer端口
	 * 
	 * @return
	 */
	public int getHttpServerPort() {
		return httpServerPort;
	}

	/**
	 * 设置HttpServer端口
	 * 
	 * @param httpServerPort
	 */
	public void setHttpServerPort(int httpServerPort) {
		this.httpServerPort = httpServerPort;
	}

}
