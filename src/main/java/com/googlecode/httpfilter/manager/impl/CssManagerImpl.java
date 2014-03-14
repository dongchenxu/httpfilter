package com.googlecode.httpfilter.manager.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.httpfilter.dao.CssDao;
import com.googlecode.httpfilter.domain.CssDO;
import com.googlecode.httpfilter.domain.NasDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.CssManager;
import com.googlecode.httpfilter.manager.NasManager;
import com.googlecode.httpfilter.util.SerUtils;

public class CssManagerImpl implements CssManager {

	@Autowired
	NasManager nasManager;
	@Autowired
	CssDao cssDao;
	
	@Override
	public CssDO createCssDO(CssDO css) throws BizException {
		try {
			css.setCssNasId( transferToNas( SerUtils.encode( css.getCssContent() ) ) );
			long id = cssDao.createCssDO(css);
			getCssDOById(id);
		} catch (SQLException e) {
			throw new BizException("create cssDO fail", e);
		} catch (IOException e) {
			throw new BizException("create cssDO fail", e);
		}
		return null;
	}

	@Override
	public CssDO getCssDOById(long id) throws BizException {
		
		try {
			return cssDao.getCssDOById(id);
		} catch (SQLException e) {
			throw new BizException("query cssDO fail", e);
		}
	}

	@Override
	public List<CssDO> getCssDOByItemId(long itemId) throws BizException {
		try {
			return cssDao.getCssDOByItemId(itemId);
		} catch (SQLException e) {
			throw new BizException("query cssDO fail", e);
		}
	}
	
	
	public long transferToNas(byte[] content) throws BizException{
		NasDO nasDo = new NasDO();
		nasDo.setContent(content);
		nasDo.setBatchNo(generateBatchNo());
		return nasManager.createNas(nasDo).getId();
	}
	
	public String generateBatchNo() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
