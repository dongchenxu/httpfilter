package com.googlecode.httpfilter.proxy.rabbit.filter;

import com.googlecode.httpfilter.proxy.rabbit.html.HtmlBlock;
import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;

public class HtmlHockFilter extends HtmlFilter {

	public HtmlHockFilter() {
		
	}
	
	private HtmlHockFilter(Connection con, HttpHeader request,
			HttpHeader response) {
		super(con, request, response);
	}
	
	@Override
	public HtmlFilter newFilter(Connection con, HttpHeader request,
			HttpHeader response) {
		return new HtmlHockFilter(con, request, response);
	}

	@Override
	public void filterHtml(HtmlBlock block) {
//		for(Token t : block.getTokens()) {
//			
//			System.out.println(t.getText());
//			
//		}
	}

}
