package com.googlecode.httpfilter.manager;

import com.googlecode.httpfilter.domain.VersionDO;

public interface VersionManager {

	/**
	 * 创建versionDo
	 * @param version
	 * @return
	 */
	VersionDO createVersionDO( VersionDO version ) throws BizException;
	/**
	 * 通过Id获取versionDO
	 * @param id
	 * @return
	 */
	VersionDO getVersionDO( long id ) throws BizException;
}
