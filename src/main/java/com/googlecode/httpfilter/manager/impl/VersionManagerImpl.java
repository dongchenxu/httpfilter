package com.googlecode.httpfilter.manager.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.dao.VersionDao;
import com.googlecode.httpfilter.domain.VersionDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.VersionManager;

@Service
public class VersionManagerImpl implements VersionManager {

	@Autowired
	VersionDao ibatisVersionDao;
	
	@Override
	public VersionDO createVersionDO(VersionDO version) throws BizException{
		try {
			long id = ibatisVersionDao.createVersionDO(version);
			return getVersionDO(id);
		} catch (SQLException e) {
			throw new BizException("create VersionDO fail", e);
		}
	}

	@Override
	public VersionDO getVersionDO(long id) throws BizException {
		try {
			return ibatisVersionDao.getVersionDOById(id);
		} catch (SQLException e) {
			throw new BizException("get VersionDO fail", e);
		}
	}

}
