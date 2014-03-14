package com.googlecode.httpfilter.service;

import java.util.List;

import com.googlecode.httpfilter.domain.MultiResultDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.domain.VersionDO;

public interface VersionService {

	/**
	 * 创建 VersionDO
	 * @param version
	 * @return
	 */
	SingleResultDO<VersionDO> createVersionDO( VersionDO version );
	
	/**
	 * 通过Id获取VersionDO
	 * @param id
	 * @return
	 */
	SingleResultDO<VersionDO> getVersionDOById( long id );
	
	/**
	 * 通过Ids获取VersionDO list
	 * @param ids
	 * @return
	 */
	MultiResultDO<Long, VersionDO> getVersionDOByIds( List<Long> ids );
}
