<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<!-- <meta name="viewport" content="width=device-width, initial-scale=1.0"> -->
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">

<!-- Page title set in pageTitle directive -->
<title page-title class="font-roboto-regular">ECPOS Manager</title>

<!-- ****BASE FOLDER DEFINE**** -->
<base href="/">

<link rel="shortcut icon" type="image/ico"
	href="${pageContext.request.contextPath}/meta/img/ecpos_logo.png" />

<style>
/* Paste this css to your style sheet file or under head tag */
/* This only works with JavaScript, 
	if it's not present, don't show loader */
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
	src:
		url('${pageContext.request.contextPath}/font/Roboto-Regular.ttf');
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

<%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/css/loadingbar.css"> --%>

<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery/dist/jquery.min.js"></script>
<!-- ****ADMIN LTE TEMPLATE UNIVERSAL JQUERY**** -->
<!-- jQuery 2.2.3 -->
<script src="${pageContext.request.contextPath}/js/loadImg.js"></script>
<!-- jQuery UI 1.11.4 -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery-ui/jquery-ui.min.js"></script>
<!-- Resolve conflict in jQuery UI tooltip with Bootstrap tooltip -->
<script>
	$.widget.bridge('uibutton', $.ui.button);
</script>

<!-- Resolve conflict in jQuery UI tooltip with Bootstrap tooltip -->
<script>
	$.widget.bridge('uibutton', $.ui.button);
</script>

<!-- ****ADMIN LTE TEMPLATE UNIVERSAL IMPORT**** -->
<!-- Bootstrap 3.3.7 -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/bootstrap/dist/js/bootstrap.min.js">
<!-- Sparkline -->
	<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery-sparkline/dist/jquery.sparkline.min.js">
</script>
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
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/dist/js/demo.js"></script>

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

<script src="https://cloud.tinymce.com/stable/tinymce.min.js"></script>

<!-- *****ANGULAR JS****** -->
<script
	src="${pageContext.request.contextPath}/angular-1.7.5/js/angular.min.js"></script>
<script
	src="${pageContext.request.contextPath}/angular-1.7.5/js/angular-route.min.js"></script>
<script
	src="${pageContext.request.contextPath}/angular-1.7.5/js/angular-messages.min.js"></script>
	
<!-- *****OTHERS JS***** -->
<script src="${pageContext.request.contextPath}/qrcode/qrcode.min.js"></script>
<script src="${pageContext.request.contextPath}/ui-cropper/js/ui-cropper.js"></script>

</head>

<!-- Body -->
<!-- appCtrl controller with serveral data used in theme on diferent view -->
<body ng-app="myApp"
	class="hold-transition skin-blue sidebar-mini font-roboto-regular">


	<div class="wrapper">


		<!-- *****************************TO-DO : INCLUDE main_header***************************** -->
		<jsp:include page="/WEB-INF/ecpos/webparts_include/main_header.jsp" />
		<!-- *****************************TO-DO : INCLUDE main_header***************************** -->

		<!-- *****************************TO-DO : INCLUDE menu_drawer***************************** -->
		<jsp:include page="/WEB-INF/ecpos/webparts_include/menu_drawer.jsp" />
		<!-- *****************************TO-DO : INCLUDE menu_drawer***************************** -->


		<!-- Page view wrapper -->
		<!-- <div id="siteloader"></div> -->
		<div ng-view></div>
		<!-- Page view wrapper -->

	</div>

</body>

<script>
	var app = angular.module("myApp", [ "ngRoute", "uiCropper","ngMessages"]);
 	/* app
			.config([
					'$routeProvider',
					'$compileProvider',
					function($routeProvider, $compileProvider) {
						$compileProvider
								.imgSrcSanitizationWhitelist(/^\s*(local|http|https|app|tel|ftp|file|blob|content|ms-appx|x-wmapp0|cdvfile):|data:image\//);
						$routeProvider
								.when(
										"/tableOrder",
										{
											templateUrl : "${pageContext.request.contextPath}/ecpos/views/tableOrder",
											controller : "Show_sales_CTRL"
										})
								.when(
										"/transactionList",
										{
											templateUrl : "${pageContext.request.contextPath}/ecpos/views/transactionList",
										 	controller : "Show_trans_CTRL"
										})
								.when(
										"/itemsManagement",
										{
											templateUrl : "${pageContext.request.contextPath}/ecpos/views/itemsManagement"
										})
								.when(
										"/check/:check_no",
										{
											templateUrl : "${pageContext.request.contextPath}/ecpos/views/check"
										})
								.when(
										"/generateReport",
										{
											templateUrl : "${pageContext.request.contextPath}/ecpos/views/generateReport"
										})
								.when(
										"/configSetting",
										{
											templateUrl : "${pageContext.request.contextPath}/ecpos/views/configSetting"
										})		
								.when(
										"/takeAwayOrder",
										{
											templateUrl : "${pageContext.request.contextPath}/ecpos/views/takeAwayOrder"
										})	
								.when(
										"/payment/:check_no?",
										{
											templateUrl : "${pageContext.request.contextPath}/ecpos/views/payment"
										})	
								.when(
										"/clientQRConnection",
										{
											templateUrl : "${pageContext.request.contextPath}/ecpos/views/clientQRConnection"
										});
					} ]); */
 	
					app
					.config([
							'$routeProvider',
							'$compileProvider',
							function($routeProvider, $compileProvider) {
								$compileProvider
										.imgSrcSanitizationWhitelist(/^\s*(local|http|https|app|tel|ftp|file|blob|content|ms-appx|x-wmapp0|cdvfile):|data:image\//);
								$routeProvider
										.when(
												"/sales",
												{
													templateUrl : "${pageContext.request.contextPath}/ecpos/views/sales",
													controller : "Show_sales_CTRL"
												})
										.when(
												"/trans",
												{
													templateUrl : "${pageContext.request.contextPath}/ecpos/views/trans",
													controller : "Show_trans_CTRL"
												})
										.when(
												"/items",
												{
													templateUrl : "${pageContext.request.contextPath}/ecpos/views/items"
												})
										.when(
												"/checks/:check_no",
												{
													templateUrl : "${pageContext.request.contextPath}/ecpos/views/checks"
												})
										.when(
												"/reports",
												{
													templateUrl : "${pageContext.request.contextPath}/ecpos/views/reports"
												})
										.when(
												"/setting",
												{
													templateUrl : "${pageContext.request.contextPath}/ecpos/views/ecpos_manager_setting"
												})		
										.when(
												"/take_away_order",
												{
													templateUrl : "${pageContext.request.contextPath}/ecpos/views/takeaway"
												})	
										.when(
												"/payment/:check_no",
												{
													templateUrl : "${pageContext.request.contextPath}/ecpos/views/payment"
												})	
										.when(
												"/connection_qr",
												{
													templateUrl : "${pageContext.request.contextPath}/ecpos/views/qr_scan"
												});
							} ]);
 	
 	
 	
 	
 	
</script>

<!-- ***** ANGULAR JS CONTROLLER ***** -->
<jsp:include page="/WEB-INF/ecpos/controller/show_sales_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/show_trans_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/show_checks_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/show_items_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/show_sales_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/printer_configuration.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/ecpos_manager_setting_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/show_take_away_order_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/show_payment_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/show_reports_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/connection_qr_CTRL.jsp" />

</html>
