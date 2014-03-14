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
<script type="text/javascript" src="${home}/lib/jquery-1.8.0.min.js">
</script>
<script type="application/javascript">
function openUrl(){
	var urlInfo = $("#url").val();
	window.open( urlInfo + "&trace_id=${trace_id}");
}
</script>
</head>
<body>
	<table>
	<tr>
		<th>链接</th>
		<td>
			<input id="url" type="text" />
			<input name="openurl" type="button" onclick="openUrl()" value="打开" size="" />
		</td>
	</tr>
	</table>
</body>
</html>