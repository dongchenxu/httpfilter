package com.googlecode.httpfilter.manager.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.util.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.httpfilter.domain.CommunicationDO;
import com.googlecode.httpfilter.domain.ConnectionDO;
import com.googlecode.httpfilter.domain.RequestDO;
import com.googlecode.httpfilter.domain.ResponseDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.service.CommunicationService;
import com.googlecode.httpfilter.service.ConnectionService;
import com.googlecode.httpfilter.util.HttpFilterUtils;
import com.googlecode.httpliar.HttpLiarExchange;
import com.googlecode.httpliar.handler.HttpResponseHandler;
import com.googlecode.httpliar.handler.ResponseHandlerResult;
import com.googlecode.httpliar.handler.block.DataBlock;

@Service
public class HttpResponseHandlerImpl implements HttpResponseHandler {

	@Autowired
	CommunicationService comtService;
	
	@Autowired
	ConnectionService contService;
	
	private static final Logger logger = LoggerFactory.getLogger("httpResponse");
	
	@Override
	public boolean isHandleResponse(HttpLiarExchange exchange) {
		String uri = exchange.getRequestURI();
		String referUrl = HttpFilterUtils.getReferer(exchange
				.getRequestFields());
		if (uri.contains("trace_id=") || uri.contains("filter_ids=") )
			return true;
		if ( !StringUtils.isEmpty( referUrl ) && (referUrl.contains("trace_id=") || referUrl.contains("filter_ids=")))
			return true;
		return false;
	}

	@Override
	public ResponseHandlerResult handleResponse(HttpLiarExchange exchange,
			DataBlock block) throws Exception {
		String uri = exchange.getRequestURI();
		long comtId;
		String traceId = HttpFilterUtils.getTraceId(
				exchange.getRequestFields(), uri);
		SingleResultDO<List<CommunicationDO>> result = comtService.fetchComtByTraceId(traceId);
		 if( result.isSuccess() ){
			 comtId = result.getModel().get(0).getId();
			 ConnectionDO cont = new ConnectionDO();
			 cont.setComtId(comtId);
			 cont.setReqDO(createReqDO( exchange ));
			 cont.setResDO( createResDO( exchange, block ) );
			 cont.setServerIP( exchange.getAddress().toString() );
			 cont.setUrl(uri);
			 SingleResultDO<ConnectionDO> contResult = contService.createConnectionDO(cont);
			 if( !contResult.isSuccess() ){
				 logger.warn("创建connectionDO失败，traceId=" + traceId);
			 }
		 }else{
			 logger.warn("查询创建CommunicationId失败，traceId=" + traceId);
			 System.out.println("查询创建CommunicationId失败，traceId=" + traceId);
		 }
		return new ResponseHandlerResult(block);
	}
	
	private RequestDO createReqDO( HttpLiarExchange exchange ) throws IOException{
		RequestDO reqDO = new RequestDO();
		 final Map<String,List<String>> requestHeader = new HashMap<String,List<String>>();
		 final HttpFields reqHttpFields = exchange.getRequestFields();
		 final Enumeration<String> reqHttpFieldEnum = reqHttpFields.getFieldNames();
		 while( reqHttpFieldEnum.hasMoreElements() ) {
			 final String nameEnum = reqHttpFieldEnum.nextElement();
			 final Enumeration<String> reqHttpFieldValueEnum = reqHttpFields.getValues(nameEnum);
			 final List<String> values = new ArrayList<String>();
			 while( reqHttpFieldValueEnum.hasMoreElements() ) {
				 final String valueEnum = reqHttpFieldValueEnum.nextElement();
				 values.add(valueEnum);
			 }
			 requestHeader.put(nameEnum, values);
		 }
		 reqDO.setHeader(requestHeader);
		 
		 if( exchange.getRequestContent() != null ){
			 final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 exchange.getRequestContent().writeTo(baos);
			 reqDO.setContent(baos.toByteArray());
			 IO.close(baos);
		 }else{
			 reqDO.setContent(null);
		 }
		 return reqDO;
	}
	
	private ResponseDO createResDO( HttpLiarExchange exchange, DataBlock block ) throws IOException{
		ResponseDO resDO = new ResponseDO();
		 final Map<String,List<String>> responseHeader = new HashMap<String,List<String>>();
		 final HttpFields resHttpFields = exchange.getResponseFields();
		 final Enumeration<String> resHttpFieldEnum = resHttpFields.getFieldNames();
		 while( resHttpFieldEnum.hasMoreElements() ) {
			 final String nameEnum = resHttpFieldEnum.nextElement();
			 final Enumeration<String> resHttpFieldValueEnum = resHttpFields.getValues(nameEnum);
			 final List<String> values = new ArrayList<String>();
			 while( resHttpFieldValueEnum.hasMoreElements() ) {
				 final String valueEnum = resHttpFieldValueEnum.nextElement();
				 values.add(valueEnum);
			 }
			 responseHeader.put(nameEnum, values);
		 }
		 resDO.setHeader(responseHeader);
		 resDO.setContent( block.getDatas() );
		 return resDO;
	}

}
