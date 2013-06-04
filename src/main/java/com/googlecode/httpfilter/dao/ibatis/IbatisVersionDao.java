package com.googlecode.httpfilter.dao.ibatis;

import java.sql.SQLException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.httpfilter.dao.Param;
import com.googlecode.httpfilter.dao.VersionDao;
import com.googlecode.httpfilter.domain.VersionDO;
@Repository
@Transactional
public class IbatisVersionDao extends AbstractSqlMapClientDaoSupport implements VersionDao {

	@Override
	public long createVersionDO(VersionDO version) throws SQLException {
		long id = generateFilterId();
		version.setId(id);
		getSqlMapClient().insert("IbatisVersionDao.createVersionDO", version);
		return id;
	}

	@Override
	public VersionDO getVersionDOById(long id) throws SQLException {
		return (VersionDO)getSqlMapClient().queryForObject("IbatisVersionDao.getVersionDOById",
				Param.create().add("id", id));
	}

	private long generateFilterId() throws SQLException {
		return (Long) getSqlMapClient().queryForObject(
				"IbatisVersionDao.generateToVersionId");
	}
}
