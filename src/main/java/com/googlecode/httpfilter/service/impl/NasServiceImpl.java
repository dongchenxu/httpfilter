package com.googlecode.httpfilter.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.domain.MultiResultDO;
import com.googlecode.httpfilter.domain.NasDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.manager.BizException;
import com.googlecode.httpfilter.manager.NasManager;
import com.googlecode.httpfilter.service.NasService;

@Service
public class NasServiceImpl implements NasService {

	private static final Logger logger = LoggerFactory.getLogger("httpfilter-biz");
	
	@Autowired
	private NasManager nasManager;
	
	@Override
	public SingleResultDO<String> createBatchNas(List<NasDO> batchNas) {
		final SingleResultDO<String> result = new SingleResultDO<String>();
		final String batchNo = nasManager.generateBatchNo();
		try {
			for( NasDO nas : batchNas ) {
				nas.setBatchNo(batchNo);
				final NasDO createNas = nasManager.createNas(nas);
				logger.debug("create nas for batch, batchNo={}, nasId={}", batchNo, createNas.getId());
			}
			result.setModel(batchNo);
			logger.info("batch create nas, batchNo={}", batchNo);
		} catch (BizException e) {
			logger.warn("batch create nas failed.", e);
			result.setSuccess(false);
		}//try
		return result;
	}

	@Override
	public MultiResultDO<NasDO> listNasByBatchNo(String batchNo) {
		final MultiResultDO<NasDO> result = new MultiResultDO<NasDO>();
		try {
			final List<NasDO> batchNas = nasManager.listNasByBatchNo(batchNo);
			result.setModels(batchNas);
		} catch (BizException e) {
			logger.warn("list nas by batchNo={} failed.", batchNo, e);
			result.setSuccess(false);
		}//try
		return result;
	}

}
