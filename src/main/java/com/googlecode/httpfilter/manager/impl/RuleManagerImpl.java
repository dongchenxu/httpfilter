package com.googlecode.httpfilter.manager.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.dao.RuleDao;
import com.googlecode.httpfilter.domain.RuleDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.RuleManager;

@Service
public class RuleManagerImpl implements RuleManager {
	
	@Autowired
	RuleDao ruleDao;

	public RuleDO createRule(RuleDO rule) throws BizException {
		try {
			long id = ruleDao.createRule(rule);
			return getRuleById(id);
		} catch (SQLException e) {
			throw new BizException("create ruleDO fail", e);
		}
	}

	public RuleDO getRuleById(long id) throws BizException {
		try {
			return ruleDao.getRuleById(id);
		} catch (SQLException e) {
			throw new BizException("query ruleDO fail by Id, id = " + id, e);
		}
	}

}
