<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META content="text/html; charset=euc-kr" http-equiv=Content-Type>
<title>비주얼인포시스 이미지뷰어 샘플</title>
<style type="text/css">
.button {
	FONT-FAMILY: Verdana, Helvetica, Arial, San-Serif, 굴림;
	font-weight:normal;
	font-size:70%;
	color:#000000;
	background-color:#ffffff;
	border-color:#CECFCE;
	margin-top:6pt;
	margin-left: .5em;	
}
</style>

<script type="text/javascript" src="frontend/frontend.nocache.js"></script>
<script type="text/javascript">

function testF()
{
	alert("aaa");
	alert(test);
}

function Addfile()
{
	var spath = "C:/Users/Lenovo/Desktop/NIPA/20130319_데모개발/VisViewer/1.jpg";
	nIndex = visView.Count + 1;
	visView.ImageAppend(spath, nIndex);
}


<!--	뷰어 초기화.	-->
function Clear()
{
	alert('clear');
	visView.Clear();
}

<!--	뷰어 파일저장.	-->
function SaveImage()
{
	var spath = "c:/visviewer/image.tif";
	if(visView.ImageSave( 0, spath, 1, 24, 50))
		alert(spath + " 경로에 저장하였습니다.");
}

function SaveMultiImage()
{
	var spath = "c:/visviewer/Multiimage.tif";
	if(visView.ImageSaveMultiTiff(spath, 50))
		alert(spath + " 경로에 저장하였습니다.");
}

<!--	뷰어 출력.	-->
function PrintImage()
{
	
	visView.ImagePrint(1);
}

<!--	뷰어 확대.	-->
function ZoomIn()
{
	visView.ZoomMode = 4;
	visView.ZoomRatio = visView.ZoomRatio + 0.1;
}

<!--	뷰어 축소.	-->
function ZoomOut()
{
	visView.ZoomMode = 4;
	visView.ZoomRatio = visView.ZoomRatio - 0.1;
}

<!--	뷰어 맞춤 보기.	-->
function ZoomMode(mode)
{
	/*
	가로 맞춤	: 0
	세로 맞춤	: 1
	페이지 맞춤	: 2
	*/
	visView.ZoomMode=mode;
}


<!--	뷰어 회전.	-->
function Rotate(angle)
{
	/*
	angle : 90, 180, 270
	*/
	visView.ImageRotate(angle);
}


<!--	뷰어 보기 모드.	-->
function ViewMode(mode)
{
	/*
	0 : 썸네일 보기
	1 : 페이지 보기
	2 : 모두 보기
	*/
	visView.ViewMode = mode;
}

</SCRIPT>


</head>

<body leftmargin="0" topmargin="0" marginheight="0" marginwidth="0" bgcolor="white" scroll=yes >
<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
<!--  <tr><td><INPUT id=descr name=descr size=30 maxlength=64 value="a"></TD></TR>  -->
  <tr><td colspan= 2>&nbsp;</td></tr>
  <tr>
  <td valign="top">
  <tr>
  <input class="button" type="button" name="btn" style ="height:25px;width:150;" value="aaaa" onclick="javascript: testF();">
  </tr>

  </td>
  </tr>
  
<tr>	
  <td class="grid" align="middle" height="100%" width="100%" colspan= 3>
  <OBJECT id=visView height=80% width=100% classid="clsid:043EE051-42B4-429C-A234-4231AD4187DA">
    	<PARAM NAME="_Version" VALUE="65536">
	<PARAM NAME="_ExtentX" VALUE="18045">
	<PARAM NAME="_ExtentY" VALUE="13838">
	<PARAM NAME="_StockProps" VALUE="0">
	<PARAM NAME="grayScale" VALUE="1">	    <!-- 	그레이 적용화면을 볼지 여부(1:그레이 씌워서 봄, 2:원본대로 봄)	-->
	<PARAM NAME="UseAnnotation" VALUE="2">	<!--	어노테이션을 사용여부 (0:사용안함, 1:보기모드, 2:수정모드)	-->
	<param name= "enablesave" value ="1" >	
  </OBJECT>         
  </td>
  </tr>
  <tr><td colspan= 2>&nbsp;</td></tr>
</table>

</body>
</html>

