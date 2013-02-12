package com.googlecode.httpfilter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewIndexController {

	@RequestMapping("/index.do")
	public String viewIndex(){
		return "index";
	}
}
