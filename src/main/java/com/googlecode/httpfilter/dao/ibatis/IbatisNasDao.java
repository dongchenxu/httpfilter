package com.googlecode.httpfilter.dao.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.httpfilter.dao.NasDao;
import com.googlecode.httpfilter.dao.Param;
import com.googlecode.httpfilter.domain.NasDO;

/**
 * NasµÄIbatisÊµÏÖ
 * @author vlinux
 *
 */
@Transactional
@Repository
public class IbatisNasDao extends AbstractSqlMapClientDaoSupport implements NasDao {

	@Override
	public long createNas(NasDO nas) throws SQLException {
		final long nasId = generateNasId();
		nas.setId(nasId);
		getSqlMapClient().insert("IbatisNasDao.createNas", nas);
		return nasId;
	}

	private long generateNasId() throws SQLException {
		return (Long) getSqlMapClient()
				.queryForObject("IbatisNasDao.generateNasId");
	}
	
	@Override
	public NasDO getNasById(long id) throws SQLException {
		return (NasDO) getSqlMapClient().queryForObject("IbatisNasDao.getNasById",
				Param.create().add("id", id));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NasDO> listNasByBatchNo(String batchNo) throws SQLException {
		return (List<NasDO>) getSqlMapClient().queryForList("IbatisNasDao.listNasByBatchNo",
				Param.create().add("batchNo", batchNo));
	}

}
