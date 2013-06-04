package com.googlecode.httpfilter.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.constant.ErrorCodeConstants;
import com.googlecode.httpfilter.domain.MultiResultDO;
import com.googlecode.httpfilter.domain.RuleDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.RuleManager;
import com.googlecode.httpfilter.service.RuleService;

@Service
public class RuleServiceImpl implements RuleService {

	@Autowired
	RuleManager ruleManager;
	
	private static final Logger logger = LoggerFactory.getLogger("httpfilter-biz");

	@Override
	public SingleResultDO<RuleDO> createRuleDO(RuleDO ruleDO) {
		final SingleResultDO<RuleDO> result = new SingleResultDO<RuleDO>();
		try {
			result.setModel( ruleManager.createRule(ruleDO) );
		} catch (BizException e) {
			logger.warn("ruleDO create comt failed.", e);
			result.setSuccess(false);
		}
		return result;
	}
	@Override
	public SingleResultDO<RuleDO> getRuleById(long id) {
		final SingleResultDO<RuleDO> result = new SingleResultDO<RuleDO>();
		try {
			result.setModel( ruleManager.getRuleById(id) );
		} catch (BizException e) {
			logger.warn("ruleDO get rule failed. id = " + id, e);
			result.setSuccess(false);
		}
		return null;
	}
	@Override
	public MultiResultDO<Long, RuleDO> searchRulesByIds(List<Long> ids) {
		final MultiResultDO<Long, RuleDO> result = new MultiResultDO<Long, RuleDO>();
		boolean isSuccess = false;
		for( long id : ids ){
			try {
				result.getModels().put(id, ruleManager.getRuleById(id));
				isSuccess = true;
			} catch (BizException e) {
				logger.warn("ruleDO get rule failed. id = " + id, e);
				result.putError(id, ErrorCodeConstants.GET_RULE_BY_ID_ERROR);
			}
		}
		result.setSuccess(isSuccess);
		return result;
	}
}
