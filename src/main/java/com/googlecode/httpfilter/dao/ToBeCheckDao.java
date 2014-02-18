package com.googlecode.httpfilter.dao;

import java.sql.SQLException;
import java.util.List;

import com.googlecode.httpfilter.domain.ToBeCheckDO;

public interface ToBeCheckDao {

	/**
	 * 创建ToBeCheckDO
	 * @param toBeCheck
	 * @return
	 */
	long createToBeCheckDO( ToBeCheckDO toBeCheck ) throws SQLException;
	
	/**
	 * 通过id获取ToBeCheckDO
	 * @param id
	 * @return
	 */
	ToBeCheckDO getToBeCheckDOById( long id ) throws SQLException;
	
	/**
	 * 通过versionId获取ToBeCheckDO
	 * @param id
	 * @return
	 */
	List<ToBeCheckDO> getToBeCheckDOsByVersionId( long versionId ) throws SQLException;
}
