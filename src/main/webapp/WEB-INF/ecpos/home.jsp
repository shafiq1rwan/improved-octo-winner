<%@ page import="mpay.ecpos_manager.general.utility.UserAuthenticationModel"%>

<%
	UserAuthenticationModel user = (UserAuthenticationModel) session.getAttribute("session_user");
%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html style="-webkit-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none;" oncontextmenu="return false">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

<!-- Page title set in pageTitle directive -->
<title page-title class="font-roboto-regular">ECPOS Manager</title>

<!-- ****BASE FOLDER DEFINE**** -->
<base href="/">

<link rel="shortcut icon" type="image/ico" href="${pageContext.request.contextPath}/meta/img/ecpos_logo.png" />

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
<link rel="stylesheet" href="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/bootstrap/dist/css/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.5.0/css/font-awesome.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/fontawesome-pro-5.6.1/css/all.css">
<!-- Ionicons -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css">
<!-- Theme style -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/adminLTE-2.4.5/dist/css/AdminLTE.min.css">
<!-- AdminLTE Skins. Choose a skin from the css/skins folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/adminLTE-2.4.5/dist/css/skins/_all-skins.min.css">
<!-- iCheck -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/iCheck/flat/blue.css">
<!-- jvectormap -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/jvectormap/jquery-jvectormap-1.2.2.css">
<!-- Date Picker -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/datepicker/datepicker3.css">
<!-- Daterange picker -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/daterangepicker/daterangepicker.css">
<!-- bootstrap wysihtml5 - text editor -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css">
<!-- DataTables -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/datatables/dataTables.bootstrap.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/datatables/extensions/Select/css/select.bootstrap.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/datatables/extensions/Buttons/css/buttons.dataTables.min.css">
<!-- ui-cropper -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/ui-cropper/css/ui-cropper.css">
<!-- loadingbar -->
<%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/css/loadingbar.css"> --%>

<!-- ****ADMIN LTE TEMPLATE UNIVERSAL JQUERY**** -->
<!-- jQuery 2.2.3 -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/jQuery/jquery-2.2.3.min.js"></script>
<script src="${pageContext.request.contextPath}/js/loadImg.js"></script>
<!-- jQuery UI 1.11.4 -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery-ui/jquery-ui.min.js"></script>
<!-- Resolve conflict in jQuery UI tooltip with Bootstrap tooltip -->
<script>$.widget.bridge('uibutton', $.ui.button);</script>

<!-- ****ADMIN LTE TEMPLATE UNIVERSAL IMPORT**** -->
<!-- Bootstrap 3.3.7 -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<!-- Sparkline -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery-sparkline/dist/jquery.sparkline.min.js"></script>
<!-- jvectormap -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/jvectormap/jquery-jvectormap-world-mill-en.js"></script>
<!-- jQuery Knob Chart -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery-knob/dist/jquery.knob.min.js"></script>
<!-- daterangepicker -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/moment/min/moment.min.js"></script>
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/daterangepicker/daterangepicker.js"></script>
<!-- datepicker -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/datepicker/bootstrap-datepicker.js"></script>
<!-- Bootstrap WYSIHTML5 -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min.js"></script>
<!-- Slimscroll -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- FastClick -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/fastclick/lib/fastclick.js"></script>
<!-- AdminLTE App -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/dist/js/adminlte.min.js"></script>
<!-- AdminLTE for demo purposes -->
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/dist/js/demo.js"></script>
<!-- DataTables -->
<script src="${pageContext.request.contextPath}/datatables/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/datatables/dataTables.bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/datatables/extensions/Select/js/dataTables.select.min.js"></script>
<script src="${pageContext.request.contextPath}/datatables/extensions/Buttons/js/dataTables.buttons.min.js"></script>
<script src="${pageContext.request.contextPath}/datatables/extensions/Buttons/js/buttons.html5.min.js"></script>

<!-- *****ANGULAR JS****** -->
<script src="${pageContext.request.contextPath}/angular-1.7.5/js/angular.min.js"></script>
<script src="${pageContext.request.contextPath}/angular-1.7.5/js/angular-route.min.js"></script>
<script src="${pageContext.request.contextPath}/angular-1.7.5/js/angular-messages.min.js"></script>

<!-- *****OTHERS JS***** -->
<script src="${pageContext.request.contextPath}/qrcode/qrcode.min.js"></script>
<script src="${pageContext.request.contextPath}/ui-cropper/js/ui-cropper.js"></script>
</head>

<!-- Body -->
<!-- appCtrl controller with serveral data used in theme on diferent view -->
<body ng-app="myApp" class="hold-transition skin-green sidebar-mini font-roboto-regular">
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
	var app = angular.module("myApp", [ "ngRoute", "uiCropper", "ngMessages" ]);
	app.config(['$routeProvider', '$compileProvider', function($routeProvider, $compileProvider) {
		$compileProvider.imgSrcSanitizationWhitelist(/^\s*(local|http|https|app|tel|ftp|file|blob|content|ms-appx|x-wmapp0|cdvfile):|data:image\//);
		$routeProvider
		.when("/connection_qr",
			{
				templateUrl : "${pageContext.request.contextPath}/ecpos/views/qr_scan"
			})
		
		
		.when("/",
			{
			<%if (user.getStoreType() == 2) {%>
				templateUrl : "${pageContext.request.contextPath}/views/table_order",
				controller : "table_order_CTRL"
			<%} else {%>
				templateUrl : "${pageContext.request.contextPath}/views/take_away_order",
				controller : "take_away_order_CTRL"
			<%}%>
			})
		.when("/table_order",
			{
				templateUrl : "${pageContext.request.contextPath}/views/table_order",
				controller : "table_order_CTRL"
			})
		.when("/deposit_order",
			{
				templateUrl : "${pageContext.request.contextPath}/views/deposit_order",
				controller : "deposit_order_CTRL"
			})
		.when("/check/:orderType/:checkNo/:tableNo",
			{
				templateUrl : "${pageContext.request.contextPath}/views/check",
				controller : "check_CTRL"
			})
		.when("/take_away_order",
			{
				templateUrl : "${pageContext.request.contextPath}/views/take_away_order",
				controller : "take_away_order_CTRL"
			})
		.when("/items_listing",
			{
				templateUrl : "${pageContext.request.contextPath}/views/items_listing",
				controller : "items_listing_CTRL"
			})
		.when("/checks_listing",
			{
				templateUrl : "${pageContext.request.contextPath}/views/checks_listing",
				controller : "checks_listing_CTRL"
			})
		.when("/transactions_listing",
			{
				templateUrl : "${pageContext.request.contextPath}/views/transactions_listing",
				controller : "transactions_listing_CTRL"
			})
		.when("/reports",
			{
				templateUrl : "${pageContext.request.contextPath}/views/reports",
				controller : "reports_CTRL"
			})
		.when("/settings",
			{
				templateUrl : "${pageContext.request.contextPath}/views/settings",
				controller : "settings_CTRL"
			});
	}]);
</script>

<!-- ***** ANGULAR JS CONTROLLER ***** -->
<jsp:include page="/WEB-INF/ecpos/controller/table_order_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/check_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/menu_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/payment_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/take_away_order_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/deposit_order_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/items_listing_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/checks_listing_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/transactions_listing_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/reports_CTRL.jsp" />
<jsp:include page="/WEB-INF/ecpos/controller/settings_CTRL.jsp" />

</html>
