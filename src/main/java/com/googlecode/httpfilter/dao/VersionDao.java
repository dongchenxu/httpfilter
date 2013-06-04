package com.googlecode.httpfilter.dao;

import java.sql.SQLException;

import com.googlecode.httpfilter.domain.VersionDO;

public interface VersionDao {

	/**
	 * 创建VersionDO
	 * @param version
	 * @return
	 */
	long createVersionDO( VersionDO version ) throws SQLException;
	
	/**
	 * 通过ID获取VersionDO
	 * @param id
	 * @return
	 */
	VersionDO getVersionDOById( long id ) throws SQLException;
}
