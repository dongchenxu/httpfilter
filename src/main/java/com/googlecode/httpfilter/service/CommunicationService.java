package com.googlecode.httpfilter.service;

import java.util.List;

import com.googlecode.httpfilter.domain.CommunicationDO;
import com.googlecode.httpfilter.domain.SingleResultDO;

public interface CommunicationService {
	/**
	 * 根据traceId创建会话，返回CommunicationDO
	 * @param traceId
	 * @return
	 */
	public SingleResultDO<CommunicationDO> createCommunication( CommunicationDO commtDO );

	/**
	 * 根据traceId获取CommunicationDO list
	 * @param traceId
	 * @return
	 */
	public SingleResultDO<List<CommunicationDO>> getCommunication( String traceId );
	/**
	 * 根据Id获取CommunicationDO
	 * @param id
	 * @return
	 */
	public SingleResultDO<CommunicationDO> getCommunication( long id );
	
	/**
	 * 查询communication，如果没有创建一个新的
	 * @param traceId
	 * @return
	 */
	SingleResultDO<List<CommunicationDO>> fetchComtByTraceId(String traceId);
	
}
