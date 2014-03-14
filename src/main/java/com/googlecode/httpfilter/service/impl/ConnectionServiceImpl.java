package com.googlecode.httpfilter.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.constant.ErrorCodeConstants;
import com.googlecode.httpfilter.domain.CommunicationDO;
import com.googlecode.httpfilter.domain.ConnectionDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.ConnectionManager;
import com.googlecode.httpfilter.service.CommunicationService;
import com.googlecode.httpfilter.service.ConnectionService;

@Service
public class ConnectionServiceImpl implements ConnectionService {

	private static final Logger logger = LoggerFactory.getLogger("httpfilter-biz");
	
	@Autowired
	ConnectionManager contManager;
	@Autowired
	CommunicationService comtService;
	
	@Override
	public SingleResultDO<ConnectionDO> createConnectionDO(ConnectionDO conntDO) {
		final SingleResultDO<ConnectionDO> result = new SingleResultDO<ConnectionDO>();
		try {
			ConnectionDO createCont = contManager.createConnection(conntDO);
			result.setModel(createCont);
		} catch (BizException e) {
			logger.warn("connectionDO create comt failed.", e);
			result.setSuccess(false);
			result.getErrMsg().putError(ErrorCodeConstants.CREATE_CONNECTION_ERROR);
		}
		return result;
	}

	@Override
	public SingleResultDO<List<ConnectionDO>> getConnectionByComtId(long comtId,
			long minContID) {
		final SingleResultDO<List<ConnectionDO>> result = new SingleResultDO<List<ConnectionDO>>();
		try {
			List<ConnectionDO> createCont = contManager.getConnectionByComtId(comtId, minContID);
			result.setModel(createCont);
		} catch (BizException e) {
			logger.warn("connectionDO query comt failed.id=" + comtId + "minContID=" + minContID, e);
			result.setSuccess(false);
			result.putError(ErrorCodeConstants.GET_CONNECTION_BY_TRACE_ID_ERROR, comtId);
		}
		return result;
	}

	@Override
	public  SingleResultDO<List<ConnectionDO>> getConnectionByComtId(long comtId) {
		
		return getConnectionByComtId(comtId, -1);
	}

	@Override
	public SingleResultDO<ConnectionDO> getConnectionById(long id) {
		final SingleResultDO<ConnectionDO> result = new SingleResultDO<ConnectionDO>();
		try {
			ConnectionDO createCont = contManager.getConnectionById(id);
			result.setModel(createCont);
		} catch (BizException e) {
			logger.warn("connectionDO query comt failed.id=" + id, e);
			result.setSuccess(false);
			result.getErrMsg().putError(ErrorCodeConstants.GET_CONNECTION_BY_ID_ERROR, id);
		}
		return result;
	}

	@Override
	public SingleResultDO<List<ConnectionDO>> getConnectionByTraceId(
			String traceId, long minContID) {
		final SingleResultDO<List<ConnectionDO>> result = new SingleResultDO<List<ConnectionDO>>();
		SingleResultDO<List<CommunicationDO>>comtDOs = comtService.getCommunication(traceId);
		if( comtDOs.isSuccess() && CollectionUtils.isNotEmpty( comtDOs.getModel() ) ){
			long comtId = comtDOs.getModel().get(0).getId();
			return getConnectionByComtId(comtId, minContID);
		}else{
			logger.warn( "query comtId by traceId fail. traceId=" + traceId );
			result.setSuccess(false);
		}
		return null;
	}

	@Override
	public SingleResultDO<List<ConnectionDO>> getConnectionByTraceId(
			String traceId) {
		return getConnectionByTraceId(traceId, -1);
	}
	
}
