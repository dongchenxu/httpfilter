package com.googlecode.httpfilter.manager;

import java.util.List;

import com.googlecode.httpfilter.domain.NasDO;

/**
 * Nas Manager
 * @author vlinux
 *
 */
public interface NasManager {

	/**
	 * 创建Nas存储
	 * @param nas
	 * @return 被创建的Nas
	 * @throws BizException
	 */
	NasDO createNas(NasDO nas) throws BizException;
	
	/**
	 * 根据ID获取Nas存储
	 * @param id
	 * @return Nas存储
	 * @throws BizException
	 */
	NasDO getNasById(long id) throws BizException;
	
	/**
	 * 根据批号查询一批Nas存储
	 * @param batchNo
	 * @return
	 * @throws BizException
	 */
	List<NasDO> listNasByBatchNo(String batchNo) throws BizException;
	
	/**
	 * 生成Nas存储批号
	 * @return
	 */
	String generateBatchNo();
	
}
