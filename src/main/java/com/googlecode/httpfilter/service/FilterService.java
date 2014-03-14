package com.googlecode.httpfilter.service;

import java.util.List;

import com.googlecode.httpfilter.domain.FilterDO;
import com.googlecode.httpfilter.domain.MultiResultDO;
import com.googlecode.httpfilter.domain.SingleResultDO;

public interface FilterService {

	/**
	 * 创建并存储FilterDO
	 * @param filterDO
	 * @return
	 */
	SingleResultDO<FilterDO> createFilterDO(FilterDO filterDO);
	
	/**
	 * 通过id获取FilterDO
	 * @param id
	 * @return
	 */
	SingleResultDO<FilterDO> getFilterById(long id);
	
	/**
	 * 通过id list获取 filterDO列表
	 * @param ids
	 * @return
	 */
	MultiResultDO<Long,FilterDO> searchFiltersByIds( List<Long> ids );
}
