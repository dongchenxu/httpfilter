package com.googlecode.httpfilter.common;

/**
 * 配置信息
 * 
 * @author vlinux
 * 
 */
public class Configer {

	private final static Configer configer = new Configer();
	
	/*
	 * firebug-lite的路径
	 */
	private String firebugPath;
	
	public static Configer getDefault() {
		return configer;
	}

	/**
	 * 设置firebug路径
	 * @param firebugPath
	 */
	public void setFirebugPath(String firebugPath) {
		this.firebugPath = firebugPath;
	}
	
	/**
	 * 获取firebug路径
	 * @return
	 */
	public String getFirebugPath() {
		return firebugPath;
	}

}
