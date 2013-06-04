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
    <body style="background:#FFCC80">
        <center>创建规则</center>
        <hr />
        <form action="./addrule.do" method="post" style="font-size:10px" >
        	<table border="0" >
        		<tbody>
        			<tr>
        				<th align="right">关键词：</th>
        				<td>
        					<input name="keyWords" type="text" align="left" />
        				</td>
        			</tr>
        			<tr>
        				<th align="right">校验类型：</th>
        				<td>
        					<input name="checkType" type="radio" value = "0" style="font-size: 1px"/>校验请求是否发送
        					<input name="checkType" type="radio" value = "1" style="font-size: 1px"/>校验request信息
        					<input name="checkType" type="radio" value = "2" style="font-size: 1px"/>校验response信息
        				</td>
        			</tr>
        			<tr>
        				<th>不校验的字段：</th>
        				<td>
        					<input name="exceptFields" type="text" align="left" />
        				</td>
        			</tr>
        		</tbody>
        	</table>
        	<table border="0">
        		<tbody>
        			<tr>
        				<th align="right" style="width:45%;text-align:left;"></th>
        				<td align="center">
        					<input name="addRule" type="submit"/>
        					<input name="reset" type="reset"/>
        				</td>
        			</tr>
        		</tbody>
        	</table>
        </form>
    </body>
</html>
