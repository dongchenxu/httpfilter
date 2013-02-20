package com.googlecode.httpfilter.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.googlecode.httpfilter.domain.ConnectionDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.service.ConnectionService;

@Controller
public class ViewIndexController {

	@RequestMapping("/index.do")
	public String viewIndex(){
		return "index";
	}
	private String[] title = { "ÐòºÅ", "URL", "×´Ì¬", "ÓòÃû", "¿Í»§¶ËIP" };
	@Autowired
	ConnectionService contService;
	
	@RequestMapping("/con.do")
	public String contView( @RequestParam("trace_id") String traceId, 
			HttpServletRequest request, ModelMap mod ) throws Exception {
		mod.addAttribute("title", title);
		SingleResultDO<List<ConnectionDO>> result = contService.getConnectionByTraceId(traceId);
		mod.addAttribute("resultList", result.getModel());
		return "con";
	}
	
	@RequestMapping("/openurl.do")
	public String openUrlView( ModelMap mod ){
		UUID uuid = UUID.randomUUID();
		mod.addAttribute( "trace_id", uuid.toString() );
		return "openurl";
	}
}
