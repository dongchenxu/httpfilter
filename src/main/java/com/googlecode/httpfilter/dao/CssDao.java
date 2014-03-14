package com.googlecode.httpfilter.dao;

import java.sql.SQLException;
import java.util.List;

import com.googlecode.httpfilter.domain.CssDO;

public interface CssDao {
	
	long createCssDO( CssDO css )throws SQLException;
	
	CssDO getCssDOById( long id )throws SQLException;
	
	List<CssDO> getCssDOByItemId( long item )throws SQLException;
}
