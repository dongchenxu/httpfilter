package com.googlecode.httpfilter.dao;

import java.sql.SQLException;

import com.googlecode.httpfilter.domain.FilterDO;

public interface FilterDao {
	
	/**
	 * 创建filterDO
	 * @param filter
	 * @return
	 */
	long createFilterDO(FilterDO filter) throws SQLException;
	/**
	 * 通过id获取FilterDO
	 * @param id
	 * @return
	 */
	FilterDO getFilterDOById(long id) throws SQLException;

}
