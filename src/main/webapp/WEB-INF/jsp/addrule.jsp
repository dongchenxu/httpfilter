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
	<center>添加成功</center>
	<table border="0" style="font-size: 10px">
		<tbody>
			<tr>
				<th align="right">关键词：</th>
				<td align="left">${keyWords}</td>
			</tr>

			<tr>
				<th align="right">校验类型：</th>
				<td align="left">${checkType}</td>
			</tr>

			<tr>
				<th align="right">不校验的字段：</th>
				<td align="left">${exceptFields}</td>
			</tr>
		</tbody>
	</table>
	<table border="0">
		<tbody>
			<tr>
				<td>
					<a href="./createrule.do">
						<input name="addRule" type="button" value="继续添加"/>
					</a>
				</td>
				<td>
					<a href="./rulelist.do">
						<input name="delRule" type="submit" value="规则管理"/>
                    </a>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>
