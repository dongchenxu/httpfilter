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
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Create Rule</title>
</head>
<body style="background: #FFCC80">
	<center>删除${isSuccess}</center>
	<table align="center" border="1" bordercolor="#e5e5e5" cellspacing="0"
		height="60%" width="80%">
		<!-- 表头 -->
		<tr bgcolor="#999999">
			<c:forEach items="${title}" var="ti">
				<th>${ti}</th>
			</c:forEach>
		</tr>
		<!-- 数据 -->
		<c:forEach items="${rules }" var="rs" varStatus="status">
			<tr class="item">
				<td>${rs.id}</td>
				<td>${rs.keyWords}</td>
				<td id="${rs.checkType}"><a name="${rs.checkType}">${rs.checkType}</a>
				</td>
				<td>${rs.exceptFields}</td>
			</tr>
		</c:forEach>
	</table>
	</br>
	<table border="0" align="center">
		<tbody>
			<tr>
				<td align="center">
				<span id="time" style="background: #00BFFF">60</span>秒钟后自动跳转，如果不跳转，请点击下面的链接
				<a href="http://localhost:8080/httpfilter/rulelist.do">规则管理页面</a>
				</td>
			</tr>
		</tbody>
	</table>
</body>
<script>
	for ( var i = 0; i < document.anchors.length; i++) {
		if (document.anchors[i].innerHTML == "1") {
			document.anchors[i].innerHTML = "请求是否发出";
		}
		if (document.anchors[i].innerHTML == "2") {
			document.anchors[i].innerHTML = "request校验";
		}
		if (document.anchors[i].innerHTML == "4") {
			document.anchors[i].innerHTML = "response校验";
		}
	}
</script>

<script language="JavaScript" type="text/javascript">
			function delayURL(url) {
				var delay = document.getElementById("time").innerHTML;
				if(delay > 0) {
					delay--;
					document.getElementById("time").innerHTML = delay;
				} else {
					window.top.location.href = url;
				}
				setTimeout("delayURL('" + url + "')", 1000);
			}
</script>

<script type="text/javascript">
	delayURL("http://localhost:8080/httpfilter/rulelist.do");
</script>

</html>
