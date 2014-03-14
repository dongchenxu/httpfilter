package com.googlecode.httpfilter.dao;

import java.sql.SQLException;
import java.util.List;

import com.googlecode.httpfilter.domain.CommunicationDO;

public interface CommunicationDao {

	/**
	 * 创建存储CommunicationDO
	 * @param Nas
	 * @return
	 * @throws SQLException
	 */
	long createComt(CommunicationDO comtDO) throws SQLException;
	
	/**
	 * 通过ID获取ComtDO
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	CommunicationDO getComtById(long id) throws SQLException;
	
	/**
	 * 通过trace_id获取ComtDO
	 * @param traceId
	 * @return
	 * @throws SQLException
	 */
	List<CommunicationDO> getComtByTraceId(String traceId) throws SQLException;
}
