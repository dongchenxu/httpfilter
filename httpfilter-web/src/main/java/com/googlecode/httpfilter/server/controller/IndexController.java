package com.googlecode.httpfilter.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	
	/**
	 * µ¼º½µ½Ö÷Ò³
	 * @return
	 */
	@RequestMapping("/index.do")
	public String viewIndex() {
		return "index";
	}
	
}
