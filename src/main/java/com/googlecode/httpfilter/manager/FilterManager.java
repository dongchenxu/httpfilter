package com.googlecode.httpfilter.manager;

import com.googlecode.httpfilter.domain.FilterDO;

public interface FilterManager {
	
	/**
	 * 创建FilterDO
	 * @param filterDO
	 * @return
	 */
	FilterDO createFilterDO(FilterDO filterDO) throws BizException;
	
	/**
	 * 通过id查询FilterDO
	 * @param id
	 * @return
	 */
	FilterDO getFilterDOById( long id ) throws BizException;

}
