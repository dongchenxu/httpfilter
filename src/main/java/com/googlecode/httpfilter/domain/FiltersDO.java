package com.googlecode.httpfilter.domain;

import java.util.Date;

public class FiltersDO {

	private long id;
	private String filterIds;
	private Date gmtCreate;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFilterIds() {
		return filterIds;
	}
	public void setFilterIds(String filterIds) {
		this.filterIds = filterIds;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	
	
}
