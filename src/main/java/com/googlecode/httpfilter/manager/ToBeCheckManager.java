package com.googlecode.httpfilter.manager;

import java.util.List;

import com.googlecode.httpfilter.domain.ToBeCheckDO;

public interface ToBeCheckManager {

	/**
	 * 创建 ToBeCheckDO
	 * @param toBeCheckDO
	 * @return
	 */
	ToBeCheckDO createToBeCheckDO( ToBeCheckDO toBeCheckDO ) throws BizException;
	
	/**
	 * 查询ToBeCheckDO
	 * @param id
	 * @return
	 */
	ToBeCheckDO getToBeCheckDOById( long id ) throws BizException;
	
	
	/**
	 * 查询ToBeCheckDO
	 * @param id
	 * @return
	 */
	List<ToBeCheckDO> getAllToBeCheckDOsByVersionId( long versionId) throws BizException;
	
}
