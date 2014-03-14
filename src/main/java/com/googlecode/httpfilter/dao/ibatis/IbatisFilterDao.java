package com.googlecode.httpfilter.dao.ibatis;

import java.sql.SQLException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.httpfilter.dao.FilterDao;
import com.googlecode.httpfilter.dao.Param;
import com.googlecode.httpfilter.domain.FilterDO;
@Repository
@Transactional
public class IbatisFilterDao extends AbstractSqlMapClientDaoSupport implements
		FilterDao {

	@Override
	public long createFilterDO(FilterDO filter) throws SQLException {
		long id = generateFilterId();
		filter.setId(id);
		getSqlMapClient().insert("IbatisFilterDao.createFilter", filter);
		return id;
	}

	private long generateFilterId() throws SQLException {
		return (Long) getSqlMapClient().queryForObject(
				"IbatisFilterDao.generateFilterId");
	}

	@Override
	public FilterDO getFilterDOById(long id) throws SQLException {
		return (FilterDO)getSqlMapClient().queryForObject("IbatisFilterDao.getFilterById",
				Param.create().add("id", id));
	}

}
