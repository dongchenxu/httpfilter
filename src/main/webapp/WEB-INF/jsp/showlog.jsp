<%@ page contentType="text/html; charset=UTF-8" language="java"
	import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java"
	import="com.googlecode.httpfilter.domain.LogHtmlDO"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:include page="/inc.jsp" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>运行版本:${versionId}的日志</title>

<style type="text/css">
[title=false] {
	color:red;
}
[title=true] {
	color:green;
}
</style>
</head>
<body style="background: #FFCC80">
	<center>版本号${versionId}的运行结果</center>
	<hr color="#999999">
	<table align="center" border="1" bordercolor="#e5e5e5" cellspacing="0"
		height="80%" width="80%">
		<!-- 表头 -->
		<tr bgcolor="#999999" height="30">
			<c:forEach items="${logTitle}" var="ti">
				<th>${ti}</th>
			</c:forEach>
		</tr>
		<!-- 数据 -->
		<c:forEach items="${logs }" var="rs" varStatus="status">
			<tr class="item" height="30">
				<td><a target="_blank" href="http://item.taobao.com/item.htm?id=${rs.pathName}">${rs.pathName}</a></td>
				<td>${rs.tcName}</td>
				<td title=${rs.result}>${rs.result}</td>
				<td title=${rs.result}>${rs.detail}</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>
