package com.googlecode.httpfilter.dao;

import java.sql.SQLException;

import com.googlecode.httpfilter.domain.RuleDO;

public interface RuleDao {

	long createRule(RuleDO rule) throws SQLException;

	RuleDO getRuleById(long id) throws SQLException;
}
