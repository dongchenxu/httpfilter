package com.googlecode.httpfilter.dao.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.httpfilter.dao.CommunicationDao;
import com.googlecode.httpfilter.dao.Param;
import com.googlecode.httpfilter.domain.CommunicationDO;

@Transactional
@Repository
public class IbatisCommunicationDao extends AbstractSqlMapClientDaoSupport implements CommunicationDao {

	@Override
	public long createComt(CommunicationDO comt) throws SQLException {
		final long id = generateComtId();
		comt.setId(id);
		getSqlMapClient().insert("IbatisCommunicationDao.createComt", comt);
		return id;
	}
	
	private long generateComtId() throws SQLException {
		return (Long) getSqlMapClient()
				.queryForObject("IbatisCommunicationDao.generateComtId");
	}

	@Override
	public CommunicationDO getComtById(long id) throws SQLException {
		return (CommunicationDO) getSqlMapClient().queryForObject("IbatisCommunicationDao.getComtById",
				Param.create().add("id", id));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CommunicationDO> getComtByTraceId(String traceId) throws SQLException {
//		List<CommunicationDO> comts = new ArrayList<CommunicationDO>();
//		try{
//			comts = getSqlMapClient().queryForList("IbatisCommunicationDao.getComtByTraceId",
//					Param.create().add("traceId", traceId));
//		}catch (Exception e) {
//			throw new SQLException("fail!!!! traceId=" + traceId, e);
//		}
//		return comts;
		return getSqlMapClient().queryForList("IbatisCommunicationDao.getComtByTraceId",
				Param.create().add("traceId", traceId));
	}

}
