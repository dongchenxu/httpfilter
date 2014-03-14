<%@ page contentType="text/html; charset=UTF-8" language="java"
	import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.googlecode.httpfilter.domain.RuleDO"%>
<%@ page language="java"
	import="com.googlecode.httpfilter.domain.ItemDO"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:include page="/inc.jsp" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Run Rule</title>
</head>
<body style="background: #FFCC80">
	<form action="./addcompare.do" method="post" onsubmit="return runrule();" style="font-size: 15px">
		<center style="font-size:25px">选择环境</center>
		<hr>
		<div align="center">
			<table align="center" border="0" bordercolor="#e5e5e5"
				cellspacing="0" height="60%" width="80%">
				<tr style="height: 30px">
					<th align="right">主环境：</th>
					<td><input id="mainEnv" name="mainType" type="radio" value="0"
						style="font-size: 1px" checked="checked" />线上 <input name="mainType" type="radio"
						value="1" style="font-size: 1px" />beta <input name="mainType"
						type="radio" value="2" style="font-size: 1px" />预发 <input
						name="mainType" type="radio" value="3" style="font-size: 1px" />灰度预发
						<input name="mainType" type="radio" value="4"
						style="font-size: 1px" />灰度beta</td>
				</tr>
				<tr style="height: 30px">
					<th align="right" style="width: 20%">待校验环境：</th>
					<td style="width: 80%">
						<input name="checkType" type="radio" value="0" style="font-size: 1px" onclick="checkOther()"/>线上 
						<input name="checkType" type="radio" value="1" style="font-size: 1px" onclick="checkOther()"/>beta 
						<input name="checkType"	type="radio" value="2" style="font-size: 1px" onclick="checkOther()" checked="checked"/>预发 
						<input name="checkType" type="radio" value="3" style="font-size: 1px" onclick="checkOther()"/>灰度预发
						<input name="checkType" type="radio" value="4" style="font-size: 1px" onclick="checkOther()"/>灰度beta
						<input id="otherType" name="checkType" type="radio" value="5" style="font-size: 1px"  onclick="checkOther()"/>其他绑定
					</td>
				</tr>
				<tr>
					<th></th>
					<td>
						<textarea id="host" rows="3" name="host" cols="60" style="display:none;" align="right"></textarea>
					</td>
				</tr>
			</table>

			<hr color="#FFCC80">
			<center>运行的规则</center>
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
						<td><input name="chooseRule" type="checkbox" checked="checked" value="${rs.id}">${rs.id}
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
						<td align="center">
							<input name="runrule" type="submit"	value="提交运行" style="font-size: 20px"/>
							<input name="reset" type="reset" value="重新提交" style="font-size: 20px"/>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<hr color="#FFCC80">
		<center>宝贝ID</center>
		<hr>
		<div align="center">
			<table align="center" border="1" bordercolor="#e5e5e5"
				cellspacing="0" height="60%" width="80%">
				<!-- 表头 -->
				<tr bgcolor="#999999">
					<c:forEach items="${itemTitle}" var="it">
						<th>${it}</th>
					</c:forEach>
				</tr>
				<!-- 数据 -->
				<c:forEach items="${items}" var="row" varStatus="status">
					<tr style="font-size: 10px; height: 20px">
						<td style="text-align: center;">${row.id}</td>
						<td>${row.title}</td>
						<td style="text-align: center;"><a target="_blank"
							href="http://item.taobao.com/item.htm?id=${row.id}">线上</a> <a
							target="_blank"
							href="http://itempre.taobao.com/item.htm?id=${row.id}">预发</a> <a
							target="_blank"
							href="http://itembeta1.taobao.com/item.htm?id=${row.id}">BETA1</a>
							<a target="_blank"
							href="http://itembeta2.taobao.com/item.htm?id=${row.id}">BETA2</a>
							<a target="_blank"
							href="http://itempre.beta.taobao.com/item.htm?id=${row.id}">灰度预发</a>
							<a target="_blank"
							href="http://item.beta.taobao.com/item.htm?id=${row.id}">灰度线上</a>
						</td>
					</tr>
				</c:forEach>
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
	<script type="text/javascript">
		function runrule(){
		alert("进入js");
			if($('input:checkbox:checked').size() == 0){
				alert("选择规则");
			}
		}
	</script>
	<script type="text/javascript">
	function checkOther(){
		if( document.getElementById("otherType").checked==true ){
			document.getElementById( "host" ).style.display="inline";
		}else{
			document.getElementById( "host" ).style.display="none";
		}
	}
	</script>
</body>
</html>
