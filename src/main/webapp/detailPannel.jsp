<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel='stylesheet' type='text/css' href='/xedm/css/styles.css' />
<script src='/xedm/js/jquery-1.10.2.js'></script>
<script type='text/javascript' src='/xedm/js/menu_jquery.js'></script>
<title></title>
<script>
$(document).ready(function() {
	$('#extentionTab').css('display','none');
	
	$('#property').click(function(){
		$('#propertyTab').css('display','');
		$('#extentionTab').css('display','none');
	});
	
	$('#extention').click(function(){
		$('#propertyTab').css('display','none');
		$('#extentionTab').css('display','');
	});
	
	 $('#documentType').change(function () {
		 var docType = $('#documentType option:selected');
		 
		$('#documentTypeName').text(docType.text());
		
		var check = docType.attr('testValue');
		
		if(check == 1)
			$('#versionCheck').attr('checked', true)
		else if(check == 0)
			$('#versionCheck').attr('checked', false)
       });
	 
	 $('#retention').change(function () {
			$('#retentionName').text($('#retention option:selected').text());
	       });
});
</script>
</head>
<body>
<script>

	function save() {
		window.open('about:blank','_self');
		opener=window;
		window.close();
	}
	
	
</script>

<div id='cssmenu'>
<ul style='background-color:black'>
   <li id='property'><a href='#'><span>속성</span></a></li>
   <li id='extention'><a href='#'><span>확장속성</span></a></li>
   <li id='version'><a href='#'><span>버전</span></a></li>
   <li id='history'><a href='#'><span>이력</span></a></li>
   <li id='security' class='last'><a href='#'><span>문서 레벨 보안</span></a></li>
</ul>
</div>
	<form name="regist" action="/xedm/json/createDoc">
		<div  id='propertyTab'>
			<table align=center border=1 bordercolor=white bordercolorlight=gray cellspacing=0 cellpadding=2>
				<tr>
					<td bgcolor=#ececec align=center >제목</td>
					<td>
						<input type=text name=title size=12 maxlength=12 value='${name}'>		
						<br><input id='versionCheck'  type=checkbox  <c:if test="${versionControl == 1}"> checked</c:if> disabled>버전제어
					</td>
				</tr>
				<tr>
					<td bgcolor=#ececec align=center>문서종류</td>
					<td id='documentTypeName'>${docTypeName }</td>
				</tr>
				<tr>
					<td bgcolor=#ececec align=center>문서종류 변경</td>
					<td>
						<select id="documentType" name="documentType" >
							<c:forEach var="doctype" items="${docTypes}" varStatus="i" begin="0">
							<option testValue='${doctype.versionControl}' <c:if test="${i.index == 0}"> selected</c:if>  value="${doctype.id}">${doctype.name}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td bgcolor=#ececec align=center>보존기간</td>
					<td id='retentionName'>${expireDate}</td>
				</tr>
				<tr>
					<td bgcolor=#ececec align=center>보존기간 변경</td>
					<td>
						<select id="retention" name="retention">
							<c:forEach var="retention" items="${retentionProfiles}" varStatus="i" begin="0">
							<option <c:if test="${i.index == 0}"> selected</c:if>  value="${retention.id}">${retention.name}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td bgcolor=#ececec align=center>검색 키워드</td>
					<td><input type=text name=keyword size=12 maxlength=5 value='${keyWord}' disabled></td>
				</tr>
				<tr>
					<td bgcolor=#ececec align=center>설명</td>
					<td>
						<textarea cols=50 rows=5 name=description value='${description}' disabled></textarea>
					</td>
				</tr>
				<tr>
					<td bgcolor=#ececec colspan=2 align=center>
						<input type=submit value=수정하기>
						<input type=reset value=다시작성>		
					</td>
				</tr>
			</table>
		</div>
		<div id='extentionTab'>
			<table align=center border=1 bordercolor=white bordercolorlight=gray cellspacing=0 cellpadding=2>
				<tr>
					<td bgcolor=#ececec align=center>템플릿</td>
					<td>
						<select id="templateId" name="templateId" >
							<c:forEach var="template" items="${templates}" varStatus="i" begin="0">
							<option  <c:if test="${i.index == 0}"> selected</c:if>  value="${template.id}">${template.name}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
			</table>
		</div>
	</form>
</body>
</html>