<%@ page contentType="text/html; charset=UTF-8" language="java"
	import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.googlecode.httpfilter.domain.RuleDO"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:include page="/inc.jsp" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>规则管理</title>
</head>
<body style="background: #FFCC80">
	<form action="./delrule.do" method="post" style="font-size: 10px">
		<div align="center">
			<center style="font-size: 20px">已有的运行规则</center>
			</br>
			<table align="center" border="1" bordercolor="#e5e5e5"
				cellspacing="0" height="60%" width="80%">
				<!-- 表头 -->
				<tr bgcolor="#999999">
					<c:forEach items="${title}" var="ti">
						<th>${ti}</th>
					</c:forEach>
				</tr>
				<!-- 数据 -->
				<c:forEach items="${rules }" var="rs" varStatus="status">
					<tr class="item">
						<td><input name="chooseRule" type="checkbox" value="${rs.id}">${rs.id}
						</td>
						<td>${rs.keyWords}</td>
						<td id="${rs.checkType}"><a name="${rs.checkType}">${rs.checkType}</a>
						</td>
						<td>${rs.exceptFields}</td>
					</tr>
				</c:forEach>
			</table>

			<table border="0">
				<tbody>
					<tr>
						<td>
							<a href="./createrule.do" target="_blank">
								<input name="addRule" type="button" value="添加规则" style="font-size: 20px"/>
        					</a>
						</td>
						<td align="center">
							<input name="delRule" type="submit" value="删除规则" style="font-size: 20px"/>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</form>
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
</body>
</html>
