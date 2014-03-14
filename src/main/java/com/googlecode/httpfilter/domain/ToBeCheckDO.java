package com.googlecode.httpfilter.domain;

import java.util.Date;

public class ToBeCheckDO {

	private long id;
	private long versionId;
	private long comtIdMain;
	private long comtIdCheck;
	private int mainEnvrmt;
	private int checkEnvrmt;
	private String sameReq;
	private boolean isCheck;
	private boolean isPass;
	private String features;
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
	public long getComtIdMain() {
		return comtIdMain;
	}
	public void setComtIdMain(long comtIdMain) {
		this.comtIdMain = comtIdMain;
	}
	public long getComtIdCheck() {
		return comtIdCheck;
	}
	public void setComtIdCheck(long comtIdCheck) {
		this.comtIdCheck = comtIdCheck;
	}
	
	public String getSameReq() {
		return sameReq;
	}
	public void setSameReq(String sameReq) {
		this.sameReq = sameReq;
	}
	public boolean getisCheck() {
		return isCheck;
	}
	public void setisCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	public void setMainEnvrmt(int mainEnvrmt) {
		this.mainEnvrmt = mainEnvrmt;
	}
	public int getMainEnvrmt() {
		return mainEnvrmt;
	}
	public void setCheckEnvrmt(int checkEnvrmt) {
		this.checkEnvrmt = checkEnvrmt;
	}
	public int getCheckEnvrmt() {
		return checkEnvrmt;
	}
	public void setIsPass(boolean isPass) {
		this.isPass = isPass;
	}
	public boolean getIsPass() {
		return isPass;
	}
	public void setFeatures(String features) {
		this.features = features;
	}
	public String getFeatures() {
		return features;
	}
}
