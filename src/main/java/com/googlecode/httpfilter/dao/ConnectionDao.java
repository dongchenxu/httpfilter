package com.googlecode.httpfilter.dao;

import java.sql.SQLException;
import java.util.List;

import com.googlecode.httpfilter.domain.ConnectionDO;

public interface ConnectionDao {

	/**
	 * 创建ConnectionDO
	 * @param cont
	 * @return
	 * @throws SQLException
	 */
	long createConnection(ConnectionDO cont) throws SQLException;
	
	/**
	 * 根据id查询ConnectionDO
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	ConnectionDO getConnectionById(long id) throws SQLException;
	
	/**
	 * 根据traceId和minId获取ConnectionDO
	 * @param traceId
	 * @param minId
	 * @return
	 * @throws SQLException
	 */
	List<ConnectionDO> getConnectionByComtId( long comtId, long minId ) throws SQLException;
}
