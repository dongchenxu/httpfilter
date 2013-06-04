package com.googlecode.httpfilter.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.googlecode.httpfilter.domain.ConnectionDO;
import com.googlecode.httpfilter.domain.RuleDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.service.ConnectionService;
import com.googlecode.httpfilter.service.RuleService;

@Controller
public class ViewIndexController {

	@RequestMapping("/index.do")
	public String viewIndex() {
		return "index";
	}

	private String[] title = { "ÐòºÅ", "URL", "×´Ì¬", "ÓòÃû", "¿Í»§¶ËIP" };
	@Autowired
	ConnectionService contService;
	@Autowired
	RuleService ruleService;

	@RequestMapping("/con.do")
	public String contView(@RequestParam("trace_id") String traceId,
			HttpServletRequest request, ModelMap mod) throws Exception {
		mod.addAttribute("title", title);
		SingleResultDO<List<ConnectionDO>> result = contService
				.getConnectionByTraceId(traceId);
		mod.addAttribute("resultList", result.getModel());
		return "showcon";
	}

	@RequestMapping("/openurl.do")
	public String openUrlView(ModelMap mod) {
		UUID uuid = UUID.randomUUID();
		mod.addAttribute("trace_id", uuid.toString());
		return "openurl";
	}

	@RequestMapping("/createrule.do")
	public String createRuleView(ModelMap mod) {
		mod.addAttribute("luanjia", "luanjia");
		return "createrule";
	}

	@RequestMapping(value = "/addrule.do", method = RequestMethod.POST)
	public String addRuleView(@RequestParam("keyWords") String keyWords,
			@RequestParam("checkType") String checkType,
			@RequestParam("exceptFields") String exceptFields, ModelMap mod) {
		RuleDO ruleDO = new RuleDO();
		if( checkType.equalsIgnoreCase("0") ){
			ruleDO.setCheckType(1);
		}
		if( checkType.equalsIgnoreCase("1") ){
			ruleDO.setCheckType(2);
		}
		if( checkType.equalsIgnoreCase("2") ){
			ruleDO.setCheckType(4);
		}
		ruleDO.setKeyWords(keyWords);
		ruleDO.setExceptFields(exceptFields);
		SingleResultDO<RuleDO> result = ruleService.createRuleDO(ruleDO);
		RuleDO rule = result.getModel();
		
		mod.addAttribute("keyWords", rule.getKeyWords());
		mod.addAttribute("checkType", rule.getCheckType());
		mod.addAttribute("exceptFields", rule.getExceptFields());
		return "addrule";
	}
}
