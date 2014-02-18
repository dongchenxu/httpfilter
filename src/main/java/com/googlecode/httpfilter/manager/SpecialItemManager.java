package com.googlecode.httpfilter.manager;

import java.util.List;

import com.googlecode.httpfilter.domain.ItemDO;

public interface SpecialItemManager {

	/**
	 * 根据csv文件名称列出其下所有商品信息
	 * @param specialCSV 文件名,格式:xxx.csv
	 * @return
	 */
//	List<ItemDO> listForSpecials(String specialCSV);
	
	/**
	 * 根据csv文件名称列出其下所有商品信息
	 * @param specialCSV 文件名,格式:xxx.csv
	 * @return
	 */
	List<ItemDO> listForSpecialsWithStyle(int style);
	/**
	 * 获取请求url
	 * @return
	 */
	String queryDataUrl();
}
