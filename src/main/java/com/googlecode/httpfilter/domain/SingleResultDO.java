package com.googlecode.httpfilter.domain;

/**
 * 返回单个结果
 * @author vlinux
 *
 * @param <T>
 */
public class SingleResultDO<T> extends BaseResultDO {

	private static final long serialVersionUID = 7987375914110334082L;
	
	private T model; //返回数据模型
	
	private ErrMsg errMsg = new ErrMsg();

	public T getModel() {
		return model;
	}

	public void setModel(T model) {
		this.model = model;
	}

	public ErrMsg getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(ErrMsg errMsg) {
		this.errMsg = errMsg;
	}

	public void putError(String errorCode, Object... args) {
		errMsg.putError(errorCode, args);
	}
	
}
