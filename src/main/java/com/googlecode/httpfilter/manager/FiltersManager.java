package com.googlecode.httpfilter.manager;

import com.googlecode.httpfilter.domain.FiltersDO;

public interface FiltersManager {

	FiltersDO createFiltersDO( FiltersDO filters ) throws BizException;
	
	FiltersDO getFiltersById( long id ) throws BizException;
}
