package com.googlecode.httpfilter.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.constant.ErrorCodeConstants;
import com.googlecode.httpfilter.domain.FilterDO;
import com.googlecode.httpfilter.domain.MultiResultDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.FilterManager;
import com.googlecode.httpfilter.service.FilterService;

@Service
public class FilterServiceImpl implements FilterService {

	private static final Logger logger = LoggerFactory.getLogger("httpfilter-biz");
	
	@Autowired
	FilterManager fitlerManager;
	@Override
	public SingleResultDO<FilterDO> createFilterDO(FilterDO filterDO) {
		final SingleResultDO<FilterDO> result = new SingleResultDO<FilterDO>();
		try {
			result.setModel( fitlerManager.createFilterDO(filterDO) );
		} catch (BizException e) {
			logger.warn("filterDO create comt failed.", e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public SingleResultDO<FilterDO> getFilterById(long id) {
		final SingleResultDO<FilterDO> result = new SingleResultDO<FilterDO>();
		try {
			result.setModel( fitlerManager.getFilterDOById(id) );
		} catch (BizException e) {
			logger.warn("filterDO get filter failed. id = " + id, e);
			result.setSuccess(false);
		}
		return null;
	}

	@Override
	public MultiResultDO<Long, FilterDO> searchFiltersByIds(List<Long> ids) {
		final MultiResultDO<Long, FilterDO> result = new MultiResultDO<Long, FilterDO>();
		boolean isSuccess = false;
		for( long id : ids ){
			try {
				result.getModels().put(id, fitlerManager.getFilterDOById(id));
				isSuccess = true;
			} catch (BizException e) {
				logger.warn("filterDO get filter failed. id = " + id, e);
				result.putError(id, ErrorCodeConstants.GET_FILTER_BY_ID_ERROR);
			}
		}
		result.setSuccess(isSuccess);
		return result;
	}

}
