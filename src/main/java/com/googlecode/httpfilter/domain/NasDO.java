package com.googlecode.httpfilter.domain;

import java.util.Date;

/**
 * Nas
 * 
 * @author vlinux
 * 
 */
public class NasDO extends FeaturesSupportDO {

	private static final long serialVersionUID = 7793599864047883619L;

	private long id;
	private String batchNo;
	private Date gmtCreate;
	private byte[] content;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	
	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

}
