package com.googlecode.httpfilter.service;

import java.util.List;

import com.googlecode.httpfilter.domain.CssDO;
import com.googlecode.httpfilter.domain.SingleResultDO;

public interface CssService {

	/**
	 * 创建CSSDO
	 * @param cssDO
	 * @return
	 */
	SingleResultDO<CssDO> createCssDO( CssDO cssDO );
	
	/**
	 * 通过id获取CSSDO
	 * @param id
	 * @return
	 */
	SingleResultDO<CssDO> getCssDOById(long id);
	
	/**
	 * 通过宝贝Id获取CSSDO 列表
	 * @param itemId
	 * @return
	 */
	SingleResultDO<List<CssDO>> getCssDOByItemId(long itemId); 
}
