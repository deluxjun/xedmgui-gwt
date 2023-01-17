<%@page import="java.util.Locale"%>
<%@page import="com.speno.xedm.core.i18n.I18N"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" href="/xedm/css/property/styles.css" type="text/css" media="screen" />
<link rel='stylesheet' type='text/css' href='/xedm/css/jquery-contextMenu.css '/>
<script src='/xedm/js/jquery-1.10.2.js'></script>
<script src="/xedm/js/jquery-ui-1.10.4.custom.min.js"></script>
<script src="/xedm/js/jquery-contextMenu.js"></script>
</head>
<script>

	$(document).ready(function() {
		$("#tabs").tabs();
		
		//속성탭
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
		 
		 $('#r_object_id').change(function () {
			 window.open('about:blank', '_self').close();
		   });
		 
		 $(function() {
					$('#submit').click(function() {
						var data = $("form").serialize();
						if($('#submit').val() == "<%= I18N.message("save") %>"){
							var request = $.ajax({
								type : "GET",
								url : "/xedm/json/createDoc",
								data : data,
								async: false,
								success : function(html) {
									if(html.errorCode == "0")
										$('#r_object_id').val(html.id);
								}
							});
						}
					
					else if($('#submit').val() == "<%= I18N.message("update") %>"){
						var request = $.ajax({
							type : "GET",
							url : "/xedm/json/updateDoc",
							data : data,
							async: false,
							success : function(html) {
								if(html.errorCode == "0")
									$('#r_object_id').val(html.id);
							}
						});
					}
				});
			});
		 
		 
	 	//확장속성탭
	 	var templateAttribute = function () {
			 var templateId = $('#templateId option:selected');
			 
			 $('#extendedAttributeList').empty();
			 if(templateId.val() == '(<%= I18N.message("notspecified") %>)') return;
			 var template = 'extendedAttribute[name^='+templateId.val()+']';
			 
			 $('#extendedAttributeList').append("<table id=templateTable align=right border=0>");
			 for(i = 0 ; i <$(template).length ; i++ ){
				 var ex = 'extendedAttribute[name='+templateId.val()+'-'+i+']';
				 var label = $(ex).attr('label');
				 var mandatory = $(ex).attr('mandatory');
				 var value = $(ex).val();
				 var type = $(ex).attr('type');
				 
				 var pos = label.indexOf('(');
				 var id = label.substring(0,pos);
			 	
				 var trName = 'tr' + id;
				 var tdName = 'td' + id;
				 var tdName2 = 'td' + id+'2';
				$('#templateTable').append('<tr id=' + trName + '>');
				$('#' + trName).append('<td id='+ tdName + '>');
				$('#' + trName).append('<td id='+ tdName2 + '>');
				
				if(mandatory == 1) $('#' + tdName).html("<b>"+label+ " : " + "</b>");
				else $('#' + tdName).html(label+ " : ");
				
				
				var ex1 = 'extendedAttributeValue[name^='+templateId.val()+']';
				
				//동일한 라벨이 있는지 체크
				var isLabel = false;
				for(j = 0 ; j < $(ex1).length ; j++){
					var selectedLabel = $(ex1).eq(j).attr('label');
					if(label == selectedLabel){
						var selectedValue = $(ex1).eq(j).val();
						isLabel = true;
						break;
					}
				}
				
				//데이터 타입에 따른 라벨 디스플레이
				if($(ex).attr('editor') == 0){
					if(isLabel) $('#' + tdName2).append("<input type=text id='"+ id +"' name='"+ id +"' value='"+selectedValue+ "'>");
					else  $('#' + tdName2).append("<input type=text id='"+ id +"' name='"+ id +"' value='"+value+ "'>");
					
					//3번은 date타입
					if(type == 3) $("#"+id).datepicker();
				}
				else if($(ex).attr('editor') == 1){
					var str = value.split(",");
					
					var tag= "";
					tag = "<select id='" + id + "' name='"+ id + "'>";
					if(mandatory == 0) tag = tag +  "<option></option>";
					
					for(j=0; j < str.length; j++){
						var pos1 = str[j].indexOf('(');
						var pos2 = str[j].indexOf(')');
						
						if(pos1 != -1 && pos2 != -1){
							str[j] = str[j].substring(pos1+1,pos2);
							tag = tag + "<option selected value='"+ str[j] +"'>" + str[j] + "</option>";
						}
						else{
							tag = tag + "<option value='"+ str[j] +"'>" + str[j] + "</option>";
						}
					}
					$('#' + tdName2).append(tag);
					$("select[name="+id+"] option[value="+selectedValue+"]").attr("selected",true);
				}
				else if($(ex).attr('editor') == 2){
					$('#' + tdName2).append("<textarea rows=1 ></textarea>");
				}
			 }
		 };
		 templateAttribute();
		 $('#templateId').change(templateAttribute);
		 
		 
		 
		 //버전탭
		 $.contextMenu({
		        selector: '.versionTab', 
		        callback: function(key, options) {
		            var m = "clicked: " + key;
		            window.console && console.log(m) || alert(m); 
		        },
		        items: {
		            "edit": {name: "Edit", icon: "edit"},
		            "cut": {name: "Cut", icon: "cut"},
		            "copy": {name: "Copy", icon: "copy"},
		            "paste": {name: "Paste", icon: "paste"},
		            "delete": {name: "Delete", icon: "delete"},
		            "sep1": "---------",
		            "quit": {name: "Quit", icon: "quit"}
		        }
		    });
		    
		    $('.versionTab').on('click', function(e){
		        
		    })
	});
	 	
	function popWindow(str){
		var url = '/xedm/download?elementId='+str;
		url = url + '&docId=${docId }'
		window.open(url, 'CLIENT_WINDOW', 'resizable=yes scrollbars=yes width=300 height=300');
	}

</script>

<body>
   <form name="regist">
	<div style="width: 1000px; margin: 0 auto 0 auto;" id="tabs">

	<ul id="navigation">
				<li class="selected"><a href="#propertyTab" ture><%= I18N.message("properties") %></a></li>
				<li ><a href="#extentionTab"><%= I18N.message("extentionproperties") %></a></li>
				<c:if test="${!empty versionList }"><li ><a href="#versionTab"><%= I18N.message("version") %></a></li></c:if>
				<c:if test="${!empty historyList }"><li ><a href="#historyTab"><%= I18N.message("history") %></a></li></c:if>
				<c:if test="${!empty securityList }"><li ><a href="#securityTab"><%= I18N.message("doclevelsecurity") %></a></li></c:if>
	</ul>
	
	<div id="propertyTab" style="float:center ;width:930px;height:350px;" >
		<br><br>
				<table align=center border=0  bordercolor=white  bordercolorlight=white   cellspacing=0 cellpadding=2 width=90%>
					<c:if test="${!empty docId }">
						<tr>
							<td bgcolor=#ececec align="right"><b><%= I18N.message("id") %>&nbsp&nbsp</b></td>
							<td>&nbsp${docId }</td>
						</tr>
					</c:if>
					<tr>
						<td bgcolor=#ececec align="right" ><b><%= I18N.message("title") %>&nbsp&nbsp</b></td>
						<td>
							&nbsp<input type=text name=title size=12 maxlength=12 value='${fileName}${name}' >		
							<br>&nbsp<input id='versionCheck'  type=checkbox  <c:if test="${doctype.versionControl == 1}"> checked</c:if> disabled><%= I18N.message("second.versioncontrol") %>
						</td>
					</tr>
					<tr>
						<td bgcolor=#ececec align="right" ><b><%= I18N.message("doctype") %>&nbsp&nbsp</b></td>
						<td id='documentTypeName'>&nbsp${docType.name }</td>
					</tr>
					<tr>
						<td bgcolor=#ececec align="right" ><b><%= I18N.message("second.changeDoctype") %>&nbsp&nbsp</b></td>
						<td>&nbsp
							<select id="documentType" name="documentType" >
								<c:forEach var="doctype" items="${docTypes}" varStatus="i" begin="0">
								<option testValue='${doctype.versionControl}' <c:if test="${doctype.id== docType.id}"> selected</c:if>  value="${doctype.id}">${doctype.name}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td bgcolor=#ececec align="right" ><b><%= I18N.message("retention") %>&nbsp&nbsp</b></td>
						<td id='retentionName'>&nbsp&nbsp${retentionProfile.name}${expireDate}</td>
					</tr>
					<tr>
						<td bgcolor=#ececec align="right" ><b><%= I18N.message("second.changeRetention") %>&nbsp&nbsp</b></td>
						<td>&nbsp
							<select id="retention" name="retention">
								<c:forEach var="retention" items="${retentionProfiles}" varStatus="i" begin="0">
								<option <c:if test="${i.index == 0}"> selected</c:if>  value="${retention.id}">${retention.name}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td bgcolor=#ececec align="right" ><b><%= I18N.message("second.searchKeyWord") %>&nbsp&nbsp</b></td>
						<td>&nbsp&nbsp<input type=text name=keyword size=12 maxlength=5 value='${keyWord}'></td>
					</tr>
					<tr>
						<td bgcolor=#ececec align="right" ><b><%= I18N.message("description") %>&nbsp&nbsp</b></td>
						<td>&nbsp
							<textarea cols=50 rows=5 name=description>${description}</textarea>		
						</td>
					</tr>
					<tr>
						<td bgcolor=#ececec  align=right><b><%= I18N.message("attachfile") %>&nbsp&nbsp</b></td>
						<td>&nbsp${fileNames}
							<c:if test="${!empty contents }">
								<c:forEach var="content" items="${contents}" varStatus="i" begin="0">
								<button elementId = '${content.elementId}' fileName = '${content.fileName}' onclick='popWindow(${content.elementId});'>${content.fileName}</button>
								</c:forEach>
							</c:if>
						</td>
					</tr>
					<tr>
						<td bgcolor=#ececec colspan=2 align=center>
							<c:if test="${empty versionList }">
							<input type=submit value=<%=I18N.message("save") %> id=submit>
							</c:if>
							<c:if test="${!empty versionList }">
							<input type=submit value=<%=I18N.message("update") %> id=submit>
							</c:if>
						</td>
					</tr>
				</table>
	</div>
	<div id='extentionTab'  style="float:center ;width:930px;height:350px;"" >
				<div style="float:left;width:400px;margin-top: 30px"">
					<table align=center border=0 bordercolor=white  bordercolorlight=white cellspacing=0 height=auto>
						<tr align=left>
							<td bgcolor=#ececec align=left><b><%= I18N.message("template") %></b></td>
							<td align=left>
								<select id="templateId" name="templateId" >
									<option<c:if test="${empty templateId }"> selected </c:if>>(<%= I18N.message("notspecified") %>) </option>
									<c:forEach var="template" items="${templates}" varStatus="i" begin="0">
										<c:if test="${template.id == templateId }">
											<option  value="${template.id}" selected>${template.name}
											<c:forEach var="attr" items="${templateAttr}" varStatus="j" begin="0">
											<extendedAttributeValue name='${template.id }-${j.index}'  editor= "${attr.editor}" mandatory= "${attr.mandatory}" type= "${attr.type}" label= "${attr.label}" value= "${attr.value}"> </extendedAttributeValue>
											</c:forEach>
										</c:if>
										<c:if test="${template.id != templateId }">
											<option  value="${template.id}">${template.name}
										</c:if>
											<c:forEach var="attr" items="${template.attributeList}" varStatus="j" begin="0">
											<extendedAttribute name='${template.id }-${j.index}'  editor= "${attr.editor}" mandatory= "${attr.mandatory}" type= "${attr.type}" label= "${attr.label}" value= "${attr.value}"> </extendedAttribute>
											</c:forEach>
										</option>
									</c:forEach>
								</select>
							</td>
						</tr>
					</table>
			</div>
			<div id=extendedAttributeList align="right" style="clear:both;float:right;width:500px; margin-top: 30px ; margin-right: 50px"></div>
	</div>
	
	<c:if test="${!empty versionList }">
				<div id='versionTab' class = 'versionTab' style="float:center ;width:930px;height:350px;" >
				<br><br>
					<table align=center border=0 bordercolor=white  bordercolorlight=white cellspacing=0  width=90% >
						<tr>
							<td bgcolor=#ececec align=center><b><%= I18N.message("user") %></b></td>
							<td bgcolor=#ececec align=center><b><%= I18N.message("event") %></b></td>
							<td bgcolor=#ececec align=center><b><%= I18N.message("title") %></b></td>
							<td bgcolor=#ececec align=center><b><%= I18N.message("version") %></b></td>
							<td bgcolor=#ececec align=center><b><%= I18N.message("date") %></b></td>
							<td bgcolor=#ececec align=center><b><%= I18N.message("description") %></b></td>
						</tr>
						<c:forEach var="ver" items="${versionList}" varStatus="i" begin="0">
						<tr align=center>
							<td> ${ver.username } </td>
							<td> ${ver.event } </td>
							<td> ${ver.title } </td>
							<td> ${ver.version } </td>
							<td> ${ver.creationDate } </td>
							<td> ${ver.comment } </td>
						</tr>
						</c:forEach>
					</table>
				</div>
			</c:if>
			
			<c:if test="${!empty historyList }">
				<div id='historyTab' style="float:center ;width:930px;height:350px;" >
				<br><br>
					<table align=center border=0 bordercolor=white  bordercolorlight=white cellspacing=0  width=90%>
						<tr>
							<td bgcolor=#ececec align=center><b><%= I18N.message("user") %></b></td>
							<td bgcolor=#ececec align=center><b><%= I18N.message("event") %></b></td>
							<td bgcolor=#ececec align=center><b><%= I18N.message("title") %></b></td>
							<td bgcolor=#ececec align=center><b><%= I18N.message("version") %></b></td>
							<td bgcolor=#ececec align=center><b><%= I18N.message("date") %></b></td>
							<td bgcolor=#ececec align=center><b><%= I18N.message("comment") %></b></td>
							<td bgcolor=#ececec align=center><b><%= I18N.message("path") %></b></td>
						</tr>
						<c:forEach var="his" items="${historyList}" varStatus="i" begin="0">
						<tr align=center>
							<td> ${his.userName } </td>
							<td> ${his.event } </td>
							<td> ${his.title } </td>
							<td> ${his.version } </td>
							<td> ${his.date } </td>
							<td> ${his.comment } </td>
							<td> ${his.path } </td>
						</tr>
						</c:forEach>
					</table>
				</div>
			</c:if>
			
			<c:if test="${!empty securtyList }">
				<div id='securityTab' style="float:center ;width:930px;height:350px;"" >
					<p align="left">엑세스 제어 목록<p>
					<p align="left">보안 프로파일:
					<select id="retentionProfileId" name="retentionProfileId" >
						<option selected>private-ACL </option>
					</select>
					</p>
				</div>
			</c:if>
		</div>
		
		<input name="r_object_id" id='r_object_id' type=hidden value="">
		<input name="Success" type=hidden value="Y">
		<input name='folderId'  type=hidden value= "${folderId}">
		
		<input name='filePath'  type=hidden value= "${filePath}">
		<input name='fullName'  type=hidden value= "${fileNames}">
		<input name='docId'  type=hidden value= "${docId}">

</div>
<script type="text/javascript">
$(document).ready(function(){
	$("ul#navigation li a").click(function() {
		$("ul#navigation li").removeClass("selected");
		$(this).parents().addClass("selected");
		return false;
	});
});
</script>
</form>
</body>
</html>