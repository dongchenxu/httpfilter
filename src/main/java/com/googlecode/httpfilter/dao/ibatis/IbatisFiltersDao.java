package com.googlecode.httpfilter.dao.ibatis;

import java.sql.SQLException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.httpfilter.dao.FiltersDao;
import com.googlecode.httpfilter.dao.Param;
import com.googlecode.httpfilter.domain.FiltersDO;
@Repository
@Transactional
public class IbatisFiltersDao extends AbstractSqlMapClientDaoSupport implements FiltersDao {

	@Override
	public long createFilters(FiltersDO filters) throws SQLException {
		long id = generateFiltersId();
		filters.setId(id);
		getSqlMapClient().insert("IbatisFiltersDao.createFilters", filters);
		return id;
	}
	
	private long generateFiltersId() throws SQLException {
		return (Long) getSqlMapClient().queryForObject(
				"IbatisFiltersDao.generateFiltersId");
	}

	@Override
	public FiltersDO getFiltersById(long id) throws SQLException {
		
		return (FiltersDO)getSqlMapClient().queryForObject("IbatisFiltersDao.getFiltersById",
				Param.create().add("id", id));
	}

}
