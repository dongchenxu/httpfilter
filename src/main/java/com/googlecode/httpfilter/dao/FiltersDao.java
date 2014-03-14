package com.googlecode.httpfilter.dao;

import java.sql.SQLException;

import com.googlecode.httpfilter.domain.FiltersDO;

public interface FiltersDao {
	
	long createFilters( FiltersDO filters ) throws SQLException;
	
	FiltersDO getFiltersById( long id ) throws SQLException;

}
