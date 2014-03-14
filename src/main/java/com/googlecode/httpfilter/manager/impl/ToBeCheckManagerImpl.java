package com.googlecode.httpfilter.manager.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.dao.ToBeCheckDao;
import com.googlecode.httpfilter.domain.ToBeCheckDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.ToBeCheckManager;

@Service
public class ToBeCheckManagerImpl implements ToBeCheckManager{

	@Autowired
	ToBeCheckDao ibatisToBeCheckDao;
	
	@Override
	public ToBeCheckDO createToBeCheckDO(ToBeCheckDO toBeCheckDO) throws BizException {
		try {
			long id = ibatisToBeCheckDao.createToBeCheckDO(toBeCheckDO);
			return getToBeCheckDOById(id);
		} catch (SQLException e) {
			throw new BizException("create ToBeCheckDO fail", e);
		}
	}

	@Override
	public ToBeCheckDO getToBeCheckDOById(long id) throws BizException {
		try {
			return ibatisToBeCheckDao.getToBeCheckDOById(id);
		} catch (SQLException e) {
			throw new BizException("query ToBeCheckDO fail id = " + id, e);
		}
	}

	@Override
	public List<ToBeCheckDO> getAllToBeCheckDOsByVersionId(long versionId)
			throws BizException {
		try{
			return ibatisToBeCheckDao.getToBeCheckDOsByVersionId(versionId);
		} catch (SQLException e) {
			throw new BizException("query ToBeCheckDO fail", e);
		}
	}

}
