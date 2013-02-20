package com.googlecode.httpfilter.domain;

import java.util.List;
import java.util.Map;

public class RequestDO extends BaseDO {
	
	private static final long serialVersionUID = -5422431033044041899L;
	private Map<String, List<String>> header;
	private byte[] content;
	
	public Map<String, List<String>> getHeader() {
		return header;
	}
	public void setHeader(Map<String, List<String>> header) {
		this.header = header;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	
}
