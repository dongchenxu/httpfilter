package com.googlecode.httpfilter.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.constant.ErrorCodeConstants;
import com.googlecode.httpfilter.domain.MultiResultDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.domain.ToBeCheckDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.impl.ToBeCheckManagerImpl;
import com.googlecode.httpfilter.service.ToBeCheckService;

@Service
public class ToBeCheckServiceImpl implements ToBeCheckService {

	@Autowired
	ToBeCheckManagerImpl toBeCheckManager;
	private static final Logger logger = LoggerFactory.getLogger("httpfilter-biz");
	
	@Override
	public SingleResultDO<ToBeCheckDO> createToBeCheckDO(ToBeCheckDO toBeCheckDO) {
		final SingleResultDO<ToBeCheckDO> result = new SingleResultDO<ToBeCheckDO>();
		
		try {
			result.setModel(toBeCheckManager.createToBeCheckDO(toBeCheckDO));
		} catch (BizException e) {
			logger.warn("create ToBeCheckDO failed.", e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public SingleResultDO<ToBeCheckDO> getToBeCheckeDOById(long id) {
		final SingleResultDO<ToBeCheckDO> result = new SingleResultDO<ToBeCheckDO>();
		try {
			result.setModel( toBeCheckManager.getToBeCheckDOById(id) );
		} catch (BizException e) {
			logger.warn("query ToBeCheckDO failed. id = " + id, e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public MultiResultDO<Long, ToBeCheckDO> getToBeCheckDOByIds(List<Long> ids) {
		final MultiResultDO<Long, ToBeCheckDO> result = new MultiResultDO<Long, ToBeCheckDO>();
		boolean isSuccess = false;
		for( long id : ids ){
			try {
				result.getModels().put(id, toBeCheckManager.getToBeCheckDOById(id));
				isSuccess = true;
			} catch (BizException e) {
				logger.warn("ToBeCheckDO get ToBeCheckDO failed. id = " + id, e);
				result.putError(id, ErrorCodeConstants.GET_TOBECHECK_BY_ID_ERROR);
			}
		}
		result.setSuccess(isSuccess);
		return result;
	}

	@Override
	public SingleResultDO<List<ToBeCheckDO>> getAllToBeCheckDOByVersionId(
			long versionId) {
		final SingleResultDO<List<ToBeCheckDO>> result = new SingleResultDO<List<ToBeCheckDO>>();
		try{
			result.setModel( toBeCheckManager.getAllToBeCheckDOsByVersionId(versionId) );
		} catch (BizException e) {
			logger.warn("query ToBeCheckDO failed.", e);
			result.setSuccess(false);
		}
		return result;
	}

}
