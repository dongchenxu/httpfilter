package com.googlecode.httpfilter.manager.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.dao.FiltersDao;
import com.googlecode.httpfilter.domain.FiltersDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.FiltersManager;

@Service
public class FiltersManagerImpl implements FiltersManager {

	@Autowired
	FiltersDao filtersDao;
	@Override
	public FiltersDO createFiltersDO(FiltersDO filters) throws BizException {
		try {
			long id = filtersDao.createFilters(filters);
			return getFiltersById(id);
		} catch (SQLException e) {
			throw new BizException("create filtersDao fail.", e);
		}
	}

	@Override
	public FiltersDO getFiltersById(long id) throws BizException{
		try {
			return filtersDao.getFiltersById(id);
		} catch (SQLException e) {
			throw new BizException("get filtersDao fail.", e);
		}
	}

}
