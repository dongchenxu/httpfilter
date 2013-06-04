package com.googlecode.httpfilter.dao.ibatis;

import java.sql.SQLException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.httpfilter.dao.Param;
import com.googlecode.httpfilter.dao.ToBeCheckDao;
import com.googlecode.httpfilter.domain.ToBeCheckDO;

@Repository
@Transactional
public class IbatisToBeCheckDao extends AbstractSqlMapClientDaoSupport implements ToBeCheckDao {

	@Override
	public long createToBeCheckDO(ToBeCheckDO toBeCheck) throws SQLException {
		long id = generateFilterId();
		toBeCheck.setId(id);
		getSqlMapClient().insert("IbatisToBeCheckDao.createToBeCheckDO", toBeCheck);
		return id;
	}

	@Override
	public ToBeCheckDO getToBeCheckDOById(long id) throws SQLException {
		return (ToBeCheckDO)getSqlMapClient().queryForObject("IbatisToBeCheckDao.getToBeCheckDOById",
				Param.create().add("id", id));
	}
	
	private long generateFilterId() throws SQLException {
		return (Long) getSqlMapClient().queryForObject(
				"IbatisToBeCheckDao.generateToBeCheckId");
	}
}
