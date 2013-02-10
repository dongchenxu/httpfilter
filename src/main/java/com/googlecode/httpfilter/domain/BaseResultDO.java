package com.googlecode.httpfilter.domain;

/**
 * 返回结果基类
 * @author vlinux
 *
 */
public class BaseResultDO extends BaseDO {

	private static final long serialVersionUID = 1978227428619439353L;

	private boolean success = true; // 默认成功

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
