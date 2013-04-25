package com.googlecode.httpfilter.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.httpfilter.constant.ErrorCodeConstants;
import com.googlecode.httpfilter.domain.CssDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.CssManager;
import com.googlecode.httpfilter.service.CssService;

public class CssServiceImpl implements CssService {

	@Autowired
	CssManager cssManager;
	@Override
	public SingleResultDO<CssDO> createCssDO(CssDO cssDO) {
		final SingleResultDO<CssDO> result = new SingleResultDO<CssDO>();
		try {
			CssDO createCss = cssManager.createCssDO(cssDO);
			result.setModel(createCss);
		} catch (BizException e) {
			result.setSuccess(false);
			result.getErrMsg().putError(ErrorCodeConstants.CREATE_CONNECTION_ERROR);
		}
		return result;
	}

	@Override
	public SingleResultDO<CssDO> getCssDOById(long id) {
		final SingleResultDO<CssDO> result = new SingleResultDO<CssDO>();
		try {
			CssDO queryCss = cssManager.getCssDOById(id);
			result.setModel(queryCss);
		} catch (BizException e) {
			result.setSuccess(false);
			result.getErrMsg().putError(ErrorCodeConstants.CREATE_CONNECTION_ERROR);
		}
		return result;
	}

	@Override
	public SingleResultDO<List<CssDO>> getCssDOByItemId(long itemId) {
		final SingleResultDO<List<CssDO>> result = new SingleResultDO<List<CssDO>>();
		try {
			List<CssDO> queryCss = cssManager.getCssDOByItemId(itemId);
			result.setModel(queryCss);
		} catch (BizException e) {
			result.setSuccess(false);
			result.getErrMsg().putError(ErrorCodeConstants.CREATE_CONNECTION_ERROR);
		}
		return result;
	}

}
