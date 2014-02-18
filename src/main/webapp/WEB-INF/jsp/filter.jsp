<%@ page contentType="text/html; charset=GBK"%>
<!--  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>-->
<!--<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>-->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="zh-CN" xml:lang="zh-CN">

<style type="text/css">
*{margin:0;padding:0;}:focus,:active {outline:0}ul,ol{list-style:none}h1,h2,h3,h4,h5,h6,pre,code {font-size:1em;}a img{border:0} 
body { font: .9em Georgia, "Times New Roman", Arial, Sans-Serif; background: #E8F7FC url(images/bg.jpg) repeat-x; color: #306172; }
a { color: #3A65A8; text-decoration: none; }
h1 { font-size: 2.9em; font-weight: normal; }
h2 { float: left; text-transform: lowercase; clear: both; font-size: 2.4em; margin: 0 0 20px; font-weight: normal; color: #CB6F9C; background: url(images/h2bg.jpg) repeat-x bottom; }
p  { clear: both; margin: 5px 0 15px; line-height: 1.7em; }
.clear { clear: both; }

.wrap { margin: 0 auto; width: 900px; }
#logo { float: left; margin: 40px 0 0; }
#menu { text-transform: lowercase; float: right; height: 120px; padding: 73px 0 0 98px; width: 475px; background: url(images/white_bubbles.jpg) no-repeat top right; }
	#menu li { display: inline; }
		#menu li a { float: left; padding: 3px 6px; margin: 0 20px 0 0; font-weight: bold; color: #B25281;  }
		#menu li a:hover, #menu li a.current { background: #EDD3E0; }

#text { clear: both; margin: 0 0 40px; }

#green_bubble { padding: 30px 0 68px 280px; height: 30px; background: url(../images/green_bubbles.jpg) no-repeat center left; }
	#green_bubble a { color: #34AE61; font-weight: bold; margin: 0 30px 0 0; }

#footer { padding: 85px 0 50px 0; background: #FF99CB url(../images/bottom.jpg) repeat-x; }
	#copyright { float: left; margin: 80px 0 0; color: #9D436F; font-size: .8em; }
		#copyright p { margin: 0 0 5px; }
		#copyright a { color: #60183B; }
	#bubble { float: right;  font-size: .9em; font-weight: bold; color: #D46FA0; background: url(../images/pink_bubbles.jpg) no-repeat; width: 220px; height: 145px; padding: 52px 30px 0 150px; text-align: right; }
</style>
<script type="text/javascript"> 
function displaySubMenu(li) { 
var subMenu = li.getElementsByTagName("ul")[0]; 
subMenu.style.display = "block"; 
} 
function hideSubMenu(li) { 
var subMenu = li.getElementsByTagName("ul")[0]; 
subMenu.style.display = "none"; 
} 
</script> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"; 
%>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<meta name="description" content="Happy template" />
	<meta name="keywords" content="happy, template" />	
	<meta name="author" content="Luka Cvrk (www.cssmoban.com)" />
	<link rel="stylesheet" href="css/main.css" type="text/css" />
	<title>Happy Template - Free Template by cssMoban.com.com</title>
</head><body>
	<div class="wrap">
		<h1 id="logo"><a href="#">happy test</a></h1>
		<ul id="menu">
			<li><a href="#">Home</a></li>		    
			<li><a href="<%=basePath %>runrule.do" target="_blank">开始校验</a></li> 
			<li><a href="<%=basePath %>createrule.do" target="_blank">创建规则</a></li>
			<li><a href="<%=basePath %>rulelist.do" target="_blank">规则管理</a></li>
		</ul>
		
		<div id="text">
			<h2>detail测试平台小工具</h2>
			<p>提供给宝贝详情团队的mm&gg试用的 <a href="#"></a><a href="#"></a><a href="#"></a></p>
		</div>

		<div id="green_bubble">
			<!--  <p><a href="http://www.declips.com" title="serving your daily dose of interesting and relevant videos from all major video sites">Latest Project</a><a href="http://www.cssmoban.com/commercial-templates" title="Professional CSS Templates">Showcase</a><a href="#">Philosophy</a><a href="#">Vision</a></p>-->
		</div>
	</div>

	<div id="footer">
		<div class="wrap">
			<div id="bubble"><p>if not now,</br>when?</br>if not me,</br>who?</p></div>
			<div id="copyright">
				<p>Copyright &copy; 2013 &minus; Happy Test &minus; Design: xueqing.ln@alibaba-inc.com, <a title="www.taobao.com" href="http://www.taobao.com/">taobao.com</a></p>
			</div>
			<div class="clear"></div>
		</div>
	</div>
</body>
</html>
