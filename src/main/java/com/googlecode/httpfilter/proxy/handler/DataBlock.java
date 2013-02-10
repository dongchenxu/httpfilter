package com.googlecode.httpfilter.proxy.handler;

/**
 * 数据块
 * @author vlinux
 *
 */
public class DataBlock {

	// 数据存储块
	private final byte[] datas;
	
	public DataBlock(byte[] datas) {
		this.datas = datas;
	}

	/**
	 * 获取数据块中的数据
	 * @return
	 */
	public byte[] getDatas() {
		return datas.clone();
	}
	
}
