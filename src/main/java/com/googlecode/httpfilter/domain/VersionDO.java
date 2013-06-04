package com.googlecode.httpfilter.domain;

import java.util.Date;

public class VersionDO {

	private long id;
	private String ruleIds;
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
	public String getRuleIds() {
		return ruleIds;
	}
	public void setRuleIds(String ruleIds) {
		this.ruleIds = ruleIds;
	}
}
