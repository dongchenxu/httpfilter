package com.googlecode.httpfilter.manager;

import com.googlecode.httpfilter.domain.RuleDO;

public interface RuleManager {

	/**
	 * 创建CommunicationDO
	 * @param commt
	 * @return
	 */
	RuleDO createRule( RuleDO rule ) throws BizException;
	
	/**
	 * 通过id获取comtDO
	 * @param id
	 * @return
	 */
	RuleDO getRuleById( long id ) throws BizException;
	
}
