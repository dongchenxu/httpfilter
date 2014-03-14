package com.googlecode.httpfilter.manager.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.dao.CommunicationDao;
import com.googlecode.httpfilter.domain.CommunicationDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.CommunicationManager;
import com.googlecode.httpfilter.util.ExceptionUtils;

@Service
public class CommunicationManagerImpl implements CommunicationManager {
	
	@Autowired
	private CommunicationDao comtDao;

	@Override
	public CommunicationDO createCommunication(CommunicationDO commt) throws BizException {
		try {
			final long comtId = comtDao.createComt(commt);
			return getComtById(comtId);
		} catch(SQLException e) {
			if( ExceptionUtils.isH2UniqueIndexException(e) ) {
				List<CommunicationDO> comts = getComtByTraceId(commt.getTraceId());
				if( CollectionUtils.isNotEmpty(comts) ) {
					return comts.iterator().next();
				}
			}
			throw new BizException("create comt failed.", e);
		}
	}

	@Override
	public CommunicationDO getComtById(long id) throws BizException {
		try{
			return comtDao.getComtById(id);
		}catch (SQLException e) {
			throw new BizException("get comt failed, comtId="+id, e);
		}
	}

	@Override
	public List<CommunicationDO> getComtByTraceId(String traceId) throws BizException {
		try {
			return comtDao.getComtByTraceId(traceId);
		} catch (SQLException e) {
			throw new BizException("get comt failed, traceId="+traceId, e);
		} catch (Exception e){
			throw new BizException("get comt failed, traceId="+traceId, e);
		}
	}
}
