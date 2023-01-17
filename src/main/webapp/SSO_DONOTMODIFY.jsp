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


<script>
// 파라미터를 그대로 포워딩하기 위하여 url 파싱
var parser = document.createElement('a');
parser.href = window.location.href;

var search = parser.search.substring(1, parser.search.length);
</script>


<%
// get cookie!
public String getCookie(String name, HttpServletRequest request){
    Cookie[] cookielist = request.getCookies();
    if(cookielist != null){
      for(int i = 0; i < cookielist.length;i++)
        if(name != null && name.equals(cookielist[i].getName()))
          return cookielist[i].getValue();
    }      
    return null;
}

// TODO: site's validation object
String validationKey = getCookie(request, "ssotoken");//쿠키에서 ssotoken 값을 읽어온다

String userName = request.getParameter("username");

SecurityService auth = (SecurityService) Context.getInstance().getBean(SecurityService.class);
SSession edmSession = auth.SSOLogin(validationKey, validationKey, "ko");
if (edmSession.isLoggedIn()) {
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
