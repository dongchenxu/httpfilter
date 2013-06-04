<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" import="com.googlecode.httpfilter.domain.ConnectionDO" %>
<%@ page language="java" import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:include page="/inc.jsp" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${home}/css/showcon.css" charset="UTF-8"></link>

<script type="text/javascript" src="${home}/js/showcon.js" charset="UTF-8"></script>
<script type="text/javascript" src="${home}/js/jquery-1.8.1.js" charset="UTF-8"></script>

<title>展示页面</title>
</head>
<body>

	<div id="main" align="center">
			<table align="center" border="1" bordercolor="#e5e5e5" cellspacing="0" height="60%" width="95%" style="word-break:break-all; word-wrap:break-all;">
				<!-- 表头 -->
				<tr bgcolor="#999999">
					<c:forEach items="${title}" var="ti" varStatus="sts">
						<th>${ti}</th>
					</c:forEach>
				</tr>
				<%
					List<ConnectionDO> resultList = (List<ConnectionDO>) request.getAttribute("resultList");
					for(int i = 0; i < resultList.size(); i++){
						//String status = resultList.get(i).getReqDO().getHeader().get("status").toString();
						//Object params = resultList.get(i).getReqDO().getContent();
						Object res = resultList.get(i).getResDO().getContent();
						String uri = resultList.get(i).getUrl().toString();
						String[] arr = uri.split("\\?");
						String params = null;
						if(arr.length > 1){
							params = arr[arr.length-1];
						}
						int len = uri.length();
						int index = uri.lastIndexOf("?");
						String uripre = uri;
						if(len > 0 && index >1 && index <= len){
							uripre = uri.substring(0, index-1);
						} 
						
						String domain = resultList.get(i).getServerIP();
						//String clientIP = resultList.get(i).getReqDO().getHeader().get("x-forwarded-for").toString();
				%>
					<tr class="item" ondblclick="dismissSysBar(<%=i %>, '<%=params %>', '<%=res %>', '<%=domain %>', '<%=uripre %>')" onclick="showSysBar(<%=i %>, '<%=params %>', '<%=res %>', '<%=domain %>', '<%=uripre %>')">
						<td width="3%"><%=i %></td>
						<td align="left" width="70%"><%=uri %></td>
						<td width="3%">200</td>
						<td width="15%"><%=domain %></td>
						<td width="9%">10.235.1.11</td>
					</tr>
				<%} %>
			</table>
			
	</div>

	<BR style="color: #e5e5e5;" />
	<div id="params_res_view">
		<TABLE border=1 cellPadding=0 cellSpacing=0 height="100%" width="95%" align="center">
			<TBODY>
				<TR id="frmStatus">
					<TD bgColor="#999999" onclick="switchSysBar()" style="WIDTH: 30pt; HEIGHT: 100%" align="right">收起/展开
						<SPAN class=navPoint id=switchPoint title=关闭/打开>3</SPAN>
					</TD>
				</TR>
				<TR>
					<TD id="frmTitle" noWrap name="fmTitle" style="display:none;">
						<DIV id="Whatever" style="padding: 10px;" align="center">
							<UL class="TabBarLevel" id="TabPage">
								<LI class="Selected"><A href="javascript:void(0)" onfocus="this.blur();" onclick="goto('0');">参数</A></LI>
								<LI><A href="javascript:void(0)" onfocus="this.blur();" onclick="goto('1');">响应</A></LI>
								<LI><A href="javascript:void(0)" onfocus="this.blur();" onclick="goto('2');">头信息</A></LI>
							</UL>
							<DIV class="HackBox">
								<TABLE id="paramFrame" border=1 cellPadding=0 cellSpacing=0 height="100%" width="100%" align="center">
									
								</TABLE>
								
								<div align="right">
								        <input type="button" name="Submit" value="添加参数" onclick="AddOneParamRow(null, null)" /> 
								     	<input type="button" name="Submit2" value="保存" onclick="saveParams()" />
								     	<input type="button" name="Submit3" value="json" onclick="make_json_response(‘content’,'0','hello','')" />
								     	<input name='txtTRLastIndex' type='hidden' id='txtTRLastIndex' value="0" />
								</div>
								
								<TABLE id="responseFrame" style="display: none" border=1 cellPadding=0 cellSpacing=0 height="100%" width="100%" align="center">
									
								</TABLE>
								
								<TABLE id="headerFrame" style="display: none" border=1 cellPadding=0 cellSpacing=0 height="100%" width="100%" align="center">
									
								</TABLE>
							</DIV>
						</DIV>
					</TD>
				</TR>
			</TBODY>
		</TABLE>
	</div>
	
</body>
</html>