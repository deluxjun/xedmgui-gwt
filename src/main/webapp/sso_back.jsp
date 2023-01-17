<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.speno.xedm.util.*"%>
<%@page import="com.speno.xedm.core.service.serials.*"%>
<%@page import="com.speno.xedm.gwt.services.*"%>

<!-- =====================================================
	XEDM Suite SSO jsp 모듈
	
	각 사이트는 이 jsp 를 복사하여 사용할 것
	Indentify 서버(사이트 인증서버)의 validation 체크는 ExternalAuthenticationProvider를 상속받아 구현하고, plugin.xml에 등록하여야 함.
	
	현재는 이 jsp를 통하여 들어올 경우, 무조건 인증이 통과 됨

<!-- ===================================================== -->

<%
SecurityService auth = (SecurityService) Context.getInstance().getBean(SecurityService.class);

// url option들을 그대로 전달.
String url = request.getRequestURI();
int symi1 = url.indexOf("?");
int symi2 = url.indexOf("#");
String symbol1 = "";
String symbol2 = "";
if (symi1 > 0 && symi2 > 0) {
	if (symi1 < symi2) {
		symbol1 = url.substring(symi1+1, symi2);
		symbol2 = url.substring(symi2);
	} else {
		symbol2 = url.substring(symi2, symi1);
		symbol1 = url.substring(symi1+1);
	}
} else if (symi1 > 0) {
	symbol1 = url.substring(symi1+1);
} else if (symi2 > 0) {
	symbol2 = url.substring(symi2);
}
out.println("<script>alert('" +url+ "')</script>");

// TODO: site's validation object
String validationKey = "";

SSession edmSession = auth.SSOLogin("admin", validationKey, "ko");
if (edmSession.isLoggedIn()) {
//	String link = "/xedm/frontend.jsp?&ssosid=" + edmSession.getSid();
	String link = "/xedm/frontend.jsp?gwt.codesvr=127.0.0.1:9997&ssosid=" + edmSession.getSid() + "&" + symbol1 + symbol2;
	out.println("<script>alert('" +link+ "')</script>");
//	response.sendRedirect(link);
}
%>
