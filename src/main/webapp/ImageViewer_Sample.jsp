<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META content="text/html; charset=euc-kr" http-equiv=Content-Type>
<title>���־������ý� �̹������ ����</title>
<style type="text/css">
.button {
	FONT-FAMILY: Verdana, Helvetica, Arial, San-Serif, ����;
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
	var spath = "C:/Users/Lenovo/Desktop/NIPA/20130319_���𰳹�/VisViewer/1.jpg";
	nIndex = visView.Count + 1;
	visView.ImageAppend(spath, nIndex);
}


<!--	��� �ʱ�ȭ.	-->
function Clear()
{
	alert('clear');
	visView.Clear();
}

<!--	��� ��������.	-->
function SaveImage()
{
	var spath = "c:/visviewer/image.tif";
	if(visView.ImageSave( 0, spath, 1, 24, 50))
		alert(spath + " ��ο� �����Ͽ����ϴ�.");
}

function SaveMultiImage()
{
	var spath = "c:/visviewer/Multiimage.tif";
	if(visView.ImageSaveMultiTiff(spath, 50))
		alert(spath + " ��ο� �����Ͽ����ϴ�.");
}

<!--	��� ���.	-->
function PrintImage()
{
	
	visView.ImagePrint(1);
}

<!--	��� Ȯ��.	-->
function ZoomIn()
{
	visView.ZoomMode = 4;
	visView.ZoomRatio = visView.ZoomRatio + 0.1;
}

<!--	��� ���.	-->
function ZoomOut()
{
	visView.ZoomMode = 4;
	visView.ZoomRatio = visView.ZoomRatio - 0.1;
}

<!--	��� ���� ����.	-->
function ZoomMode(mode)
{
	/*
	���� ����	: 0
	���� ����	: 1
	������ ����	: 2
	*/
	visView.ZoomMode=mode;
}


<!--	��� ȸ��.	-->
function Rotate(angle)
{
	/*
	angle : 90, 180, 270
	*/
	visView.ImageRotate(angle);
}


<!--	��� ���� ���.	-->
function ViewMode(mode)
{
	/*
	0 : ����� ����
	1 : ������ ����
	2 : ��� ����
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
	<PARAM NAME="grayScale" VALUE="1">	    <!-- 	�׷��� ����ȭ���� ���� ����(1:�׷��� ������ ��, 2:������� ��)	-->
	<PARAM NAME="UseAnnotation" VALUE="2">	<!--	������̼��� ��뿩�� (0:������, 1:������, 2:�������)	-->
	<param name= "enablesave" value ="1" >	
  </OBJECT>         
  </td>
  </tr>
  <tr><td colspan= 2>&nbsp;</td></tr>
</table>

</body>
</html>

