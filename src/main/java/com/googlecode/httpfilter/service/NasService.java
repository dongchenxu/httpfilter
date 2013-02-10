package com.googlecode.httpfilter.service;

import java.util.List;

import com.googlecode.httpfilter.domain.MultiResultDO;
import com.googlecode.httpfilter.domain.NasDO;
import com.googlecode.httpfilter.domain.SingleResultDO;

/**
 * Nas服务层
 * @author vlinux
 *
 */
public interface NasService {

	/**
	 * 创建一批NAS存储
	 * @param batchNas
	 * @return 存储批号
	 */
	SingleResultDO<String/*BatchNo*/> createBatchNas(List<NasDO> batchNas);
	
	/**
	 * 列出统一批号下的NAS存储
	 * @param batchNo
	 * @return
	 */
	MultiResultDO<NasDO> listNasByBatchNo(String batchNo);
	
}
