package com.googlecode.httpfilter.service;

import java.util.List;

import com.googlecode.httpfilter.domain.FilterDO;
import com.googlecode.httpfilter.domain.FiltersDO;
import com.googlecode.httpfilter.domain.SingleResultDO;

public interface FiltersService {

	SingleResultDO<FiltersDO> createFilters( FiltersDO filters );
	
	SingleResultDO<FiltersDO> getFiltersById( long id );
	
	/**
	 * 过滤器集合，filterIds字段存放了过滤器的ID，用逗号隔开
	 * @param filters
	 * @return
	 */
	List<FilterDO> filersDOToFliterList( FiltersDO filters );
	
	FiltersDO filterListToFiltersDO( List<FilterDO> filterList );
}
