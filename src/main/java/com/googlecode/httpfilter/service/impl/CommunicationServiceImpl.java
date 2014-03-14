package com.googlecode.httpfilter.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.constant.ErrorCodeConstants;
import com.googlecode.httpfilter.domain.CommunicationDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.CommunicationManager;
import com.googlecode.httpfilter.service.CommunicationService;
@Service
public class CommunicationServiceImpl implements CommunicationService {
	
	private static final Logger logger = LoggerFactory.getLogger("httpfilter-biz");
	@Autowired
	CommunicationManager communicationManager;
	@Override
	public SingleResultDO<CommunicationDO> createCommunication(CommunicationDO comt) {
		final SingleResultDO<CommunicationDO> result = new SingleResultDO<CommunicationDO>();
		try {
			final CommunicationDO createComt = communicationManager.createCommunication(comt);
			result.setModel(createComt);
		} catch (BizException e) {
			logger.warn("CommunicationDO create comt failed.", e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public SingleResultDO<List<CommunicationDO>> getCommunication(String traceId) {
		final SingleResultDO<List<CommunicationDO>> result = new SingleResultDO<List<CommunicationDO>>();
		try {
			final List<CommunicationDO> queryComt = communicationManager.getComtByTraceId(traceId);
			result.setModel(queryComt);
		} catch (BizException e) {
			result.getErrMsg().putError(ErrorCodeConstants.GET_COMMUNICATION_BY_TRACE_ID_ERROR, traceId);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public SingleResultDO<CommunicationDO> getCommunication(long id) {
		final SingleResultDO<CommunicationDO> result = new SingleResultDO<CommunicationDO>();
		try {
			final CommunicationDO queryComt = communicationManager.getComtById(id);
			result.setModel(queryComt);
		} catch (BizException e) {
			logger.warn("CommunicationDO query comt failed. id=" + id, e);
			result.setSuccess(false);
			result.getErrMsg().putError(ErrorCodeConstants.GET_COMMUNICATION_BY_ID_ERROR, id);
		}
		return result;
	}

	@Override
	public SingleResultDO<List<CommunicationDO>> fetchComtByTraceId(
			String traceId) {
		SingleResultDO<List<CommunicationDO>> result = new SingleResultDO<List<CommunicationDO>>();
		try {
			List<CommunicationDO> comts = communicationManager.getComtByTraceId(traceId);
			if( CollectionUtils.isEmpty( comts ) ){
				CommunicationDO commt = new CommunicationDO();
				commt.setTraceId(traceId);
				List<CommunicationDO> createComts = new ArrayList<CommunicationDO>();
				createComts.add(communicationManager.createCommunication(commt));
				result.setModel(createComts);
			}else{
				result.setModel(comts);
			}
		} catch (BizException e) {
			logger.warn("CommunicationDO fetch comt failed. traceId=" + traceId, e);
			result.setSuccess(false);
			result.putError(ErrorCodeConstants.CREATE_COMMUNICATION_ERROR, traceId);
		}
		return result;
	}

}
