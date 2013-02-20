package com.googlecode.httpfilter.domain;

import java.util.Date;

public class FilterDO {

	private long id;
	private String reqUrl;
	private String key;
	private long valueNasId;
	private String value;
	private int operation;
	private Date gmtCreate;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getReqUrl() {
		return reqUrl;
	}
	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public long getValueNasId() {
		return valueNasId;
	}
	public void setValueNasId(long valueNasId) {
		this.valueNasId = valueNasId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getOperation() {
		return operation;
	}
	public void setOperation(int operation) {
		this.operation = operation;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
}
