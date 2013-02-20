package com.googlecode.httpfilter.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.domain.FilterDO;
import com.googlecode.httpfilter.domain.FiltersDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.FiltersManager;
import com.googlecode.httpfilter.service.FilterService;
import com.googlecode.httpfilter.service.FiltersService;
@Service
public class FiltersServiceImpl implements FiltersService {

	private static final Logger logger = LoggerFactory.getLogger("httpfilter-biz");
	private final String regex = ",";
	@Autowired
	FilterService filterService;
	@Autowired
	FiltersManager filtersManager;
	@Override
	public SingleResultDO<FiltersDO> createFilters(FiltersDO filters) {
		final SingleResultDO<FiltersDO> result = new SingleResultDO<FiltersDO>();
		try {
			result.setModel( filtersManager.createFiltersDO(filters) );
		} catch (BizException e) {
			logger.warn("filtersDO create comt failed.", e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public SingleResultDO<FiltersDO> getFiltersById(long id) {
		final SingleResultDO<FiltersDO> result = new SingleResultDO<FiltersDO>();
		try {
			result.setModel(filtersManager.getFiltersById(id));
		} catch (BizException e) {
			logger.warn("filtersDO get comt failed.", e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public List<FilterDO> filersDOToFliterList(FiltersDO filters) {
		String filterIds = filters.getFilterIds();
		String[] ids = filterIds.split(regex);
		List<FilterDO> filterList = new ArrayList<FilterDO>();
		if( ArrayUtils.isEmpty( ids ) )
			return null;
		for( int index = 0; index < ids.length; index ++ )
		{
			if( ids[index] != null ){
				long id = NumberUtils.toLong( ids[index], -1 );
				SingleResultDO<FilterDO> resultDO = filterService.getFilterById(id);
				if( resultDO.isSuccess() ){
					filterList.add( resultDO.getModel() );
				}
			}
		}
		return filterList;
	}

	@Override
	public FiltersDO filterListToFiltersDO(List<FilterDO> filterList) {
		String ids = regex;
		for( FilterDO filter : filterList ){
			ids += filter.getId() + regex;
		}
		FiltersDO filters = new FiltersDO();
		filters.setFilterIds(ids);
		SingleResultDO<FiltersDO> result = createFilters( filters );
		if( result.isSuccess() )
			return result.getModel();
		else
			return null;
	}

}
