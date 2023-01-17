<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%! static String MODULE="frontend"; %>
<%@ include file="header.jsp" %>


    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
	<iframe src="javascript:''" id="__DownloadFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>


		<!--add loading indicator while the app is being loaded-->
		<div id="loadingWrapper">
			<div id="loading">
				<div class="loadingIndicator">
					<img src="./skin/images/loading32.gif" width="32" height="32" style="margin-right: 8px; float: left; vertical-align: top;" />
					<span id="loadingTitle">EDM Suite</span>
					<br />
					<span id="loadingMsg">Loading styles and images...</span>
				</div>
			</div>
		</div>
		

<!-- <script type="text/javascript">document.getElementById('loadingTitle').innerHTML = 'Loading';</script> -->

<script type="text/javascript">document.getElementById('loadingMsg').innerHTML = 'Loading Core API...';</script>

<!--include the SC Core API-->
<script src=<%=MODULE%>/sc/modules/ISC_Core.js></script>

<!--include SmartClient -->
<script type="text/javascript">document.getElementById('loadingMsg').innerHTML = 'Loading UI Components...';</script>
<script src='<%=MODULE%>/sc/modules/ISC_Foundation.js'></script>
<script src='<%=MODULE%>/sc/modules/ISC_Containers.js'></script>
<script src='<%=MODULE%>/sc/modules/ISC_Grids.js'></script>
<script src='<%=MODULE%>/sc/modules/ISC_Forms.js'></script>
<script src='<%=MODULE%>/sc/modules/ISC_RichTextEditor.js'></script>
<script src='<%=MODULE%>/sc/modules/ISC_Calendar.js'></script>
<script type="text/javascript">document.getElementById('loadingMsg').innerHTML = 'Loading Data API...';</script>
<script src='<%=MODULE%>/sc/modules/ISC_DataBinding.js'></script>
<script src='<%=MODULE%>/sc/modules/ISC_Drawing.js'></script>

<!--load skin-->
<script type="text/javascript">document.getElementById('loadingMsg').innerHTML = 'Loading skin...';</script>
<script type="text/javascript">
    document.write("<"+"script src=" + isomorphicDir + "skins/" + currentSkin + "/load_skin.js?isc_version=9.0.js><"+"/script>");
</script>


    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
	<script type="text/javascript">document.getElementById('loadingMsg').innerHTML = 'Loading...';</script>
	
	
<%@ include file="footer.jsp" %>

