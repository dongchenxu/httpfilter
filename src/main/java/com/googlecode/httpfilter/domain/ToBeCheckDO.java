package com.googlecode.httpfilter.domain;

import java.util.Date;

public class ToBeCheckDO {

	private long id;
	private long versionId;
	private String comtIdMain;
	private String comtIdCheck;
	private String mainEnvrmt;
	private String checkEnvrmt;
	private String sameReq;
	private boolean isCheck;
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
	public long getVersionId() {
		return versionId;
	}
	public void setVersionId(long versionId) {
		this.versionId = versionId;
	}
	public String getComtIdMain() {
		return comtIdMain;
	}
	public void setComtIdMain(String comtIdMain) {
		this.comtIdMain = comtIdMain;
	}
	public String getComtIdCheck() {
		return comtIdCheck;
	}
	public void setComtIdCheck(String comtIdCheck) {
		this.comtIdCheck = comtIdCheck;
	}
	public String getMainEnvrmt() {
		return mainEnvrmt;
	}
	public void setMainEnvrmt(String mainEnvrmt) {
		this.mainEnvrmt = mainEnvrmt;
	}
	public String getCheckEnvrmt() {
		return checkEnvrmt;
	}
	public void setCheckEnvrmt(String checkEnvrmt) {
		this.checkEnvrmt = checkEnvrmt;
	}
	public String getSameReq() {
		return sameReq;
	}
	public void setSameReq(String sameReq) {
		this.sameReq = sameReq;
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
}
