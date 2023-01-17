<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="javax.servlet.http.Cookie"%>
<%@page import="java.io.*"%>
<%@page import="java.util.*"%>
<%@page import="com.speno.xedm.util.*"%>
<%!
    public String getClientVersion(HttpServletRequest request){
      return getCookie("xedm-version",request);
    }

    public String getServerVersion(){
      return getProperty("settings.product.release");
    }
    
    public String getProperty(String name){
      try{
        ContextProperties config=ContextProperties.getInstance();
        return config.getProperty(name);
      }catch(Throwable t){
      }  
      return null;
    }
    
    public String getCookie(String name, HttpServletRequest request){
      Cookie[] cookielist = request.getCookies();
      if(cookielist != null){
        for(int i = 0; i < cookielist.length;i++)
          if(name != null && name.equals(cookielist[i].getName()))
            return cookielist[i].getValue();
      }      
      return null;
    }
    
    public String message(String message, HttpServletRequest request){
        try{
          ResourceBundle res = ResourceBundle.getBundle("i18n.messages", request.getLocale());
          return res.getString(message);
        }catch(Throwable t){
        }
        return message;
    }
%>
<%
  String locale = request.getParameter("locale");
	if (locale == null || "".equals(locale))
		locale = "";
		
  String sVersion=getServerVersion();
  String cVersion=getClientVersion(request);
  boolean versionChanged=cVersion!=null && !cVersion.equals(sVersion);
  
  if(versionChanged &&
		  !"checkversion".equals(MODULE) &&
		  !"true".equals(request.getParameter("skipreleasecheck")))
     response.sendRedirect("./checkversion.jsp");
%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!-- UP AND RUNNING -->
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
<!-- 		<meta http-equiv="X-UA-Compatible" content="IE=9"> -->
        <meta http-equiv="X-UA-Compatible" content="IE=Edge" />

		<title></title>
		<!--CSS for loading message at application Startup-->
		<style type="text/css">
body {
	overflow: hidden
}

#loading {
	border: 1px solid #ccc;
	position: absolute;
	left: 45%;
	top: 40%;
	padding: 2px;
	z-index: 20001;
	height: auto;
}

#loading a {
	color: #225588;
}

#loading .loadingIndicator {
	background: white;
	font: bold 13px tahoma, arial, helvetica;
	padding: 10px;
	margin: 0;
	height: auto;
	color: #444;
}

#loadingMsg {
	font: normal 10px arial, tahoma, sans-serif;
}
</style>

<link REL="STYLESHEET" HREF="./skin/edmstyle.css" TYPE="text/css" />
<link rel="shortcut icon" type="image/x-icon" href='./skin/brand/favicon.ico' />
<!-- google visualization 을 offline에서 사용가능하도록 함 -->
<script type="text/javascript" src="jsapi"></script>
<script type="text/javascript" src="uds_api_contents.js"></script>
<script type="text/javascript">		
	// Determine what skin file to load
  var currentSkin = "Enterprise";
  var isomorphicDir = "<%=MODULE%>/sc/";
  
</script>

</head>

<!-- 20140505, junsoo, for self closing -->
<body onload="window.open('', '_self', '');">