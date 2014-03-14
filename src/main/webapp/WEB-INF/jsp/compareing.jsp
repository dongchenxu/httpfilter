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
	<center>校验开始</center>
	<h1> 更改host</h1>
	<h1> 启动浏览器，请不要关闭</h1>
    <h1> ${isSuccess}</h1>
    </h1>
	<table border="0">
		<tbody>
			<tr>
				<td>
					<a href="./showlog.do?versionId=${versionId}">
						<input name="addRule" type="button" value="显示日志"/>
					</a>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>