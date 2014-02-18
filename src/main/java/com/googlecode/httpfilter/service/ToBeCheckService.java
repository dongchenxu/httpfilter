package com.googlecode.httpfilter.service;

import java.util.List;

import com.googlecode.httpfilter.domain.MultiResultDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.domain.ToBeCheckDO;

public interface ToBeCheckService {

	/**
	 * 创建ToBeCheckDO
	 * @param toBeCheckDO
	 * @return
	 */
	SingleResultDO<ToBeCheckDO> createToBeCheckDO( ToBeCheckDO toBeCheckDO );
	
	/**
	 * 通过id获取ToBeCheckDO
	 * @param id
	 * @return
	 */
	SingleResultDO<ToBeCheckDO> getToBeCheckeDOById( long id );
	
	/**
	 * 通过ids获取ToBeCheckDO
	 * @param ids
	 * @return
	 */
	MultiResultDO<Long, ToBeCheckDO> getToBeCheckDOByIds( List<Long> ids );
	
	/**
	 * 通过ids获取ToBeCheckDO
	 * @param ids
	 * @return
	 */
	SingleResultDO<List<ToBeCheckDO>> getAllToBeCheckDOByVersionId( long versionId );
}
