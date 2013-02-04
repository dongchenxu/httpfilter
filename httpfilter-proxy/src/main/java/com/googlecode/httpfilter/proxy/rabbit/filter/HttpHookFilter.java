package com.googlecode.httpfilter.proxy.rabbit.filter;

import java.nio.channels.SocketChannel;

import com.googlecode.httpfilter.proxy.rabbit.http.HttpHeader;
import com.googlecode.httpfilter.proxy.rabbit.proxy.Connection;
import com.googlecode.httpfilter.proxy.rabbit.proxy.HttpProxy;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;

/**
 * Http¹³×Ó
 * @author luanjia
 *
 */
public class HttpHookFilter implements HttpFilter {
	
	@Override
	public HttpHeader doHttpInFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpHeader doHttpOutFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpHeader doConnectFiltering(SocketChannel socket,
			HttpHeader header, Connection con) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setup(SProperties properties, HttpProxy proxy) {
		// TODO Auto-generated method stub
		
	}

}
