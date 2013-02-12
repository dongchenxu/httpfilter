package com.googlecode.httpfilter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class viewIndexController {

	@RequestMapping("/index.do")
	public String viewIndex(){
		return "index";
	}
}
