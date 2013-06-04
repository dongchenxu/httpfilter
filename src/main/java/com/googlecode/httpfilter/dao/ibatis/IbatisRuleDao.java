package com.googlecode.httpfilter.dao.ibatis;

import java.sql.SQLException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.httpfilter.dao.Param;
import com.googlecode.httpfilter.dao.RuleDao;
import com.googlecode.httpfilter.domain.RuleDO;
@Repository
@Transactional
public class IbatisRuleDao extends AbstractSqlMapClientDaoSupport implements RuleDao{

	@Override
	public long createRule(RuleDO rule) throws SQLException {
		long id = generateRuleId();
		rule.setId(id);
		getSqlMapClient().insert("IbatisRuleDao.createRule", rule);
		return id;
	}
	
	private long generateRuleId() throws SQLException {
		return (Long) getSqlMapClient().queryForObject("IbatisRuleDao.generateRuleId");
	}

	@Override
	public RuleDO getRuleById(long id) throws SQLException {
		return (RuleDO)getSqlMapClient().queryForObject("IbatisRuleDao.getRuleById",
				Param.create().add("id", id));
	}

}
