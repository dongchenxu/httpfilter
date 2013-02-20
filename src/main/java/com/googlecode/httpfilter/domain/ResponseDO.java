package com.googlecode.httpfilter.domain;

import java.util.List;
import java.util.Map;

public class ResponseDO extends BaseDO {

	private static final long serialVersionUID = 319124307954523690L;
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
