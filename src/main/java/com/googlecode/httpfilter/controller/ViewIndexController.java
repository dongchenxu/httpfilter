package com.googlecode.httpfilter.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.googlecode.httpfilter.constant.ErrorCodeConstants;
import com.googlecode.httpfilter.domain.CommunicationDO;
import com.googlecode.httpfilter.domain.ConnectionDO;
import com.googlecode.httpfilter.domain.ItemDO;
import com.googlecode.httpfilter.domain.LogHtmlDO;
import com.googlecode.httpfilter.domain.RuleDO;
import com.googlecode.httpfilter.domain.SingleResultDO;
import com.googlecode.httpfilter.domain.ToBeCheckDO;
import com.googlecode.httpfilter.domain.VersionDO;
import com.googlecode.httpfilter.manager.SpecialItemManager;
import com.googlecode.httpfilter.service.CommunicationService;
import com.googlecode.httpfilter.service.ConnectionService;
import com.googlecode.httpfilter.service.RuleService;
import com.googlecode.httpfilter.service.ToBeCheckService;
import com.googlecode.httpfilter.service.VersionService;

@Controller
public class ViewIndexController {

	@RequestMapping("/index.do")
	public String viewIndex() {
		return "index";
	}

	private String[] title = { "序号", "URL", "状态", "域名", "客户端IP" };
	private String[] comtTitle = { "序号", "链接" };
	private String[] itemTitle = { "宝贝id","宝贝标题", "链接" };
	private String[] ruleTitle = { "序号", "关键词", "校验类型", "免校验字段" };
	private String[] logTitle = { "宝贝ID", "TC", "结果", "详细结果" };
	
	private String detailskipPre = "42.120.82.62 detailskip.taobao.com m.ajax.taobao.com ajax.tbcdn.cn as.taobao.com tds.alicdn.com;42.120.82.62 pre.detailskip.taobao.com pre.ajax.tbcdn.cn m.ajax.taobao.com pre.tds.alicdn.com";
	private String detailskipBeta = "110.75.96.18 detailskip.taobao.com  ajax.tbcdn.cn  m.ajax.taobao.com  as.taobao.com";
	@Autowired
	ConnectionService contService;
	@Autowired
	RuleService ruleService;
	@Autowired
	VersionService versionService;
	@Autowired
	SpecialItemManager specialItemManager;
	@Autowired
	ToBeCheckService toBeCheckService;
	@Autowired
	CommunicationService comtService;

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
	
	@RequestMapping("/filter.do")
	public String welcomeView(ModelMap mod) {
		
		List<RuleDO> rules = new ArrayList<RuleDO>();
		//获取所有的规则
		SingleResultDO<List<RuleDO>> result = ruleService.searchAllRules();
		if( result.isSuccess() ){
			rules = result.getModel();
			mod.addAttribute( "rules", rules );
		}else{
			mod.addAttribute( "error", result.getErrMsg().getErrorCode() );
		}
		return "filter";
	}
	
	@RequestMapping("/rulelist.do")
	public String ruleListView(ModelMap mod) {
		List<RuleDO> rules = new ArrayList<RuleDO>();
		//获取所有的规则
		SingleResultDO<List<RuleDO>> result = ruleService.searchAllRules();
		if( result.isSuccess() ){
			rules = result.getModel();
			mod.addAttribute("rules", rules);
			mod.addAttribute("title", ruleTitle);
			mod.addAttribute("isSuccess", "成功");
		}else{
			mod.addAttribute("isSuccess", "失败");
		}
		return "rulelist";
	}
	
	@RequestMapping("/delrule.do")
	public String delRuleView( @RequestParam("chooseRule") String[] chooseRule, ModelMap mod ) {
		List<RuleDO> rules = new ArrayList<RuleDO>();
		if( chooseRule != null && chooseRule.length > 0 ){
			for( int index = 0; index < chooseRule.length; index ++ ){
				SingleResultDO<RuleDO>result = ruleService.delRuleById( Long.parseLong( chooseRule[index] ) );
				if( result.isSuccess() && result.getModel() != null ){
					rules.add(result.getModel());
				}
			}
			if( rules.size() == 0 ){
				mod.addAttribute("isSuccess", "失败");
			}else{
				mod.addAttribute("rules", rules);
				mod.addAttribute("title", ruleTitle);
				mod.addAttribute("isSuccess", "成功");
			}
		}else{
			mod.addAttribute("isSuccess", "失败");
		}
		return "delrule";
	}
	
	@RequestMapping("/createrule.do")
	public String createRuleView(ModelMap mod) {
		return "createrule";
	}

	/**
	 * 创建规则
	 * @param keyWords
	 * @param checkType
	 * @param exceptFields
	 * @param mod
	 * @return
	 */
	@RequestMapping(value = "/addrule.do")
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
	
	/**
	 * 创建对比记录
	 * 
	 * @param mainEnv
	 * @param checkEnv
	 * @param chooseRule
	 * @param mod
	 * @return
	 */
	@RequestMapping( value = "/addcompare.do" )
	public String addCompView(@RequestParam("mainType") String mainEnv,@RequestParam("checkType") String checkEnv, 
			@RequestParam("host") String host, @RequestParam("chooseRule") String[] chooseRule,	ModelMap mod){
		List<ToBeCheckDO> compares = new ArrayList<ToBeCheckDO>();
		VersionDO versionDO = new VersionDO();
		// 转换选择的规则
		String ruleTempStr = "";
		if( chooseRule != null && chooseRule.length > 0 ){
			for( int index = 0; index < chooseRule.length; index ++ ){
				ruleTempStr += chooseRule[index] + ";";
			}
		}else{
			mod.addAttribute( "isSucess", "添加队列失败" );
			return "addcompare";
		}
		
		// 创建运行版本
		versionDO.setRuleIds(ruleTempStr);
		SingleResultDO<VersionDO> vResult = versionService.createVersionDO(versionDO);
		if( vResult.isSuccess() ){
			Long versionId = vResult.getModel().getId();
			// 记录该版本的host
			if( Integer.parseInt(mainEnv) == 5 || Integer.parseInt( checkEnv ) == 5){
				// 待修改 ，更改host
				writeStrToHostFile( host, versionId );
			}
			//create compare
			if( isNotEmpty( mainEnv ) && isNotEmpty( checkEnv ) && checkOtherType(checkEnv, host) ){
				List<ItemDO> items = specialItemManager.listForSpecialsWithStyle(15);
				for( ItemDO item : items ){
					ToBeCheckDO beCheckDO = new ToBeCheckDO();
					beCheckDO.setMainEnvrmt( Integer.parseInt( mainEnv ) );
					beCheckDO.setCheckEnvrmt( Integer.parseInt( checkEnv ) );
					beCheckDO.setVersionId( versionId );
					beCheckDO.setisCheck(false);
					beCheckDO.setIsPass(false);
					beCheckDO.setSameReq( item.getId() + "" );
					
					// 创建Main and check EvnCOMTID
					beCheckDO.setComtIdMain( createCOMTID() );
					beCheckDO.setComtIdCheck( createCOMTID() );
					
					if( Integer.parseInt(mainEnv) == 5 || Integer.parseInt( checkEnv ) == 5){
						beCheckDO.setFeatures( "host:1" );
					}
					SingleResultDO<ToBeCheckDO> compare = toBeCheckService.createToBeCheckDO(beCheckDO);
					if( compare.isSuccess() ){
						compares.add(compare.getModel());
					}else{
						mod.addAttribute( "isSucess", "添加队列失败" );
						return "addcompare";
					}
				}
				
				mod.addAttribute( "mainEvn", analysisEvn( mainEnv ) );
				mod.addAttribute( "checkEvn", analysisEvn( checkEnv ) );
				mod.addAttribute( "versionId", versionId );
				
				// 创建 队列运行的excel
				List<String> mainUrlList = new ArrayList<String>();
				List<String> checkUrlList = new ArrayList<String>();
				SingleResultDO<List<ToBeCheckDO>> toBeCheckResult = toBeCheckService.getAllToBeCheckDOByVersionId( versionId );
				if( toBeCheckResult.isSuccess() && toBeCheckResult.getModel() != null && toBeCheckResult.getModel().size() != 0){
					List<ToBeCheckDO> toBeCheckDOs = toBeCheckResult.getModel();
					for( ToBeCheckDO tempDO : toBeCheckDOs ){
						String mainUrl = analysisEvnToUrl( tempDO.getMainEnvrmt() + "" );
						if( mainUrl != null && mainUrl != "" && tempDO.getSameReq() != null && tempDO.getSameReq() != "" && tempDO.getComtIdMain() != 0){
							mainUrl += tempDO.getSameReq();
							SingleResultDO<CommunicationDO> result = comtService.getCommunication( tempDO.getComtIdMain() );
							if( result.isSuccess() ){
								mainUrl += "&trace_id=" + result.getModel().getTraceId();
								mainUrlList.add( mainUrl );
							}
						}
						
						String checkUrl = analysisEvnToUrl( tempDO.getCheckEnvrmt() + "" );
						if( checkUrl != null && checkUrl != "" && tempDO.getSameReq() != null && tempDO.getSameReq() != "" && tempDO.getComtIdCheck() != 0){
							checkUrl += tempDO.getSameReq();
							SingleResultDO<CommunicationDO>  result = comtService.getCommunication( tempDO.getComtIdCheck() );
							if( result.isSuccess() ){
								checkUrl += "&trace_id=" + result.getModel().getTraceId();
								checkUrlList.add( checkUrl );
							}
						}
					}
				} else{
					mod.addAttribute( "isSucess", "添加队列成功，创建excel失败" );
					return "addcompare";
				}
				
				String mainFileName = "result_main_" + versionId + ".xls";
				String checkFileName = "result_check_" + versionId + ".xls";
				boolean mainIsSucess = writeToExcel( mainUrlList, mainFileName );
				boolean checkIsSucess = writeToExcel( checkUrlList, checkFileName );
				
				writeToCSV( mainUrlList, "csv_" + mainFileName );
				writeToCSV( checkUrlList, "csv_" + checkFileName );
				
				if(mainIsSucess && checkIsSucess ){
					mod.addAttribute( "isSucess", "添加队列成功,创建excel成功" );
					mod.addAttribute("mainUrlList", mainUrlList);
					mod.addAttribute("checkUrlList", checkUrlList);
					mod.addAttribute("comtTitle", comtTitle);
				}else{
					mod.addAttribute( "isSucess", "添加队列成功，创建excel失败" );
					return "addcompare";
				}
			}else{
				mod.addAttribute( "isSucess", "添加队列失败" );
				return "addcompare";
			}
		}else{
			mod.addAttribute( "isSucess", "添加队列失败" );
			return "addcompare";
		}
		
		return "addcompare";
	}
	
	/**
	 * 创建对比记录
	 * 
	 * @param mainEnv
	 * @param checkEnv
	 * @param chooseRule
	 * @param mod
	 * @return
	 */
	@RequestMapping( value = "/compareadd.do" )
	public String addCompViewNew(@RequestParam("mainType") String mainEnv,@RequestParam("checkType") String checkEnv, 
			@RequestParam("host") String host, @RequestParam("chooseRule") String[] chooseRule,	ModelMap mod){
		VersionDO versionDO = new VersionDO();
		List<String> mainUrlList = new ArrayList<String>();
		List<String> checkUrlList = new ArrayList<String>();
		int arrNum = 0;
		// 转换选择的规则
		String ruleTempStr = "";
		if( chooseRule != null && chooseRule.length > 0 ){
			for( int index = 0; index < chooseRule.length; index ++ ){
				ruleTempStr += chooseRule[index] + ";";
			}
		}else{
			mod.addAttribute( "isSucess", "添加队列失败" );
			return "compareadd";
		}
		
		// 创建运行版本
		versionDO.setRuleIds(ruleTempStr);
		SingleResultDO<VersionDO> vResult = versionService.createVersionDO(versionDO);
		if( vResult.isSuccess() ){
			Long versionId = vResult.getModel().getId();
			final int numItem = 100;
			// 记录该版本的host
			writeStrToHostFileNew( analysisEvnToHost( mainEnv, host ), "main", versionId );
			writeStrToHostFileNew( analysisEvnToHost( checkEnv, host ), "check", versionId );
			//create compare
			if( isNotEmpty( mainEnv ) && isNotEmpty( checkEnv ) && checkOtherType(checkEnv, host) ){
				List<ItemDO> items = specialItemManager.listForSpecialsWithEachStyle( numItem );
				for( ItemDO item : items ){
					ToBeCheckDO beCheckDO = new ToBeCheckDO();
					beCheckDO.setMainEnvrmt( Integer.parseInt( mainEnv ) );
					beCheckDO.setCheckEnvrmt( Integer.parseInt( checkEnv ) );
					beCheckDO.setVersionId( versionId );
					beCheckDO.setisCheck(false);
					beCheckDO.setIsPass(false);
					beCheckDO.setSameReq( item.getId() + "" );
					
					// 创建Main and check EvnCOMTID
					beCheckDO.setComtIdMain( createCOMTID() );
					beCheckDO.setComtIdCheck( createCOMTID() );
					
					if( Integer.parseInt(mainEnv) == 5 || Integer.parseInt( checkEnv ) == 5){
						beCheckDO.setFeatures( "host:1" );
					}
					SingleResultDO<ToBeCheckDO> compare = toBeCheckService.createToBeCheckDO( beCheckDO );
					
					// 创建 队列运行的excel
					if( compare.isSuccess() ){
						ToBeCheckDO tempDO = compare.getModel();
						// prepare mainUrl
						String mainUrl = analysisEvnToUrl( tempDO.getMainEnvrmt() + "" );
						if( mainUrl != null && mainUrl != "" && tempDO.getSameReq() != null && tempDO.getSameReq() != "" && tempDO.getComtIdMain() != 0){
							mainUrl += tempDO.getSameReq();
							SingleResultDO<CommunicationDO> result = comtService.getCommunication( tempDO.getComtIdMain() );
							if( result.isSuccess() ){
								mainUrl += "&trace_id=" + result.getModel().getTraceId();
								mainUrlList.add( mainUrl );
							}
						}
						// prepare checkUrl
						String checkUrl = analysisEvnToUrl( tempDO.getCheckEnvrmt() + "" );
						if( checkUrl != null && checkUrl != "" && tempDO.getSameReq() != null && tempDO.getSameReq() != "" && tempDO.getComtIdCheck() != 0){
							checkUrl += tempDO.getSameReq();
							SingleResultDO<CommunicationDO>  result = comtService.getCommunication( tempDO.getComtIdCheck() );
							if( result.isSuccess() ){
								checkUrl += "&trace_id=" + result.getModel().getTraceId();
								checkUrlList.add( checkUrl );
							}
						}
						// 每100个新建一个文件，并将其写入文件
						if( mainUrlList.size() >= 100 && checkUrlList.size() >= 100 ){
							String mainFileName = "result_main_" + versionId + "_" + arrNum + ".xls";
							String checkFileName = "result_check_" + versionId + "_" + arrNum + ".xls";
							
							boolean mainIsSucess = writeToCSVNew( mainUrlList, "csv_" + mainFileName, versionId );
							boolean checkIsSucess = writeToCSVNew( checkUrlList, "csv_" + checkFileName, versionId );
							
							if(mainIsSucess && checkIsSucess ){
								mod.addAttribute( "isSucess", "添加队列成功,创建excel成功" );
								arrNum ++;
								mainUrlList.clear();
								checkUrlList.clear();
							}else{
								mod.addAttribute( "isSucess", "添加队列成功，创建excel失败" );
								return "compareadd";
							}
						}
					}else{
						mod.addAttribute( "isSucess", "添加队列失败" );
						return "compareadd";
					}
				}
				
				mod.addAttribute( "mainEvn", analysisEvn( mainEnv ) );
				mod.addAttribute( "checkEvn", analysisEvn( checkEnv ) );
				mod.addAttribute( "versionId", versionId );
				mod.addAttribute( "arrNum", arrNum );
			}else{
				mod.addAttribute( "isSucess", "添加队列失败" );
				return "compareadd";
			}
		}else{
			mod.addAttribute( "isSucess", "添加队列失败" );
			return "compareadd";
		}
		
		return "compareadd";
	}
	
	/**
	 * 写入版本.xml文件
	 * @param xml
	 * @param versionId
	 */
	public void writeStrToHostFile(String xml, long versionId) {
		try {
			FileOutputStream fos = new FileOutputStream(new File( System.getProperty("user.dir") + "/data/host_version_" + versionId + ".xml"));
			Writer os = new OutputStreamWriter( fos, "UTF-8" );
			os.write( xml );
			os.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 写入版本.xml文件，文件名字添加环境变量
	 * @param xml
	 * @param versionId
	 */
	public void writeStrToHostFileNew(String xml, String evn, long versionId) {
		try {
			String dirName = System.getProperty("user.dir") + "/data/"+ versionId + "/host_version_" + evn + "_" + versionId + ".xml";
			File file = new File( dirName );
			File parent = file.getParentFile();
			if( parent != null && !parent.exists() ){
				parent.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream( file );
			Writer os = new OutputStreamWriter( fos, "UTF-8" );
			os.write( xml );
			os.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkOtherType( String checkEvn, String host ){
		int id = Integer.parseInt( checkEvn );
		if( id == 5 ){
			if( isNotEmpty(host) ){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	@RequestMapping( value = "/showlog.do", method = RequestMethod.GET )
	public String checkResultView(@RequestParam("versionId") String versionId, ModelMap mod){
		
		List<RuleDO> ruleDOList = new ArrayList<RuleDO>();
		List<String> keyWords = new ArrayList<String>();
		String keyWordstr = ";";
		
		SingleResultDO<List<ToBeCheckDO>> toBeCheckResult = toBeCheckService.getAllToBeCheckDOByVersionId( Long.parseLong( versionId ) );
		SingleResultDO<VersionDO> vDO = versionService.getVersionDOById( Long.parseLong( versionId ) );
		if( vDO.isSuccess() ){
			String rulesStr = vDO.getModel().getRuleIds();
			String[] rules = rulesStr.split( ";" );
			if( rules.length > 0 ){
				for( int index = 0; index < rules.length; index ++ ){
					SingleResultDO<RuleDO> result = ruleService.getRuleById( Long.parseLong( rules[index] ) );
					if( result.isSuccess() ){
						ruleDOList.add( result.getModel() );
						String str = result.getModel().getKeyWords();
						keyWords.add( str );
						keyWordstr = keyWordstr + str + ";";
					}
				}
			}
		}
		if( toBeCheckResult.isSuccess() && toBeCheckResult.getModel() != null && toBeCheckResult.getModel().size() != 0){
			List<ToBeCheckDO> toBeCheckDOs = toBeCheckResult.getModel();
			List<LogHtmlDO> logs = new ArrayList<LogHtmlDO>();
			
			String logPaths = "{\"paths\": [" + "\r\n";

			for( int i =0; i < toBeCheckDOs.size(); i ++ ){
				
				ToBeCheckDO tempDO = toBeCheckDOs.get(i);
				//日志
				String logPathName = "{\"path_name\": \""+ versionId + "_";
				String logMethods = "\"methods\": [" + "\r\n";
				String logPath = "{";
				
				// html日志
				String htmlPathName = "";
				
				long mainComtId = tempDO.getComtIdMain();
				long checkComtId = tempDO.getComtIdCheck();
				
				SingleResultDO<List<ConnectionDO>> mainResult = contService.getConnectionByComtId(mainComtId);
				SingleResultDO<List<ConnectionDO>> checkResult = contService.getConnectionByComtId(checkComtId);
				
				if( mainResult.isSuccess() && checkResult.isSuccess() && mainResult.getModel() != null && checkResult.getModel() != null ){
					List<ConnectionDO> mainContResult = mainResult.getModel();
					List<ConnectionDO> checkContResult = checkResult.getModel();
					logPathName += tempDO.getSameReq() + "\",";
					htmlPathName += tempDO.getSameReq();
					for( int index = 0; index < ruleDOList.size(); index++ ){
						RuleDO rule = ruleDOList.get(index);

						switch ( (int) rule.getCheckType() ) {
						case 1:// 1:请求有发出
							SingleResultDO<List<ConnectionDO>> result = checkHasRequest( mainContResult, checkContResult, rule.getKeyWords() );
							tempDO.setisCheck(true);
							logMethods += createLogMethod( rule.getKeyWords(), "是否存在", tempDO.getSameReq(), result );
							logs.add( createHtmlLogMethod( htmlPathName, rule.getKeyWords(), "是否存在", tempDO.getSameReq(), result ) );
							break;
						case 2:// 2:校验request信息
							String eptFields = rule.getExceptFields();
							SingleResultDO<List<ConnectionDO>> reqResult;
							if( isNotEmpty(eptFields) ){
								String[] eptArr = eptFields.split(";");
								reqResult = checkRequest( mainContResult, checkContResult, rule.getKeyWords(), eptArr );
							}else{
								reqResult = checkRequest( mainContResult, checkContResult, rule.getKeyWords() );
							}
							tempDO.setisCheck(true);
							logs.add( createHtmlLogMethod( htmlPathName, rule.getKeyWords(), "request信息", tempDO.getSameReq(), reqResult ) );
							logMethods += createLogMethod( rule.getKeyWords(), "request信息", tempDO.getSameReq(), reqResult);
							break;
						case 4:// 3:校验response信息
							SingleResultDO<List<ConnectionDO>> resResult = checkResponse( getComtTraceId( mainComtId ), getComtTraceId( checkComtId ), mainContResult, checkContResult, rule.getKeyWords() );
							tempDO.setisCheck(true);
							logs.add( createHtmlLogMethod( htmlPathName, rule.getKeyWords(), "response信息", tempDO.getSameReq(), resResult ) );
							logMethods += createLogMethod( rule.getKeyWords(), "response信息", tempDO.getSameReq(), resResult);
							break;
						default:
							logMethods += createLogMethod( rule.getKeyWords(), "错误：没有匹配到校验规则", tempDO.getSameReq(), false);
							break;
						}
						if( index != ruleDOList.size() - 1 ){
							logMethods += ",\r\n";
						}
					}
				}
				
				// 组装 Path
				logPath = logPathName + logMethods + "]}";
				
				if( i == toBeCheckDOs.size() - 1 ){
					logPaths += logPath;
				}else{
					logPaths += logPath + ",\r\n";
				}
			}
			// 组装 Paths
			logPaths += "]}";
			writeStrToFile( logPaths, versionId );
			
			mod.addAttribute( "versionId", versionId );
			mod.addAttribute("logs", logs);
			mod.addAttribute( "logTitle", logTitle );
		}
		return "showlog";
	}
	
	
	@RequestMapping( value = "/showlognew.do", method = RequestMethod.GET )
	public String checkResultViewNew(@RequestParam("versionId") String versionId, @RequestParam("arrnum") String arrNum,
			ModelMap mod){
		
		List<RuleDO> ruleDOList = new ArrayList<RuleDO>();
		List<String> keyWords = new ArrayList<String>();
		String keyWordstr = ";";
		
		SingleResultDO<List<ToBeCheckDO>> toBeCheckResult = toBeCheckService.getAllToBeCheckDOByVersionId( Long.parseLong( versionId ) );
		SingleResultDO<VersionDO> vDO = versionService.getVersionDOById( Long.parseLong( versionId ) );
		if( vDO.isSuccess() ){
			String rulesStr = vDO.getModel().getRuleIds();
			String[] rules = rulesStr.split( ";" );
			if( rules.length > 0 ){
				for( int index = 0; index < rules.length; index ++ ){
					SingleResultDO<RuleDO> result = ruleService.getRuleById( Long.parseLong( rules[index] ) );
					if( result.isSuccess() ){
						ruleDOList.add( result.getModel() );
						String str = result.getModel().getKeyWords();
						keyWords.add( str );
						keyWordstr = keyWordstr + str + ";";
					}
				}
			}
		}
		if( toBeCheckResult.isSuccess() && toBeCheckResult.getModel() != null && toBeCheckResult.getModel().size() != 0){
			List<ToBeCheckDO> toBeCheckDOs = toBeCheckResult.getModel();
			List<LogHtmlDO> logs = new ArrayList<LogHtmlDO>();
			String logPaths = "{\"paths\": [" + "\r\n";
			for( int i =0; i < toBeCheckDOs.size(); i ++ ){
				ToBeCheckDO tempDO = toBeCheckDOs.get(i);
				//日志
				String logPathName = "{\"path_name\": \""+ versionId + "_";
				String logMethods = "\"methods\": [" + "\r\n";
				String logPath = "{";
				
				// html日志
				String htmlPathName = "";
				
				long mainComtId = tempDO.getComtIdMain();
				long checkComtId = tempDO.getComtIdCheck();
				
				SingleResultDO<List<ConnectionDO>> mainResult = contService.getConnectionByComtId(mainComtId);
				SingleResultDO<List<ConnectionDO>> checkResult = contService.getConnectionByComtId(checkComtId);
				
				if( mainResult.isSuccess() && checkResult.isSuccess() && mainResult.getModel() != null && checkResult.getModel() != null ){
					List<ConnectionDO> mainContResult = mainResult.getModel();
					List<ConnectionDO> checkContResult = checkResult.getModel();
					logPathName += tempDO.getSameReq() + "\",";
					htmlPathName += tempDO.getSameReq();
					for( int index = 0; index < ruleDOList.size(); index++ ){
						RuleDO rule = ruleDOList.get(index);

						switch ( (int) rule.getCheckType() ) {
						case 1:// 1:请求有发出
							SingleResultDO<List<ConnectionDO>> result = checkHasRequest( mainContResult, checkContResult, rule.getKeyWords() );
							tempDO.setisCheck(true);
							logMethods += createLogMethod( rule.getKeyWords(), "是否存在", tempDO.getSameReq(), result );
							logs.add( createHtmlLogMethod( htmlPathName, rule.getKeyWords(), "是否存在", tempDO.getSameReq(), result ) );
							break;
						case 2:// 2:校验request信息
							String eptFields = rule.getExceptFields();
							SingleResultDO<List<ConnectionDO>> reqResult;
							if( isNotEmpty(eptFields) ){
								String[] eptArr = eptFields.split(";");
								reqResult = checkRequest( mainContResult, checkContResult, rule.getKeyWords(), eptArr );
							}else{
								reqResult = checkRequest( mainContResult, checkContResult, rule.getKeyWords() );
							}
							tempDO.setisCheck(true);
							logs.add( createHtmlLogMethod( htmlPathName, rule.getKeyWords(), "request信息", tempDO.getSameReq(), reqResult ) );
							logMethods += createLogMethod( rule.getKeyWords(), "request信息", tempDO.getSameReq(), reqResult);
							break;
						case 4:// 3:校验response信息
							SingleResultDO<List<ConnectionDO>> resResult = checkResponse( getComtTraceId( mainComtId ), getComtTraceId( checkComtId ), mainContResult, checkContResult, rule.getKeyWords() );
							tempDO.setisCheck(true);
							logs.add( createHtmlLogMethod( htmlPathName, rule.getKeyWords(), "response信息", tempDO.getSameReq(), resResult ) );
							logMethods += createLogMethod( rule.getKeyWords(), "response信息", tempDO.getSameReq(), resResult);
							break;
						default:
							logMethods += createLogMethod( rule.getKeyWords(), "错误：没有匹配到校验规则", tempDO.getSameReq(), false);
							break;
						}
						if( index != ruleDOList.size() - 1 ){
							logMethods += ",\r\n";
						}
					}
				}
				
				// 组装 Path
				logPath = logPathName + logMethods + "]}";
				
				if( i == toBeCheckDOs.size() - 1 ){
					logPaths += logPath;
				}else{
					logPaths += logPath + ",\r\n";
				}
			}
			// 组装 Paths
			logPaths += "]}";
			writeStrToFile( logPaths, versionId );
			
			mod.addAttribute( "versionId", versionId );
			mod.addAttribute("logs", logs);
			mod.addAttribute( "logTitle", logTitle );
		}
		return "showlognew";
	}
	
	public String getComtTraceId( long comtId ){
		SingleResultDO<CommunicationDO>  comtDO = comtService.getCommunication( comtId );
		if( comtDO.isSuccess() ){
			return comtDO.getModel().getTraceId();
		}else{
			return "";
		}
	}
	
	/**
	 * 写入版本.xml文件
	 * @param xml
	 * @param versionId
	 */
	public void writeStrToFile(String xml, String versionId) {
		try {
			FileOutputStream fos = new FileOutputStream(new File( System.getProperty("user.dir") + "/data/version_" + versionId + ".xml"));
			Writer os = new OutputStreamWriter( fos, "UTF-8" );
			os.write( xml );
			os.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取版本.xml文件
	 * @param xml
	 * @param versionId
	 */
	public String readStrToFile( String versionId ) {
        Reader reader  =   null ;
        String str = "";
		try  {
            reader = new InputStreamReader( new FileInputStream( new File( System.getProperty("user.dir") + "/data/version_" + versionId + ".xml" )) );
             int  tempchar;
             while  ((tempchar  =  reader.read())  !=   - 1 ) {
                 if  ((( char ) tempchar)  !=   '\r' ) {
                	 str += (char)tempchar;
                }
            }
            reader.close();
        }  catch  (Exception e) {
            e.printStackTrace();
        }
        return str;
	}
	
	public String createLogMethod( String keyWords, String ruleType, String sameId, SingleResultDO<List<ConnectionDO>> resultDO ){
		String str = "{";
		String tcId = "\"tc_id\": \"" + keyWords + "_" + ruleType + "_" + sameId + "\",";
		String methodName = "\"method_name\": \"" + keyWords + "请求" + ruleType + "校验\",";
		String doc = "\"method_name\": \"" + keyWords + "请求" + ruleType + "校验\",";
		String result = "\"result\": \"" + resultDO.isSuccess() + "\",";
		String detail = "\"detail\":\"";
		if( resultDO.isSuccess() ){
			detail += "校验成功";
			if( resultDO.getErrMsg() != null && resultDO.getErrMsg().getErrorCode() !=null && resultDO.getErrMsg().getErrorCode() != "" ){
				detail += "，但" + resultDO.getErrMsg() + "\"";
			}else{
				detail += "\"";
			}
		}else{
			detail += resultDO.getErrMsg() + "\"";
		}
		
		str += tcId + methodName + doc + result + detail + "}";
		return str;
	}
	
	public LogHtmlDO createHtmlLogMethod( String pathName, String keyWords, String ruleType, String sameId, SingleResultDO<List<ConnectionDO>> result){
		LogHtmlDO lhDO = new LogHtmlDO();
		
		lhDO.setPathName( pathName );
		lhDO.setTcName( keyWords + "_" + ruleType + "_" + sameId );
		lhDO.setResult( result.isSuccess() );
		if( result.isSuccess() ){
			if( result.getErrMsg() != null && result.getErrMsg().getErrorCode() !=null && result.getErrMsg().getErrorCode() != "" ){
				lhDO.setDetail( result.getErrMsg().getErrorCode() );
			}else{
				lhDO.setDetail( "校验通过" );
			}
		}else{
			lhDO.setDetail( result.getErrMsg().getErrorCode() );
		}
		return lhDO;
	}
	
	public String createLogMethod( String keyWords, String ruleType, String sameId, boolean isSuccess ){
		String str = "{";
		String tcId = "\"tc_id\": \"" + keyWords + "_" + ruleType + "_" + sameId + "\",";
		String methodName = "\"method_name\": \"" + keyWords + "请求" + ruleType + "校验\",";
		String doc = "\"method_name\": \"" + keyWords + "请求" + ruleType + "校验\",";
		String result = "\"result\": \"" + isSuccess + "\",";
		String detail = "\"detail\":\"";
		if( isSuccess ){
			detail += "校验成功\"";
		}else{
			detail += "校验失败\"";
		}
		
		str += tcId + methodName + doc + result + detail + "}";
		return str;
	}
	
	public SingleResultDO<List<ConnectionDO>> checkHasRequest( List<ConnectionDO> mainContResult, List<ConnectionDO> checkContResult, String keyWords ){
		SingleResultDO<List<ConnectionDO>> result = new SingleResultDO<List<ConnectionDO>>();
		result.setSuccess(true);
		
		List<ConnectionDO> needDOMAC = new ArrayList<ConnectionDO>();
		ConnectionDO main = null;
		ConnectionDO check = null;
		for (int index = 0; index < mainContResult.size(); index++) {
			if (mainContResult.get(index).getUrl().contains(keyWords)) {
				main = mainContResult.get(index);
				break;
			}
		}

		for (int index = 0; index < checkContResult.size(); index++) {
			if (checkContResult.get(index).getUrl().contains(keyWords)) {
				check = checkContResult.get(index);
				break;
			}
		}
		if (main != null && check != null) {
			needDOMAC.add(main);
			needDOMAC.add(check);
			result.setModel(needDOMAC);
		} else {
			if (main == null && check == null){
				result.setSuccess(true);
				result.getErrMsg()
				.putError(
						ErrorCodeConstants.CHECK_AND_MAIN_REQUEST_NOT_CONTAINT_KEYWORDS_ERROR,
						keyWords);
			}else{
				result.setSuccess(false);
				if (main == null)
					result.getErrMsg()
							.putError(
									ErrorCodeConstants.MAIN_REQUEST_NOT_CONTAINT_KEYWORDS_ERROR,
									keyWords);
				if (check == null)
					result.getErrMsg()
							.putError(
									ErrorCodeConstants.CHECK_REQUEST_NOT_CONTAINT_KEYWORDS_ERROR,
									keyWords);
				}
			}
		return result;
	}
	
	public SingleResultDO<List<ConnectionDO>> checkRequest( List<ConnectionDO> mainContResult, List<ConnectionDO> checkContResult, String keyWords, String[] eptArr ){
		SingleResultDO<List<ConnectionDO>> result = new SingleResultDO<List<ConnectionDO>>();
		SingleResultDO<List<ConnectionDO>>hasResult = checkHasRequest( mainContResult, checkContResult, keyWords );
		
		if( hasResult.isSuccess() ){
			List<ConnectionDO> sameReq = hasResult.getModel();
			if( sameReq == null || sameReq.isEmpty() ){
				result.setErrMsg( hasResult.getErrMsg() );
				result.setSuccess(true);
			}else{
				ConnectionDO mainCont = sameReq.get(0);
				ConnectionDO checkCont = sameReq.get(1);
				
				// check requset header
//				Map<String, List<String>>mainHeader = mainCont.getReqDO().getHeader();
//				Map<String, List<String>>checkHeader = checkCont.getReqDO().getHeader();
				
				// check Param
				String mainUrl = mainCont.getUrl();
				String checkUrl = checkCont.getUrl();
				Map< String, String > mainP;
				Map< String, String > checkP;
				if( eptArr != null ){
					mainP = getParamFromUrlWihtEpt( mainUrl, eptArr );
					checkP = getParamFromUrlWihtEpt( checkUrl, eptArr );
				}else{
					mainP = getParamFromUrl(mainUrl);
					checkP = getParamFromUrl(checkUrl);
				}
				
				SingleResultDO<List<String>> mapResult = checkMap( mainP, checkP );
				if( mapResult.isSuccess() ){
					result.setSuccess(true);
					result.setModel(mainContResult);
				}else{
					result.setSuccess(false);
					result.setErrMsg( mapResult.getErrMsg() );
				}
			}
			
		}else{
			result.setSuccess(false);
			result.setErrMsg( hasResult.getErrMsg() );
		}
		
		return result;
	}
	
	public SingleResultDO<List<ConnectionDO>> checkRequest( List<ConnectionDO> mainContResult, List<ConnectionDO> checkContResult, String keyWords ){
		return checkRequest( mainContResult, checkContResult, keyWords, null );
	}
	
	public SingleResultDO<List<String>> checkMap( Map<String,String> main, Map<String,String> check ){
		SingleResultDO<List<String>> result = new SingleResultDO<List<String>>();
		List<String> keyList = new ArrayList<String>();
		result.setSuccess(true);
		Iterator<String> mainIt = main.keySet().iterator();
		
		while( mainIt.hasNext() ){
			String mainKey = (String) mainIt.next();
			
			if( check.containsKey(mainKey) ){
				if( main.get(mainKey).equalsIgnoreCase( check.get(mainKey) ) ){
					result.setSuccess(true);
					keyList.add( mainKey );
				}else{
					result.setSuccess(false);
					result.getErrMsg().put( ErrorCodeConstants.CHECK_PARAM_VALUE_NOE_EQUAL, mainKey );
					break;
				}
			}else{
				result.setSuccess(false);
				result.getErrMsg().put( ErrorCodeConstants.CHECK_NOT_CONTAIN_PARAM, mainKey );
				break;
			}
		}
		return result;
	}
	
	public Map<String,String> getParamFromUrl( String url ){
		Map<String, String> params = new HashMap<String, String>();
		String[] urlAarray = url.split("[?]");
		if( urlAarray.length == 2 ){
			String[] pArray = urlAarray[1].split("&");
			if( pArray.length>0 ){
				for( int index = 0; pArray.length > index; index ++ ){
					String[] temp = pArray[index].split("=");
					if( temp.length == 2 ){
						params.put(temp[0], temp[1]);
					}
					if( temp.length == 1 ){
						params.put(temp[0], "");
					}
				}
			}
		}
		return params;
	}
	
	public Map<String,String> getParamFromUrlWihtEpt( String url, String[] eptArr ){
		Map<String, String> params = new HashMap<String, String>();
		String[] urlAarray = url.split("[?]");
		if( urlAarray.length == 2 ){
			String[] pArray = urlAarray[1].split("&");
			if( pArray.length>0 ){
				for( int index = 0; pArray.length > index; index ++ ){
					String[] temp = pArray[index].split("=");
					if( temp.length == 2 ){
						if( isNotExpectStr( temp[0], eptArr ) )
							params.put(temp[0], temp[1]);
					}
					if( temp.length == 1 ){
						if( isNotExpectStr( temp[0], eptArr ) )
							params.put(temp[0], "");
					}
				}
			}
		}
		return params;
	}
	
	public boolean isNotExpectStr( String str, String[] eptArr ){
		boolean isExpect = true;
		for( int i = 0; i < eptArr.length; i++ ){
			if( str.equalsIgnoreCase( eptArr[i] ) ){
				isExpect = false;
				break;
			}
		}
		return isExpect;
	}
	
	
	public SingleResultDO<List<ConnectionDO>> checkResponse( String mainComtId, String checkComtId, List<ConnectionDO> mainContResult, List<ConnectionDO> checkContResult, String keyWords ){
		SingleResultDO<List<ConnectionDO>> result = new SingleResultDO<List<ConnectionDO>>();
		SingleResultDO<List<ConnectionDO>>hasResult = checkHasRequest( mainContResult, checkContResult, keyWords );
		
		if( hasResult.isSuccess() ){
			List<ConnectionDO> sameReq = hasResult.getModel();
			if( sameReq == null || sameReq.isEmpty() ){
				result.setErrMsg( hasResult.getErrMsg() );
				result.setSuccess(true);
			}else{
				ConnectionDO mainCont = sameReq.get(0);
				ConnectionDO checkCont = sameReq.get(1);
				
				// check response header
//				Map<String, List<String>>mainHeader = mainCont.getResDO().getHeader();
//				Map<String, List<String>>checkHeader = checkCont.getResDO().getHeader();
				
				// check response content
				String mainContent = removeDaimon( removeStyle( new String( mainCont.getResDO().getContent() ) ).replaceAll(  "" + mainComtId, "" ) );
				String checkContent = removeDaimon( removeStyle( new String( checkCont.getResDO().getContent() ) ).replaceAll( "" + checkComtId, "") );
				
				
				
				boolean contEqual = Arrays.equals( mainContent.getBytes(), checkContent.getBytes() );
				if( contEqual ){
					result.setSuccess(true);
					result.setModel(mainContResult);
				}else{
					result.setSuccess(false);
					result.getErrMsg().put( ErrorCodeConstants.CHECK_RESPONSE_CONTENT_NOT_SAME,  keyWords );
				}
			}
		}else{
			result.setSuccess(false);
			result.setErrMsg( hasResult.getErrMsg() );
		}
		
		return result;
	}
	
	public String removeDaimon( String str ){
		str = str.replaceAll( "itempre.taobao.com", "item.taobao.com");
		str = str.replaceAll( "itembeta1.taobao.com", "item.taobao.com");
		str = str.replaceAll( "itembeta2.taobao.com", "item.taobao.com");
		str = str.replaceAll( "itempre.beta.taobao.com", "item.taobao.com");
		str = str.replaceAll( "item.pre.taobao.com", "item.taobao.com");
		
		str = str.replaceAll("limitTime:[^,]*,", "");
		str = str.replaceAll(",\"tkn\":[^,]*}", "}}");
		str = str.replaceAll( "jsonp[^,]*\\(", "(");
		return str;
	}
	
	@RequestMapping( value = "/compareing.do", method = RequestMethod.GET)
	public String startCompare( @RequestParam("versionId") String versionId, @RequestParam("num") String numStr,ModelMap mod ){
		int arrNum = Integer.parseInt( numStr );
		//更改host
		String dirStr = System.getProperty("user.dir") + "/data/"+ versionId;
		String mainHostStr = getHostStr( dirStr + "/host_version_main_" + versionId + ".xml" );
		if( mainHostStr != null && !mainHostStr.isEmpty() ){
			if( !updateHostFile( mainHostStr ) ){
				mod.addAttribute( "isSuccess", "修改host文件失败");
				return "compareing";
			}
		}
		List<String> mainItems = null;
		for( int index = 0; index < arrNum; index++ ){
			String mainItemsDir = dirStr + "/csv_result_main_" + versionId + "_" + index + ".xls";
			try {
				BufferedReader reader = new BufferedReader(new FileReader( mainItemsDir ));
				mainItems = getItemUrlString( reader );
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 设置 chrome 的路径
			System.setProperty( "webdriver.chrome.driver", "D:\\Selenium WebDriver\\chromedriver_win32\\chromedriver.exe");
			// 创建一个 ChromeDriver 的接口，用于连接 Chrome
			// 创建一个 Chrome 的浏览器实例
			WebDriver mainDriver = new ChromeDriver();

			for( int i = 0; i < mainItems.size(); i ++ ){
				mainDriver.get( mainItems.get(i) );
				try {
					Thread.sleep( 2000 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			mainDriver.quit();
			mainItems.clear();
			
			// 待校验的宝贝
			String checkHostStr = getHostStr( dirStr + "/host_version_check_" + versionId + ".xml" );
			if( checkHostStr != null && !checkHostStr.isEmpty() ){
				if( !updateHostFile( checkHostStr ) ){
					mod.addAttribute( "isSuccess", "修改host文件失败");
					return "compareing";
				}
			}
			List<String> checkItems = null;
			String checkItemsDir = dirStr + "/csv_result_check_" + versionId + "_" + index + ".xls";
			try {
				BufferedReader reader = new BufferedReader(new FileReader( checkItemsDir ));
				checkItems = getItemUrlString( reader );
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 启动浏览器并打开宝贝
			System.setProperty( "webdriver.chrome.driver", "D:\\Selenium WebDriver\\chromedriver_win32\\chromedriver.exe");
			WebDriver checkDriver = new ChromeDriver();

			for( int i = 0; i < checkItems.size(); i ++ ){
				checkDriver.get( checkItems.get(i) );
				try {
					Thread.sleep( 2000 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			checkDriver.quit();
			checkItems.clear();
		}
		mod.addAttribute( "isSuccess", "运行成功" );
		return "compareing";
	}
	
	private  List<String> getItemUrlString( BufferedReader reader) throws IOException {
		String line = reader.readLine();
		List<String> items = new ArrayList<String>();
		while (ReadLineIsNotEmpty(line)) {
			String row[] = line.split(",");// CSV格式文件为逗号分隔符文件，这里根据逗号切分
			items.add( row[1].replaceAll("\"", "") );
			line = reader.readLine();
		}
		return items;
	}
	
	private boolean updateHostFile( String hostStr ){
		try { 
			
			String hostSpt = ";";
			String ipSpt = " ";
			
			List<String> ips = new ArrayList<String>();
			List<String> domains = new ArrayList<String>();
			
	        String[] ipAndDomains = hostStr.split( hostSpt );
	            
	        if( ipAndDomains != null && ipAndDomains.length > 0 ){
	          	for( int index = 0; index < ipAndDomains.length; index ++ ){
	          		String[] temp = ipAndDomains[index].split( ipSpt );
	           		if( temp != null && temp.length > 1 ){
	          			ips.add( temp[0] );
	          			String domain = "";
	          			for( int i = 1; i < temp.length; i ++ ){
	          				domain += temp[i] + " ";
	          			}
	          			domains.add( domain.trim() );
	           		}else{
	           			System.out.println( "ERROR: get ip and domain error. " + ipAndDomains[index] );
	           			return false;
	           		}
	           	}
	           }else{
	           	System.out.println( "ERROR: get ip and domain error. " + hostStr );
	           	return false;
	        }
	        
	        updateHosts( ips, domains );
			// 让hosts切换实时生效
			Security.setProperty("networkaddress.cache.ttl", "0");
			Security.setProperty("networkaddress.cache. negative .ttl", "0");
			Runtime.getRuntime().exec("ipconfig  /flushdns");
			
			return true;
		} catch (IOException e) { 
		     System.out.println("ERROR: call runtime to flushdns exception: " + e.getMessage());     
		     return false;
		}
	}
	
	/**
	 * 更改host
	 * 1、如果有相同的绑定，不更改host
	 * 2、如果有部分绑定相同，注释掉原来host绑定，添加新的绑定
	 * 3、如果没有该绑定，添加新绑定
	 * @param ips
	 * @param domains
	 * @return
	 */
	private SameHost updateHosts( List<String>ips, List<String>domains ){
		if( ips.size() != domains.size() ){
			return SameHost.error;
		}
		
		/** 
         * 获取host文件 
         */ 
        String fileName = getHostFile(); 
        List<?> hostFileDataLines = null; 
        try { 
            hostFileDataLines = FileUtils.readLines(new File(fileName)); 
        } catch (IOException e) { 
            System.out.println("Reading host file occurs error: " + e.getMessage()); 
            return SameHost.error;
        }
        
        List<String> newLinesList = new ArrayList<String>();
        String split = " ";
        /**
         * 指定domain是否存在，如果存在，则不追加
         * 标识本次文件是否有更新，比如：如果指定的IP和域名已经在host文件中存在，则不用再写文件 
         * 默认所有绑定需要在host文件最后添加
         */
        List<Boolean> needAdd = new ArrayList<Boolean>();
        for( int index = 0; index < ips.size(); index ++ ){
        	needAdd.add( true );
        }
        
        for( Object line : hostFileDataLines ){
        	String strLine = line.toString();
        	String temp = removeStyle( strLine );
        	if( !strLine.startsWith( "#" ) && temp != null && !temp.isEmpty()  ){
        		String[] host = strLine.split( split );
        		if( host.length < 2 ){
        			System.out.println("host file is error. start:" + strLine + "end");
        			return SameHost.error;
        		}
        		
        		// 原本的域名转换成list
    			List<String> orgDomains = new ArrayList<String>(); 
    			for( int j = 1; j < host.length; j ++ ){
					orgDomains.add( host[j].trim() );
				}
        		SameHost lineSame = null;
        		// 需要更新的绑定循环
        		for( int i = 0; i < ips.size(); i ++ ){
        			// 新绑定的域名string转成list
        			List<String> updateDomains = new ArrayList<String>();
        			String[] domainStrs = domains.get(i).split(split);
        			if( domainStrs.length <= 0 ){
        				return SameHost.error;
        			}
        			for( int j = 0; j < domainStrs.length; j ++ ){
        				updateDomains.add( domainStrs[j].trim() );
        			}
        			
        			// IP相同
        			if( host[0].trim().equalsIgnoreCase( ips.get( i ).trim() ) ){
        				lineSame = judgeSameDomain( orgDomains, updateDomains );
        				// IP和域名完全相同，则该新增绑定不需要添加
        				if( lineSame == SameHost.same ){
    						needAdd.set( i, false );
    						break;
        				}
        				// 部分域名相同
        				if( lineSame == SameHost.part ){
    						break;
        				}
        				
        			}else{// IP不同
        				lineSame = judgeSameDomain( orgDomains, updateDomains );
        				// 域名相同，IP不同，需要注释掉
        				if( lineSame == SameHost.same ){
        					lineSame = SameHost.part;
        					break;
        				}
        				// 部分域名相同
        				if( lineSame == SameHost.part ){
    						break;
        				}
        			}
        		}
        		// 域名且IP不同；IP相同域名不同，不需要注释，回写到host
				if( lineSame.equals( SameHost.different ) )
					newLinesList.add( strLine );
				// 域名部分相同，需要注释
				if( lineSame.equals( SameHost.part ) )
					newLinesList.add( "#" + strLine );
				// 相同的绑定，不需要再额外添加
				if( lineSame.equals( SameHost.same ) )
					newLinesList.add( strLine );
        	}else{
        		newLinesList.add( strLine );
        	}
        }
        
        int temp = 0;
        for( int index = 0; index < needAdd.size(); index ++ ){
        	if( needAdd.get( index ) ){
        		if( temp == 0 )
        			newLinesList.add( "#luanjia新增绑定" );
        		newLinesList.add( ips.get(index) + split + domains.get(index) );
        		temp ++;
        	}
        }
        /** 
         * 写设定文件
         */
		try {
			FileUtils.writeLines( new File(fileName), newLinesList );
			if( temp == 0 )
				return SameHost.different;
			if( temp < ips.size() )
				return SameHost.part;
			if( temp == ips.size() )
				return SameHost.same;
			return SameHost.error;
		} catch (IOException e) {
			System.out.println("Updating host file occurs error: " + e.getMessage());
			return SameHost.error;
		}
	}
	
	/**
	 * 去掉 removeString 字符串中的空格、回车
	 * @param removeString
	 * @return
	 * @author luanjia
	 */
	protected static String removeStyle( String removeString ) {
		String subString = "";
		if( removeString != "" && removeString != null ){
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			java.util.regex.Matcher actrualJsonStrMa = p.matcher( removeString );
			subString = actrualJsonStrMa.replaceAll( "" );	
		}
		return subString;
	}
	/**
	 * 判读域是否相同
	 * @param originalDomains
	 * @param newDomains
	 * @return
	 */
	private SameHost judgeSameDomain( List<String> originalDomains, List<String> newDomains ){
		boolean codeSame = false;
 		int domainSameNum = 0;
		if( originalDomains.size() == newDomains.size() ){
			for( int i = 0; i < originalDomains.size(); i++ ){
				codeSame = false;
				for( int j = 0; j < newDomains.size(); j++ ){
					if( originalDomains.get( i ).equalsIgnoreCase( newDomains.get( j ) ) ){
						codeSame = true;
						break;
					}
				}
				if( codeSame == true ){
					domainSameNum ++;
				}
			}
		}else{
			for( int i = 0; i < originalDomains.size(); i++ ){
				codeSame = false;
				for( int j = 0; j < newDomains.size(); j++ ){
					if( originalDomains.get( i ).equalsIgnoreCase( newDomains.get( j ) ) ){
						codeSame = true;
						break;
					}
				}
				if( codeSame == true ){
					domainSameNum ++;
				}
			}
		}
		
		return isSameDomain( domainSameNum, newDomains.size() );
	}
	
	private SameHost isSameDomain( int sameNum, int domainSize ){
		if( sameNum == 0 )
			return SameHost.different;
		if( sameNum == domainSize )
			return SameHost.same;
		if( sameNum < domainSize )
			return SameHost.part;
		return SameHost.different;
	}
	/**
	 * 相同的程度：
	 * different 表示完全不同
	 * same 表示完全相同
	 * part 表示部分相同
	 * @author luanjia
	 *
	 */
	public enum SameHost {
		different, same, part, error;
	}
	
	/** 
     * 获取host文件路径 
     * @return 
     */ 
	private String getHostFile() { 
        String fileName = null; 
        // 判断系统 
        if ("linux".equalsIgnoreCase(System.getProperty("os.name"))) { 
            fileName = "/etc/hosts"; 
        } else { 
            fileName = System.getenv("windir") + "\\system32\\drivers\\etc\\hosts"; 
        } 
        return fileName; 
    } 
	
	private String getHostStr( String hostDirStr ){
		String host = null;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader( hostDirStr ));
			host = getNeedDaString( reader );
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return host;
	}
	
	public String getNeedDaString( BufferedReader reader) throws IOException {
		String line = reader.readLine();
		String host = null;
		while ( ReadLineIsNotEmpty(line) ) {
			host += line;
			line = reader.readLine();
		}
		return host;
	}
	
	private boolean ReadLineIsNotEmpty( String readLine ){
		if( null != readLine && !readLine.isEmpty() ){
			return true;
		}else {
			return false;
		}
	}
	
	@RequestMapping( value = "/createfile.do" )
	public String createExcelView(@RequestParam("versionId") String versionId, ModelMap mod){
		
		List<String> mainUrlList = new ArrayList<String>();
		List<String> checkUrlList = new ArrayList<String>();
		SingleResultDO<List<ToBeCheckDO>> toBeCheckResult = toBeCheckService.getAllToBeCheckDOByVersionId( Long.parseLong( versionId ) );
		if( toBeCheckResult.isSuccess() && toBeCheckResult.getModel() != null && toBeCheckResult.getModel().size() != 0){
			List<ToBeCheckDO> toBeCheckDOs = toBeCheckResult.getModel();
			for( ToBeCheckDO tempDO : toBeCheckDOs ){
				String mainUrl = analysisEvnToUrl( tempDO.getMainEnvrmt() + "" );
				if( mainUrl != null && mainUrl != "" && tempDO.getSameReq() != null && tempDO.getSameReq() != ""){
					mainUrl += tempDO.getSameReq();
					mainUrlList.add( mainUrl );
				}
				
				String checkUrl = analysisEvnToUrl( tempDO.getCheckEnvrmt() + "" );
				if( checkUrl != null && checkUrl != "" && tempDO.getSameReq() != null && tempDO.getSameReq() != ""){
					checkUrl += tempDO.getSameReq();
					checkUrlList.add( checkUrl );
				}
			}
		}
		
		String mainFileName = "result_main_" + versionId + ".xls";
		String checkFileName = "result_check_" + versionId + ".xls";
		writeToExcel( mainUrlList, mainFileName );
		writeToExcel( checkUrlList, checkFileName );
		
		writeToCSV( mainUrlList, "csv_" + mainFileName );
		writeToCSV( checkUrlList,"csv_" + checkFileName );
		return "createfile";
	}
	
	private boolean writeToCSV( List<String>urlList, String fileName ){
		boolean isSucess = true;
		String xml = "";
		for( int index = 0; index < urlList.size(); index ++ ){
			xml += "\"" + index + "\"," + "\"" + urlList.get(index) + "\"\r\n";
		}
		try{
			FileOutputStream fos = new FileOutputStream(new File( System.getProperty("user.dir") + "/data/" + fileName ));
			Writer os = new OutputStreamWriter( fos, "UTF-8" );
			os.write( xml );
			os.flush();
			fos.close();
		} catch (IOException e) {
			isSucess =  false;
			System.out.println("产生错误，错误讯息：" + e.toString());
		}
		return isSucess;
	}
	
	private boolean writeToCSVNew( List<String>urlList, String fileName, long versionId ){
		boolean isSucess = true;
		String xml = "";
		for( int index = 0; index < urlList.size(); index ++ ){
			xml += "\"" + index + "\"," + "\"" + urlList.get(index) + "\"\r\n";
		}
		try{
			FileOutputStream fos = new FileOutputStream(new File( System.getProperty("user.dir") + "/data/" + versionId + "/" + fileName ));
			Writer os = new OutputStreamWriter( fos, "UTF-8" );
			os.write( xml );
			os.flush();
			fos.close();
		} catch (IOException e) {
			isSucess =  false;
			System.out.println("产生错误，错误讯息：" + e.toString());
		}
		return isSucess;
	}
	
	private boolean writeToExcel( List<String> urlList, String fileName ){
		boolean isSucess = true;
		try {
			FileOutputStream fos = new FileOutputStream( System.getProperty("user.dir") + "/data/" + fileName);
			HSSFWorkbook wb = new HSSFWorkbook();// 创建工作薄
			HSSFSheet sheet = wb.createSheet();// 创建工作表
			wb.setSheetName(0, "process");// 设置工作表名
			HSSFRow row = null;
			// 宣告一列
			HSSFCell cell = null;
			// 宣告一个储存格
			row = sheet.createRow(0);
			// 建立一个新的列，注意是第五列(列及储存格都是从0起算)
			cell = row.createCell(0);
			// 设定这个储存格的字串要储存双位元
			cell.setCellValue("用例");
			cell = row.createCell(1);
			cell.setCellValue("测试用例名称");
			cell = row.createCell(2);
			cell.setCellValue("是否执行");
			cell = row.createCell(3);
			cell.setCellValue("执行次数");
			cell = row.createCell(4);
			cell.setCellValue("备注");
			cell = row.createCell(5);
			cell.setCellValue("itemurl");
			for( int index = 1; index < urlList.size() + 1; index ++ ){
				row = sheet.createRow( index );
				cell = row.createCell( 0 );
				cell.setCellValue( index );
				cell = row.createCell( 1 );
				cell.setCellValue( "httpfilter" );
				cell = row.createCell( 2 );
				cell.setCellValue( "Y" );
				cell = row.createCell( 3 );
				cell.setCellValue( 1 );
				cell = row.createCell( 5 );
				cell.setCellValue( urlList.get(index -1) );
			}
			wb.createSheet();// 创建工作表
			wb.setSheetName(1, "initsql");// 设置工作表名
			wb.createSheet();// 创建工作表
			wb.setSheetName(2, "RebackSql");// 设置工作表名
			wb.createSheet();// 创建工作表
			wb.setSheetName(3, "DeleteSql");// 设置工作表名
			
			wb.write(fos);
			fos.close();
		} catch (IOException e) {
			isSucess =  false;
			System.out.println("产生错误，错误讯息：" + e.toString());
		}
		return isSucess;
	}
	
	public void appendMethodB(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public long createCOMTID() {
		UUID uuid = UUID.randomUUID();
		long comtId = -1;
		SingleResultDO<List<CommunicationDO>> comtDOs = comtService.fetchComtByTraceId(uuid.toString());
		if( comtDOs.isSuccess() && CollectionUtils.isNotEmpty( comtDOs.getModel() ) ){
			comtId = comtDOs.getModel().get(0).getId();
		}
		return comtId;
	}
	@RequestMapping(value = "/runrule.do")
	public String runRuleView(ModelMap mod ){
		List<RuleDO> rules = new ArrayList<RuleDO>();
		//获取所有的规则
		SingleResultDO<List<RuleDO>> result = ruleService.searchAllRules();
		if( result.isSuccess() ){
			rules = result.getModel();
		}
		List<ItemDO> items = specialItemManager.listForSpecialsWithStyle(15);
		mod.addAttribute("rules", rules);
		mod.addAttribute("title", ruleTitle);
		mod.addAttribute("itemTitle", itemTitle);
		mod.addAttribute("items", items);
		
		return "runrule";
	}
	
	public String analysisEvn( String env ){
		//判断主环境
		if( env.equalsIgnoreCase("0") ){
			return "线上";
		}
		if( env.equalsIgnoreCase("1") ){
			return "beta";
		}
		if( env.equalsIgnoreCase("2") ){
			return "预发";
		}
		if( env.equalsIgnoreCase("3") ){
			return "灰度预发";
		}
		if( env.equalsIgnoreCase("4") ){
			return "灰度beta";
		}
		return null;
	}
	
	public String analysisEvnToHost( String env, String host ){
		//判断主环境
		if( env.equalsIgnoreCase("0") ){
			return "";
		}
		if( env.equalsIgnoreCase("1") ){
			return detailskipBeta;
		}
		if( env.equalsIgnoreCase("2") ){
			return detailskipBeta;
		}
		if( env.equalsIgnoreCase("3") ){
			return detailskipPre;
		}
		if( env.equalsIgnoreCase("4") ){
			return detailskipPre;
		}
		if( env.equalsIgnoreCase("5") ){
			return host;
		}
		return null;
	}
	
	public String analysisEvnToUrl( String env ){
		//判断主环境
		if( env.equalsIgnoreCase("0") ){
			return "http://item.taobao.com/item.htm?id=";
		}
		if( env.equalsIgnoreCase("1") ){
			return "http://itembeta1.taobao.com/item.htm?id=";
		}
		if( env.equalsIgnoreCase("2") ){
			return "http://itempre.taobao.com/item.htm?id=";
		}
		if( env.equalsIgnoreCase("3") ){
			return "http://itempre.beta.taobao.com/item.htm?id=";
		}
		if( env.equalsIgnoreCase("4") ){
			return "http://item.beta.taobao.com/item.htm?id=";
		}
		if( env.equalsIgnoreCase("5") ){
			return "http://item.taobao.com/item.htm?id=";
		}
		return null;
	}
	
	public boolean isNotEmpty( String str ){
		if( str != null && !str.isEmpty() )
			return true;
		return false;
	}
}
