package com.googlecode.httpfilter.dao.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.httpfilter.dao.ConnectionDao;
import com.googlecode.httpfilter.dao.Param;
import com.googlecode.httpfilter.domain.ConnectionDO;
@Repository
@Transactional
public class IbatisConnectionDao extends AbstractSqlMapClientDaoSupport
		implements ConnectionDao {

	@Override
	public long createConnection(ConnectionDO cont) throws SQLException {
		long id = generateContId();
		cont.setId(id);
		getSqlMapClient().insert("IbatisConnectionDao.createCont", cont);
		return id;
	}

	private long generateContId() throws SQLException {
		return (Long) getSqlMapClient().queryForObject(
				"IbatisConnectionDao.generateContId");
	}

	@Override
	public ConnectionDO getConnectionById(long id) throws SQLException {
		return (ConnectionDO) getSqlMapClient()
				.queryForObject("IbatisConnectionDao.getContById",
						Param.create().add("id", id));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ConnectionDO> getConnectionByComtId(long comtId, long minId)
			throws SQLException {
		return getSqlMapClient().queryForList("IbatisConnectionDao.getContByComtId",
				Param.create().add("comtId", comtId).add("minId",minId));
	}
}
