<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="javax.servlet.http.Cookie"%>
<%@page import="java.io.*"%>
<%@page import="java.util.*"%>
<%@page import="com.speno.xedm.util.*"%>
<%@page import="com.speno.xedm.core.service.serials.*"%>
<%@page import="com.speno.xedm.gwt.services.*"%>

<script>
// 파라미터를 그대로 포워딩하기 위하여 url 파싱
var parser = document.createElement('a');
parser.href = window.location.href;

var search = parser.search.substring(1, parser.search.length);
</script>


<%
// TODO: site's validation object
String sid = request.getParameter("sid");

SecurityService auth = (SecurityService) Context.getInstance().getBean(SecurityService.class);
SSession edmSession = auth.login(sid);
if (edmSession != null && edmSession.isLoggedIn()) {
%>

<script>
	// 직접 로그인하도록 포워딩
	var link = './frontend.jsp?ssosid=' + '<%= edmSession.getSid() %>' + '&' + search + parser.hash;
	//alert(link);
	window.location = link;
</script>

<%
} else {
%>
<script>
// 직접 로그인하도록 포워딩
var link = './frontend.jsp?sso=true' + '&' + search + parser.hash;
//alert(link);
window.location = link;
</script>
<%
}
%>