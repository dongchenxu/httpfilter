package com.googlecode.httpfilter.manager;

import java.util.List;

import com.googlecode.httpfilter.domain.CommunicationDO;

public interface CommunicationManager {
	
	/**
	 * 创建CommunicationDO
	 * @param commt
	 * @return
	 */
	CommunicationDO createCommunication( CommunicationDO commt ) throws BizException;
	
	/**
	 * 通过id获取comtDO
	 * @param id
	 * @return
	 */
	CommunicationDO getComtById(long id) throws BizException;
	
	/**
	 * 通过traceId获取comtDO
	 * @param traceId
	 * @return
	 * @throws BizException
	 */
	List<CommunicationDO> getComtByTraceId(String traceId) throws BizException;
}
