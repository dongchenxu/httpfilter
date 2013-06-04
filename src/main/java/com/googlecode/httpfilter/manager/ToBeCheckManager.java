package com.googlecode.httpfilter.manager;

import com.googlecode.httpfilter.domain.ToBeCheckDO;

public interface ToBeCheckManager {

	/**
	 * ¥¥Ω® ToBeCheckDO
	 * @param toBeCheckDO
	 * @return
	 */
	ToBeCheckDO createToBeCheckDO( ToBeCheckDO toBeCheckDO ) throws BizException;
	
	/**
	 * ≤È—ØToBeCheckDO
	 * @param id
	 * @return
	 */
	ToBeCheckDO getToBeCheckDOById( long id ) throws BizException;
	
}
