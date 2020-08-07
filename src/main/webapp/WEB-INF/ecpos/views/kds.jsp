<%@ page
	import="mpay.ecpos_manager.general.utility.UserAuthenticationModel"%>

<%
	UserAuthenticationModel user = (UserAuthenticationModel) session.getAttribute("session_user");
%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

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
<title page-title class="font-roboto-regular">VernPOS KDS</title>

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

<!-- *****OTHERS JS***** -->
<script src="${pageContext.request.contextPath}/qrcode/qrcode.min.js"></script>
<script
	src="${pageContext.request.contextPath}/ui-cropper/js/ui-cropper.js"></script>
<script src="${pageContext.request.contextPath}/sweetAlert/sweetalert2.js"></script>
</head>

<!-- Body -->
<!-- appCtrl controller with serveral data used in theme on diferent view -->
<body ng-app="kdsApp"
	class="hold-transition skin-green sidebar-mini font-roboto-regular">
	<div ng-controller="kds_CTRL" class="wrapper"
		style="background-color: #0000000d; height: 100%; width: 100%; overflow-y: hidden;">
		<!-- *****************************TO-DO : INCLUDE main_header***************************** -->
		<jsp:include page="/WEB-INF/ecpos/webparts_include/main_header.jsp" />
		<!-- *****************************TO-DO : INCLUDE main_header***************************** -->			
		<div ng-init="initOrder();" id="contentId"
			style="height: calc(100vh - 50px); padding: 15px; margin-right: auto; margin-left: auto; padding-left: 15px; padding-right: 15px;">
			
			<!-- start kds order list -->
			<div ng-bind-html="contentKds"></div>
			<!-- end kds order list -->

			<!-- Start kds setting window -->
			<div class="modal fade" id="kds_setting">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">
								<i class="fa fa-gear"></i> Setting
							</h4>
						</div>
						<form role="form" ng-submit="saveKdsSetting()">
							<div class="box-body">
								<input type="hidden" ng-model="kdsConfig.id">
								<div class="form-group">
									<p>
										Time until warning color <b class="badge bg-yellow">&nbsp;</b>
										:
									</p>
									<div class="row">
										<div class="col-xs-5">
											<input type="text" class="form-control" ng-model="kdsConfig.timeWarning">
										</div>
										<p>minute(s)</p>
									</div>
								</div>
								<div class="form-group">
									<p>
										Time until late color <b class="badge bg-red">&nbsp;</b> :
									</p>
									<div class="row">
										<div class="col-xs-5">
											<input type="text" class="form-control" ng-model="kdsConfig.timeLate">
										</div>
										<p>minute(s)</p>
									</div>
								</div>
							</div>
							<div class="modal-footer">
							<button type="submit" class="btn btn-primary">Save
								changes</button>
						</div>
						</form>
						
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- End kds setting window -->



		</div>
	</div>
</body>
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/dist/js/pages/dashboard.js"></script>
<!-- iCheck 1.0.1 -->
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/plugins/iCheck/icheck.min.js"></script>
<!-- ***** ANGULAR JS CONTROLLER ***** -->
<jsp:include page="/WEB-INF/ecpos/controller/kds_CTRL.jsp" />
</html>
