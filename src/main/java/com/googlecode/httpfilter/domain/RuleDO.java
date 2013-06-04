package com.googlecode.httpfilter.domain;

import java.util.Date;

public class RuleDO {

	private long id;
	private String keyWords;
	private long checkType;
	private String exceptFields;
	private Date gmtCreate;
	
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}
	public long getCheckType() {
		return checkType;
	}
	public void setCheckType(long checkType) {
		this.checkType = checkType;
	}
	public String getExceptFields() {
		return exceptFields;
	}
	public void setExceptFields(String exceptFields) {
		this.exceptFields = exceptFields;
	}
	
}
