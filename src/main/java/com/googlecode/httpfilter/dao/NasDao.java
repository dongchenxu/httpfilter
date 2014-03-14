package com.googlecode.httpfilter.dao;

import java.sql.SQLException;
import java.util.List;

import com.googlecode.httpfilter.domain.NasDO;

/**
 * Nas DAO
 * @author vlinux
 *
 */
public interface NasDao {

	/**
	 * 创建一个Nas存储
	 * @param Nas
	 * @return nasId
	 * @throws SQLException
	 */
	long createNas(NasDO Nas) throws SQLException;
	
	/**
	 * 根据ID获取一个Nas存储
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	NasDO getNasById(long id) throws SQLException;
	
	/**
	 * 根据BatchNo获取一批Nas存储
	 * @param batchNo
	 * @return
	 * @throws SQLException
	 */
	List<NasDO> listNasByBatchNo(String batchNo) throws SQLException;
	
}
