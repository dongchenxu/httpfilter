package com.googlecode.httpfilter.dao.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.httpfilter.dao.CssDao;
import com.googlecode.httpfilter.dao.Param;
import com.googlecode.httpfilter.domain.CssDO;

@Repository
@Transactional
public class IbatisCssDao extends AbstractSqlMapClientDaoSupport implements
		CssDao {

	@Override
	public long createCssDO(CssDO css) throws SQLException {
		long id = generateCssId();
		css.setId(id);
		getSqlMapClient().insert("IbatisCssDao.createCss", css);
		return id;
	}

	private long generateCssId() throws SQLException {
		return (Long) getSqlMapClient().queryForObject(
				"IbatisCssDao.generateCssId");
	}

	@Override
	public CssDO getCssDOById(long id) throws SQLException {
		return (CssDO) getSqlMapClient().queryForObject(
				"IbatisCssDao.getCssById", Param.create().add("id", id));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CssDO> getCssDOByItemId(long itemId) throws SQLException {
		return getSqlMapClient().queryForList("IbatisCssDao.getCssByItemId",
				Param.create().add("itemId", itemId));
	}

}
