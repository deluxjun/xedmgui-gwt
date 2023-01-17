<%@page import="java.util.Locale"%>
<%@page import="com.speno.xedm.core.i18n.I18N"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!doctype html>
<html lang="us">
<head>
	<meta charset="utf-8">
	<title>jQuery UI Example Page</title>
	<link href="/xedm/css/property/jquery-ui.css" rel="stylesheet">
<style>
body{
		font: "Trebuchet MS", sans-serif;
		margin: 50px;
}
table{
 border-collapse:collapse;
 border-left:1px soild;
 border-top:1px soild;
 color:#333;
 border-left-color:#ccc;
 }
 table caption{
 font-size:1.1em;
 font-weight:bold;
 letter-spacing:-1px;
 margin-bottom:10px;
 padding:5px;
 background:#efefef;
 border: 1px solid #ccc;
 }
 table thead tr th{
 background:#e2e2e2;
 border: 1px solid #ccc;
 }
 table td, th{
 padding:5px;
 line-height:1.8em;
 font-size:0.8em;
 vertical-align:middle;
 /*width:20%*/
 }
 ahover{
 display:none;
 decoration:none;
 }
	</style>
</head>
<body>

<div id="tabs">
	<ul>
		<li><a href="#tabs-1"><b>속성</b></a></li>
<%-- 		<li><a href="#tabs-2"><b>보안</b></a></li>--%>
	</ul>
	<div id="tabs-1">
		<h2 class="demoHeaders">폴더 속성</h2>  
		<div>
			<p id="tooltip">
					경로: ${path }<br>
					폴더명: ${folderName}<br>
					설명 :  ${description}<br>
					사용량 : <a href="#" title="" id="ahover">${usedSpace }</a><br>
					총용량 : <a href="#" title="" id="ahover">${totalSpace }</a>
			</a>
		</div>
	  <%-- <button id="button">button</button> --%>

</p>
	</div>
	<%--
	<div id="tabs-2">
	<table cellpadding="0" cellspacing="0" width='100%'>
		<caption>보안 프로파일 : 계약 - ACL 만기일 : 미정</caption>
		<cal width="10%"/>
		<cal width="10%"/>
		<cal width="10%"/>
		<cal width="14%"/>
		<cal width="14%"/>
		<cal width="14%"/>
		<cal width="14%"/>
		<thead>
			<tr>
				<th>이름</th>
				<th>생성</th>
				<th>보기</th>
				<th>읽기</th>
				<th>쓰기</th>
				<th>업데이트</th>
				<th>삭제</th>
			</tr>
		</thead>
		<tbody>
		   <tr>
			<td colspan=8 style="border-bottom: 2px solid #ccc;"> - 내 권한</td>
		   </tr>
		   <tr>
			<td>admin</td>
			<td><input type="checkbox" value="" id=""/></td>
			<td><input type="checkbox" value="" id=""/></td>
			<td><input type="checkbox" value="" id=""/></td>
			<td><input type="checkbox" value="" id=""/></td>
			<td><input type="checkbox" value="" id=""/></td>
			<td><input type="checkbox" value="" id=""/></td>
		   </tr>
		     <tr>
			<td colspan=8 style="border-top: 2px solid #ccc; border-bottom: 2px solid #ccc;"> - 사용자</td>
		   </tr>
		   <tr>
			<td>admin</td>
			<td><input type="checkbox" value="" id=""/></td>
			<td><input type="checkbox" value="" id=""/></td>
			<td><input type="checkbox" value="" id=""/></td>
			<td><input type="checkbox" value="" id=""/></td>
			<td><input type="checkbox" value="" id=""/></td>
			<td><input type="checkbox" value="" id=""/></td>
		   </tr>
		   <tr>
				<td colspan=8 style="border-top: 2px solid #ccc;"></td>
		   </tr>
		</tbody>
	</table>
	</div>
	 --%>
</div>



<script src="/xedm/css/property/jquery.js"></script>
<script src="/xedm/css/property/jquery-ui.js"></script>
<script>
$( "#button" ).button();
$( "#tabs" ).tabs();
$( "#tooltip" ).tooltip();
</script>
</body>
</html>
