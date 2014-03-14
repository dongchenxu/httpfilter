package com.googlecode.httpfilter.service;

import java.util.List;

import com.googlecode.httpfilter.domain.ConnectionDO;
import com.googlecode.httpfilter.domain.SingleResultDO;

public interface ConnectionService {

	/**
	 * 创建会话
	 * @param conntDO
	 * @return
	 */
	public SingleResultDO<ConnectionDO> createConnectionDO( ConnectionDO conntDO );
	
	/**
	 * 根据ComtId和minContID最小ConnectionId获取所有请求
	 * @param traceId
	 * @return
	 */
	 SingleResultDO<List<ConnectionDO>> getConnectionByComtId(long comtId, long minContID);
	
	/**
	 * 根据ComtId获取所有请求
	 * @param traceId
	 * @return
	 */
	SingleResultDO<List<ConnectionDO>> getConnectionByComtId(long comtId);

	/**
	 * 根据traceId和minContID最小ConnectionId获取所有请求
	 * @param traceId
	 * @return
	 */
	 SingleResultDO<List<ConnectionDO>> getConnectionByTraceId(String traceId, long minContID);
	
	/**
	 * 根据traceId获取所有请求
	 * @param traceId
	 * @return
	 */
	SingleResultDO<List<ConnectionDO>> getConnectionByTraceId(String traceId);
	
	/**
	 * 根据Id获取所有请求
	 * @param Id
	 * @return
	 */
	public SingleResultDO<ConnectionDO> getConnectionById(long id);
	
	
	
}
