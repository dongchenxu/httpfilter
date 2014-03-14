package com.googlecode.httpfilter.domain;

import java.util.Date;

public class CommunicationDO extends FeaturesSupportDO{

	private static final long serialVersionUID = 8675202196627989834L;
	private long id;
	private String traceId;
	private Date gmtCreate;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getTraceId(){
		return traceId;
	}
	
	public void setTraceId( String traceId ){
		this.traceId = traceId;
	}
	
	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
}
