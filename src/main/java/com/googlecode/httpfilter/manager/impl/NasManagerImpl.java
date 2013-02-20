package com.googlecode.httpfilter.manager.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.dao.NasDao;
import com.googlecode.httpfilter.domain.NasDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.NasManager;
import com.googlecode.httpfilter.util.MD5Utils;

/**
 * NasManager 实现
 * @author vlinux
 *
 */
@Service
public class NasManagerImpl implements NasManager {

	@Autowired
	private NasDao nasDao;

	@Override
	public NasDO createNas(NasDO nas) throws BizException {
		try {
			nas.getFeaturesMap().put("MD5", MD5Utils.getMD5(nas.getContent()));
			final long nasId = nasDao.createNas(nas);
			return getNasById(nasId);
		} catch(SQLException e) {
			throw new BizException("create nas failed.", e);
		}
	}

	@Override
	public NasDO getNasById(long id) throws BizException {
		try {
			return nasDao.getNasById(id);
		} catch(SQLException e) {
			throw new BizException("get nas failed, nasId="+id, e);
		}
	}

	@Override
	public List<NasDO> listNasByBatchNo(String batchNo) throws BizException {
		try {
			return nasDao.listNasByBatchNo(batchNo);
		} catch(SQLException e) {
			throw new BizException("list nas of batch failed, batchNo="+batchNo, e);
		}
	}
	
	/**
	 * 生成批号
	 * @return
	 */
	@Override
	public String generateBatchNo() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
}
