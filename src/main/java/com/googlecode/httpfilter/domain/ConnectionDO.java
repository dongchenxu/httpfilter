package com.googlecode.httpfilter.domain;

import java.util.Date;

public class ConnectionDO {

	private long id;
	private String url;
	private RequestDO reqDO;
	private long reqNasId;
	private ResponseDO resDO;
	private long resNasId;
	private String serverIP;
	private long comtId;
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getReqNasId() {
		return reqNasId;
	}
	public void setReqNasId(long reqNasId) {
		this.reqNasId = reqNasId;
	}
	public RequestDO getReqDO() {
		return reqDO;
	}
	public void setReqDO(RequestDO reqDO) {
		this.reqDO = reqDO;
	}
	public ResponseDO getResDO() {
		return resDO;
	}
	public void setResDO(ResponseDO resDO) {
		this.resDO = resDO;
	}
	public long getResNasId() {
		return resNasId;
	}
	public void setResNasId(long resNasId) {
		this.resNasId = resNasId;
	}
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	public long getComtId() {
		return comtId;
	}
	public void setComtId(long comtId) {
		this.comtId = comtId;
	}
	
	
}
