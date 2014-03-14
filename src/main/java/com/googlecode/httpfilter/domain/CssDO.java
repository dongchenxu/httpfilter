package com.googlecode.httpfilter.domain;

import java.util.Date;

public class CssDO {

	private long id;
	private long cssNasId;
	private String element;
	private String cssContent;
	private long itemId;
	private Date gmtCreate;
	
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public long getItemId() {
		return itemId;
	}
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getCssNasId() {
		return cssNasId;
	}
	public void setCssNasId(long cssNasId) {
		this.cssNasId = cssNasId;
	}
	public String getElement() {
		return element;
	}
	public void setElement(String element) {
		this.element = element;
	}
	public String getCssContent() {
		return cssContent;
	}
	public void setCssContent(String cssContent) {
		this.cssContent = cssContent;
	}
}
