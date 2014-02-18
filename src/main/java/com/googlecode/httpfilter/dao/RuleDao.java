package com.googlecode.httpfilter.dao;

import java.sql.SQLException;
import java.util.List;

import com.googlecode.httpfilter.domain.RuleDO;

public interface RuleDao {

	long createRule(RuleDO rule) throws SQLException;

	RuleDO getRuleById(long id) throws SQLException;
	
	List<RuleDO> getAllRules() throws SQLException;
	
	int removeRuleById(long id) throws SQLException;
}
