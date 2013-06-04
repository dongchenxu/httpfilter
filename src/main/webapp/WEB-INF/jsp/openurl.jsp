<%@ page contentType="text/html; charset=UTF-8" language="java"
	import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java"
	import="com.googlecode.httpfilter.domain.ConnectionDO"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:include page="/inc.jsp" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>打开跟踪的连接</title>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/showcon.css" charset="UTF-8"></link>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/showcon.js" charset="UTF-8"></script>
<script type="text/javascript" src="${home}/lib/jquery-1.8.0.min.js">
</script>

<script type="application/javascript">
function openUrl(){
	var urlInfo = $("#url").val();
	window.open( urlInfo + "&trace_id=${trace_id}");
};
</script>

<script type="application/javascript">
function getUrl(){
	window.open( "http://localhost:8080/httpfilter/con.do?trace_id=${trace_id}" );
};
</script>

</head>
<body>
	<div>
		<table align="center" width="100%">
		<tr>
			<th>链接</th>
			<td>
				<input id="url" type="text" value="" />
				<input name="openurl" type="button" onclick="openUrl()" value="打开" size="" />
				<input name="geturl" type="button" onclick="getUrl()" value="获取" size="" />
			</td>
		</tr>
		</table>
	</div>
	
	<div align="center" >
			<table align="center" border="1" bordercolor="#e5e5e5" cellspacing="0" height="60%" width="80%">
				<!-- 表头 -->
				<tr bgcolor="#999999">
					<c:forEach items="${title}" var="ti">
						<th>${ti}</th>
					</c:forEach>
				</tr>
				<!-- 数据 -->
				<c:forEach items="${resultList }" var="rs" varStatus="status">
					<tr class="item" ondblclick="dismissSysBar(${status.index }, '${rs.params}', '${rs.response}')" onclick="showSysBar(${status.index }, '${rs.params}', '${rs.response}')">
						<td>${status.index}</td>
						<td>${rs.req}</td>
						<td>${rs.status}</td>
						<td>${rs.domain}</td>
						<td>${rs.clientIp}</td>
					</tr>
					
				</c:forEach>
			</table>
	</div>

	<BR color="#e5e5e5" />
	<TABLE border=1 cellPadding=0 cellSpacing=0 height="100%" width="80%" align="center">
		<TBODY>
			<TR>
				<TD bgColor="#999999" onclick="switchSysBar()" style="WIDTH: 30pt; HEIGHT: 100%" align="right">收起/展开
					<SPAN class=navPoint id=switchPoint title=关闭/打开>3</SPAN>
				</TD>
			</TR>
			<TR>
				<TD id="frmTitle" noWrap name="fmTitle">
					<DIV id="Whatever" style="padding: 10px;" align="center">
						<UL class="TabBarLevel" id="TabPage">
							<LI class="Selected"><A href="javascript:void(0)"
								onfocus="this.blur();" onclick="goto('0');">参数</A></LI>
							<LI><A href="javascript:void(0)" onfocus="this.blur();"
								onclick="goto('1');">响应</A></LI>
						</UL>
						<DIV class="HackBox">
						<!-- 
							<IFRAME name="tabIframe" src="request-param.do"  id=”win” name=”win” onload=”Javascript:SetWinHeight(this)”
								marginheight="8" marginwidth="8" frameborder="0" width="100%"></IFRAME>
						-->
							<TABLE id="paramFrame" border=1 cellPadding=0 cellSpacing=0 height="100%" width="100%" align="center">
								
							</TABLE>
							<div align="right">
							        <input type="button" name="Submit" value="添加参数" onclick="AddOneParamRow(null, null)" /> 
							     	<input type="button" name="Submit2" value="保存" onclick="ClearAllSign()" />
							     	<input name='txtTRLastIndex' type='hidden' id='txtTRLastIndex' value="0" />
							</div>
							<TABLE id="responseFrame" style="display: none" border=1 cellPadding=0 cellSpacing=0 height="100%" width="100%" align="center">
								
							</TABLE>
						</DIV>
					</DIV>
				</TD>
			</TR>
		</TBODY>
	</TABLE>
</body>
</html>