<%@ page contentType="text/html; charset=UTF-8" language="java"
	import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java"
	import="com.googlecode.httpfilter.domain.RuleDO"%>
<%@ page language="java"
	import="com.googlecode.httpfilter.domain.ItemDO"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:include page="/inc.jsp" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>生成校验队列</title>
<script type="application/javascript">
	function showlog() {
		var urlInfo = "http://localhost:8080/httpfilter/compareing.do";
		window.open( urlInfo + "?versionId=${versionId}&arrnum=${arrNum}");
	}
</script>
</head>
<body style="background: #FFCC80">
	<center>${isSucess}</center>
	<hr>
	<div align="center">
		<table align="center" border="0" bordercolor="#e5e5e5" cellspacing="0"
			height="60%" width="80%">
			<tr>
				<th align="right">主环境：</th>
				<td>${mainEvn}</td>
			</tr>
			<tr>
				<th align="right">待校验环境：</th>
				<td>${checkEvn}</td>
			</tr>
			<tr>
				<th align="right">版本号：</th>
				<td>${versionId}</td>
			</tr>
			<tr>
				<th align="right">文件数据： </th>
				<td>${arrNum}</td>
            </tr>
		</table>
	</div>
	<hr color="#FFCC80">

	<div align="center">
		<table border="0">
			<tbody>
				<tr>
					<td align="center"><input name="StartCheck" type="button"
						onclick="showlog()" value="开始校验" size="" /></td>
				</tr>
			</tbody>
		</table>
	</div>
</body>
</html>
