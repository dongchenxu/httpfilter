package com.googlecode.httpfilter.manager;

import java.util.List;

import com.googlecode.httpfilter.domain.CssDO;

public interface CssManager {

	CssDO createCssDO(CssDO css) throws BizException;

	CssDO getCssDOById(long id) throws BizException;

	List<CssDO> getCssDOByItemId(long itemId) throws BizException;
}
