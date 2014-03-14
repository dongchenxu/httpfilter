package com.googlecode.httpfilter.manager.impl;

import java.sql.SQLException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.dao.FilterDao;
import com.googlecode.httpfilter.domain.FilterDO;
import com.googlecode.httpfilter.domain.NasDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.FilterManager;
import com.googlecode.httpfilter.manager.NasManager;

@Service
public class FilterManagerImpl implements FilterManager {

	@Autowired
	NasManager nasManager;
	@Autowired
	FilterDao filterDao;

	private final int NOT_HAS_NASIS = 0;

	@Override
	public FilterDO createFilterDO(FilterDO filterDO) throws BizException {
		try {
			if ((filterDO.getKey() == "" || filterDO.getKey() == null)
					&& (filterDO.getValue() != "" && filterDO.getValue() != null)) {
				NasDO nasDo = new NasDO();
				nasDo.setContent(filterDO.getValue().getBytes());
				nasDo.setBatchNo(generateBatchNo());
				long nasId = nasManager.createNas(nasDo).getId();
				filterDO.setValue(null);
				filterDO.setValueNasId(nasId);
			} else {
				filterDO.setValueNasId(NOT_HAS_NASIS);
			}

			long id = filterDao.createFilterDO(filterDO);
			return getFilterDOById(id);
		} catch (SQLException e) {
			throw new BizException("create filterDO fail", e);
		}
	}

	@Override
	public FilterDO getFilterDOById(long id) throws BizException {
		try {
			FilterDO filter = filterDao.getFilterDOById(id);
			if (filter.getValueNasId() != NOT_HAS_NASIS
					&& filter.getValue() == null) {
				String value = nasManager.getNasById(filter.getValueNasId())
						.getContent().toString();
				filter.setValue(value);
			}
		} catch (SQLException e) {
			throw new BizException("query filterDO fail by Id, id = " + id, e);
		}
		return null;
	}

	public String generateBatchNo() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
