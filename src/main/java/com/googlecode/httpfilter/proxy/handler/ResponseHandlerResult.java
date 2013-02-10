package com.googlecode.httpfilter.proxy.handler;


/**
 * 应答处理结果
 * @author vlinux
 *
 */
public class ResponseHandlerResult extends HandlerResult {

	private final DataBlock block;
	
	public ResponseHandlerResult(final DataBlock block) {
		this.block = block;
	}

	/**
	 * 获取数据块
	 * @return
	 */
	public DataBlock getBlock() {
		return block;
	}

}
