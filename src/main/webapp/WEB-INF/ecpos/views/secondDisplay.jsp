<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String http_message = (String) (request.getAttribute("http_message") == null ? "" : request.getAttribute("http_message"));
%>
<!DOCTYPE html>
<html
	style="-webkit-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none;"
	oncontextmenu="return false">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">

<!-- Page title set in pageTitle directive -->
<title page-title class="font-roboto-regular">VernPOS Second Display</title>

<!-- ****BASE FOLDER DEFINE**** -->
<base href="/">

<link rel="shortcut icon" type="image/ico"
	href="${pageContext.request.contextPath}/meta/img/ecpos_logo.png" />

<style>
.no-js #loader {
	display: none;
}

.js #loader {
	display: block;
	position: absolute;
	left: 100px;
	top: 0;
}

.se-pre-con {
	position: fixed;
	left: 0px;
	top: 0px;
	width: 100%;
	height: 100%;
	z-index: 9999;
	background: url(images/loader-64x/Preloader_2.gif) center no-repeat #fff;
}

@font-face {
	font-family: 'robotofontregular';
	src: url('${pageContext.request.contextPath}/font/Roboto-Regular.ttf');
}

.font-roboto-regular {
	font-family: robotofontregular;
}
</style>

<!-- *****Admin LTE****** -->
<!-- Bootstrap 3.3.7 -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/bootstrap/dist/css/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.5.0/css/font-awesome.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/fontawesome-pro-5.6.1/css/all.css">
<!-- Ionicons -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css">
<!-- Theme style -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/adminLTE-2.4.5/dist/css/AdminLTE.min.css">
<!-- AdminLTE Skins. Choose a skin from the css/skins folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/adminLTE-2.4.5/dist/css/skins/_all-skins.min.css">
<!-- iCheck -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/iCheck/flat/blue.css">
<!-- jvectormap -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/jvectormap/jquery-jvectormap-1.2.2.css">
<!-- Date Picker -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/datepicker/datepicker3.css">
<!-- Daterange picker -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/daterangepicker/daterangepicker.css">
<!-- bootstrap wysihtml5 - text editor -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css">
<!-- DataTables -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/datatables/dataTables.bootstrap.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/datatables/extensions/Select/css/select.bootstrap.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/datatables/extensions/Buttons/css/buttons.dataTables.min.css">
<!-- ui-cropper -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/ui-cropper/css/ui-cropper.css">
<!-- iCheck for checkboxes and radio inputs -->
<%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/iCheck/all.css"> --%>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/iCheck/minimal/_all.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/sweetAlert/sweetalert2.css">
<!-- loadingbar -->
<%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/css/loadingbar.css"> --%>

<!-- ****ADMIN LTE TEMPLATE UNIVERSAL JQUERY**** -->
<!-- jQuery 2.2.3 -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/jQuery/jquery-2.2.3.min.js"></script>
<script src="${pageContext.request.contextPath}/js/loadImg.js"></script>
<!-- jQuery UI 1.11.4 -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery-ui/jquery-ui.min.js"></script>
<!-- Resolve conflict in jQuery UI tooltip with Bootstrap tooltip -->
<script>
	$.widget.bridge('uibutton', $.ui.button);
</script>

<!-- ****ADMIN LTE TEMPLATE UNIVERSAL IMPORT**** -->
<!-- Bootstrap 3.3.7 -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<!-- Sparkline -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery-sparkline/dist/jquery.sparkline.min.js"></script>
<!-- jvectormap -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/jvectormap/jquery-jvectormap-world-mill-en.js"></script>
<!-- jQuery Knob Chart -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery-knob/dist/jquery.knob.min.js"></script>
<!-- daterangepicker -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/moment/min/moment.min.js"></script>
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/daterangepicker/daterangepicker.js"></script>
<!-- datepicker -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/datepicker/bootstrap-datepicker.js"></script>
<!-- Bootstrap WYSIHTML5 -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min.js"></script>
<!-- Slimscroll -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- FastClick -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/fastclick/lib/fastclick.js"></script>
<!-- AdminLTE App -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/dist/js/adminlte.min.js"></script>
<!-- AdminLTE for demo purposes -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/dist/js/demo.js"></script>
<!-- DataTables -->
<script
	src="${pageContext.request.contextPath}/datatables/jquery.dataTables.min.js"></script>
<script
	src="${pageContext.request.contextPath}/datatables/dataTables.bootstrap.min.js"></script>
<script
	src="${pageContext.request.contextPath}/datatables/extensions/Select/js/dataTables.select.min.js"></script>
<script
	src="${pageContext.request.contextPath}/datatables/extensions/Buttons/js/dataTables.buttons.min.js"></script>
<script
	src="${pageContext.request.contextPath}/datatables/extensions/Buttons/js/buttons.html5.min.js"></script>

<!-- *****ANGULAR JS****** -->
<script
	src="${pageContext.request.contextPath}/angular-1.7.5/js/angular.min.js"></script>
<script
	src="${pageContext.request.contextPath}/angular-1.7.5/js/angular-route.min.js"></script>
<script
	src="${pageContext.request.contextPath}/angular-1.7.5/js/angular-messages.min.js"></script>
</head>

<!-- Body -->
<!-- appCtrl controller with serveral data used in theme on diferent view -->
<body ng-app="secondDisplayApp"
	class="hold-transition skin-blue sidebar-mini font-roboto-regular">
	<div ng-controller="secondDisplay_CTRL" class="wrapper"
		style="background-color: #0000000d; height: 100%; width: 100%; overflow-y: hidden;">
		<div style="width: 100%; height:100%; font-size: 20px; background: #f1f2f6; color: #FFFFFF; margin: 0px; height: 100vh; position: absolute;">
			<marquee direction="left" width="100%" style="text-align: center;" Scrollamount=7><span id='storename' style="word-spacing: 1px; font-style:italic; color: #ff6348; font-size: 44px;"><b>Welcome to Cufe</b></span></marquee>
			<!--  - <span id='date-part' style="word-spacing: 1px; color: white; font-size: 40px;"></span> -->
			<div ng-bind-html="contentKds" style="padding-left: 10px; padding-right: 40px; color: #2f3542; font-size: 30px;"></div>
		</div>
		<div ng-init="initOrder();" id="contentId"
			style="height: calc(100vh - 0px); ">
		</div>
	</div>
</body>
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/dist/js/pages/dashboard.js"></script>
<!-- iCheck 1.0.1 -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/iCheck/icheck.min.js"></script>
<!-- ***** ANGULAR JS CONTROLLER ***** -->
<jsp:include page="/WEB-INF/ecpos/controller/secondDisplay_CTRL.jsp" />
<script type="text/javascript">
	$(document).ready(
			function() {
				var interval = setInterval(function() {
					var momentNow = moment();
					$('#date-part').html(
							momentNow.format('hh:mm:ss A')
									+ ' ('
									+ momentNow.format('DD/MM/YYYY')+')');
				}, 100);
			});
</script>
</html>
