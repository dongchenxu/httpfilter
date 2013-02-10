package com.googlecode.httpfilter.domain;

import java.util.List;

/**
 * 多结果返回对象
 * @author vlinux
 *
 * @param <T>
 */
public class MultiResultDO<T> extends BaseResultDO {

	private static final long serialVersionUID = -2000834975633519075L;

	private List<T> models;

	public List<T> getModels() {
		return models;
	}

	public void setModels(List<T> models) {
		this.models = models;
	}

}
