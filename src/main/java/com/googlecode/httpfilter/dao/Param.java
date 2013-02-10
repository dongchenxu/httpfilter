package com.googlecode.httpfilter.dao;

import java.util.HashMap;

/**
 * ²ÎÊý´«µÝ
 * @author vlinux
 *
 */
public class Param extends HashMap<String,Object>{

	private static final long serialVersionUID = -5467863139406272322L;

	public Param add(String key, Object value) {
		this.put(key, value);
		return this;
	}
	
	public static Param create() {
		return new Param();
	}
	
}
