package com.googlecode.httpfilter.manager;

import java.util.List;

import com.googlecode.httpfilter.domain.ConnectionDO;

public interface ConnectionManager {

	/**
	 * 创建一个ConnectionDO
	 * @param cont
	 * @return
	 * @throws BizException
	 */
	ConnectionDO createConnection(ConnectionDO cont) throws BizException;
	
	/**
	 * 通过id获取ConnectionDO
	 * @param id
	 * @return
	 * @throws BizException
	 */
	ConnectionDO getConnectionById(long id) throws BizException;
	
	/**
	 * 根据traceId和minId获取
	 * @param traceId
	 * @param minId
	 * @return
	 */
	List<ConnectionDO> getConnectionByComtId( long comtId, long minId ) throws BizException;
	
}
