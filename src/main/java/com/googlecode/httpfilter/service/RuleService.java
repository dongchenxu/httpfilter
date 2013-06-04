package com.googlecode.httpfilter.service;

import java.util.List;

import com.googlecode.httpfilter.domain.MultiResultDO;
import com.googlecode.httpfilter.domain.RuleDO;
import com.googlecode.httpfilter.domain.SingleResultDO;

public interface RuleService {

	/**
	 * 创建比对规则
	 * @param ruleDO
	 * @return
	 */
	SingleResultDO<RuleDO> createRuleDO(RuleDO ruleDO);
	
	/**
	 * 通过id获取RuleDO
	 * @param id
	 * @return
	 */
	SingleResultDO<RuleDO> getRuleById(long id);
	
	/**
	 * 通过id list获取 RuleDO列表
	 * @param ids
	 * @return
	 */
	MultiResultDO<Long,RuleDO> searchRulesByIds( List<Long> ids );
}
