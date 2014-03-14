package com.googlecode.httpfilter.manager.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.dao.ConnectionDao;
import com.googlecode.httpfilter.domain.ConnectionDO;
import com.googlecode.httpfilter.domain.NasDO;
import com.googlecode.httpfilter.domain.RequestDO;
import com.googlecode.httpfilter.domain.ResponseDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.ConnectionManager;
import com.googlecode.httpfilter.manager.NasManager;
import com.googlecode.httpfilter.util.SerUtils;
@Service
public class ConnectionManagerImpl implements ConnectionManager {

	@Autowired
	ConnectionDao contDao;
	@Autowired
	NasManager nasManager;
	@Override
	public ConnectionDO createConnection(ConnectionDO cont) throws BizException {
		try {
			cont.setReqNasId(transferToNas(SerUtils.encode(cont.getReqDO())));
			cont.setResNasId(transferToNas(SerUtils.encode(cont.getResDO())));
			long id = contDao.createConnection(cont);
			return getConnectionById(id);
		} catch (SQLException e) {
			throw new BizException("create connectionDO fail", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new BizException("create connectionDO fail", e);
		}
	}

	@Override
	public ConnectionDO getConnectionById(long id) throws BizException {
		try {
			ConnectionDO cont = contDao.getConnectionById(id);
			RequestDO reqDO = SerUtils.decode( nasManager.getNasById( cont.getReqNasId() ).getContent() );
			cont.setReqDO(reqDO);
			ResponseDO resDO = SerUtils.decode( nasManager.getNasById( cont.getResNasId() ).getContent() );
			cont.setResDO(resDO);
			return cont;
		} catch (SQLException e) {
			throw new BizException("query connectionDO fail", e);
		} catch (IOException e) {
			throw new BizException("query connectionDO fail", e);
		}
	}

	@Override
	public List<ConnectionDO> getConnectionByComtId(long comtId, long minId) throws BizException {
		try {
			List<ConnectionDO> conts = contDao.getConnectionByComtId(comtId, minId);
			if( !CollectionUtils.isEmpty( conts ) ){
				for( ConnectionDO cont : conts ){
					RequestDO reqDO = SerUtils.decode( nasManager.getNasById( cont.getReqNasId() ).getContent() );
					cont.setReqDO(reqDO);
					ResponseDO resDO = SerUtils.decode( nasManager.getNasById( cont.getResNasId() ).getContent() );
					cont.setResDO(resDO);
				}
			}
			return conts;
		} catch (SQLException e) {
			throw new BizException("query connectionDO fail", e);
		} catch (IOException e) {
			throw new BizException("query connectionDO fail", e);
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
