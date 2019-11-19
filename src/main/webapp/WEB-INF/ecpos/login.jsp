<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String http_message = (String) (request.getAttribute("http_message") == null ? "" : request.getAttribute("http_message"));
%>

<!DOCTYPE html>
<html style="-webkit-user-select: none; -moz-user-select: none; -ms-user-select: none; -khtml-user-select: none; user-select: none;" oncontextmenu="return false">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="icon" href="${pageContext.request.contextPath}/meta/img/ecpos_logo.png" type="image/x-icon">
<title>ManagePay | VERNPOS</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/css_responsive/demo.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/css_responsive/agent_login.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/css_responsive/mygroup.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/bootstrap/dist/css/bootstrap.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.5.0/css/font-awesome.min.css">
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery/dist/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery-ui/jquery-ui.min.js"></script>
<script src='https://www.google.com/recaptcha/api.js'></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/font-awesome.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/jquery/jeffect.css">
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<script src="https://ajax.aspnetcdn.com/ajax/jquery.validate/1.14.0/jquery.validate.js"></script>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jqKeyboard/jqbtk.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jqKeyboard/jqbtk.min.css">

<style>
input[type="text"]:focus, input[type="password"]:focus, input[type="datetime"]:focus,
	input[type="datetime-local"]:focus, input[type="date"]:focus, input[type="month"]:focus,
	input[type="time"]:focus, input[type="week"]:focus, input[type="number"]:focus,
	input[type="email"]:focus, input[type="url"]:focus, input[type="search"]:focus,
	input[type="tel"]:focus, input[type="color"]:focus, input[type="phone"]:focus,
	select[name="gender"]:focus, select[name="country"]:focus, select[name="state"]:focus,
	.uneditable-input:focus {
	border-color: #00FA9A;
	/* box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075) inset, 0 0 8px #00FA9A; */
	outline: 0 none;
}

@font-face {
	font-family: 'robotofontregular';
	src: url('bodydiagnosis/font/Roboto-Regular.ttf');
}

.font-roboto-regular {
	font-family: robotofontregular;
}

.form-signin input[type="text"] {
	margin-bottom: 5px;
	border-bottom-left-radius: 0;
	border-bottom-right-radius: 0;
}

.form-signin input[type="password"] {
	margin-bottom: 10px;
	border-top-left-radius: 0;
	border-top-right-radius: 0;
}

.form-signin .form-control {
	position: relative;
	font-size: 16px;
	font-family: 'Open Sans', Arial, Helvetica, sans-serif;
	height: auto;
	padding: 10px;
	-webkit-box-sizing: border-box;
	-moz-box-sizing: border-box;
	box-sizing: border-box;
}

.test:hover {
  box-shadow: 1px 1px 4px grey;
}

.shutdown {
    border: 8px solid #0cf;
    border-radius: 100px;
    box-shadow: 0 0 10px #0cf, inset 0 0 10px #0cf;
    height: 100px;
    width: 100px;
}

.shutdown .inner {
    border: 5px solid #0cf;
    border-radius: 100px;
    box-shadow: 0 0 10px #0cf, inset 0 0 10px #0cf;
    height: 30px;
    left: 30px;
    position: relative;
    top: 30px;
    width: 30px;
}

.shutdown .bar {
    border-left: 5px solid #0cf;
    box-shadow: 0 0 10px #0cf;
    height: 20px;
    left: 47px;
    position: relative;
    top: -15px;
    width: 0;
}

.shutdown .sub-bar {
    border-left: 11px solid black;
    height: 30px;
    margin-left: 44px;
    margin-top: -20px;
    position: absolute;
    width: 0;
}​
</style>
</head>

<body
	style="background: url(${pageContext.request.contextPath}/img/cover/Cover.jpg) no-repeat; background-size: cover; min-height: 100vh; opacity: 0.9;">
	<div id="loadingPanel" style="min-height: 100vh; display: flex; align-items: center; position: absolute;">
		<div style="min-width: 100vw; display: flex; flex-direction: column; align-items: center;">
			<div class="panel"
				style="background-color: rgba(0, 0, 0, 0.9); text-align: center;">
				<span id="progressText" style="color: white; margin-top: 10px;">Initializing...</span>
				<div class="progress" style="width: 90vh; margin: 10px;">
				  	<div id="progressBar" class="progress-bar progress-bar-info progress-bar-striped active" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
				    	<span class="sr-only">0%</span>
				  	</div>
				</div>
				<p id="progressPercentage" style="color: white; margin-bottom: 10px;">0%</p>
				<button id="retryBtn" type="button" class="btn btn-primary" onclick="beginLoading()">Retry</button>
				<button id="skipBtn" type="button" class="btn btn-primary" onclick="skipLoading()">Skip</button>
			</div>
		</div>
	</div>
	<div id="loginPanel" style="min-height: 100vh; display: flex; align-items: center; position: absolute;">
		<div style="min-width: 100vw; display: flex; flex-direction: column; align-items: center;">
			<div class="panel" style="background-color: rgba(0, 0, 0, 0.9);">
				<div class="panel-heading text-center">
					<img
						src="${pageContext.request.contextPath}/meta/img/ecpos_logo.png"
						style="height: 180px; padding-top: 15px;">
				</div>

				<div class="panel-body text-center">
					<%if (!http_message.equals("")) { %>
					<span style="color: red;">${http_message}</span>
					<%} %>
					<form id="normalForm" action="${pageContext.request.contextPath}/authentication" method="post" accept-charset="UTF-8" role="form" class="form-signin" autocomplete="off">
						<fieldset>
							<label class="login-label" style="color: white;">User Name</label>
							<input class="form-control" id="username" name="username" placeholder="User Name" type="text" required>
							<br>
							<label class="login-label"  style="color: white;">Password</label>
							<input class="form-control" id="password" name="password" placeholder="Password" type="password" required>
							<br> 
							<input class="btn btn-lg btn-block" style="background-color: #00FA9A; color: white;" type="submit" value="Login">
						</fieldset>
					</form>
					<form id="qrForm" action="${pageContext.request.contextPath}/authenticationQR" method="post" accept-charset="UTF-8" role="form" class="form-signin" autocomplete="off">
						<fieldset>
							<input type="hidden" id="qrContent" name="qrContent" value="">
							<input id="showQRLoginBtn" class="btn btn-lg btn-block" style="background-color: #00FA9A; color: white;" type="button" value="Login">
						</fieldset>
					</form>
					<br>
					<button id="switchBtn" type="button" class="btn btn-primary btn-xs">Switch to Form Login</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Scan QR Modal [START] -->
	<div class="modal fade" id="scan_qr_modal" tabindex="-1" role="dialog"
		aria-labelledby="scan_qr_modal" aria-hidden="true"
		data-keyboard="false" data-backdrop="static">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">Login</h4>
				</div>
				<div class="modal-body text-center">
					<form autocomplete="off">
						<div class="row">
							<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
								<div class="form-group">
									<h3>Scan QR to login</h3>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	<!-- Scan QR Modal [END] -->

	<!-- Loading Modal [START] -->
	<div class="modal fade" data-backdrop="static" id="loading_modal"
		role="dialog">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-body">
					<div class="text-center">
						<img style="width: 75%"
							src="${pageContext.request.contextPath}/img/gif/loading.gif"><br>
						<span>Logging In...</span>
					</div>
				</div>
			</div>
		</div>
	</div>
	<button title="Shutdown" id="shutdownBtn" type="button" style="display: inline-block; border-radius: 50px; box-shadow: 0px 0px 2px #888;
  	padding: 0.5em 0.6em;position: absolute; bottom: 10px; left: 5%; margin-left: -50px;">
		<i class="fa fa-power-off" style="font-size:18px"></i>
	</button>
	
	
	
	<!-- Loading Modal [END] -->
	<script type="text/javascript" src="${pageContext.request.contextPath}/jqKeyboard/jqbtk.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jqKeyboard/jqbtk.min.js"></script>
</body>
<script type="text/javascript">
	$(function() {
		$('#username').keyboard();
		$('#password').keyboard();
	});
</script>
<script>
<%if (!http_message.equals("")) { %>
var isLoad = false;
<%} else {%>
var isLoad = true;
<%} %>

var isSyncMenu = false;
var isSyncStore = false;

var formTypeID = ${loginType};
var isFormSwitchable = ${isLoginSwitch};

var isQRLoginExecuted = true;
var loginQRContent = "";

$("div#loadingPanel").hide();
$("div#loginPanel").hide();
$("button#retryBtn").hide();
$("button#skipBtn").hide();

function updateLoginUI() {
	if (formTypeID == 1) {
		$("#switchBtn").html("Switch to QR Login");
		$("form#normalForm").show();
		$("form#qrForm").hide();
	} else if (formTypeID == 2) {
		$("#switchBtn").html("Switch to Form Login");
		$("form#normalForm").hide();
		$("form#qrForm").show();
	}

	if (!isFormSwitchable) {
		$("#switchBtn").hide();
	}
}

$("#switchBtn").click(function() {
	formTypeID = (formTypeID + 1) % 3;
	if (formTypeID == 0) {
		formTypeID += 1;
	}
	
	updateLoginUI();
});


$("#shutdownBtn").click(function() {
	shutdownPC();
});

$("#showQRLoginBtn").click(function() {
	$("#scan_qr_modal").modal("show");
	$("#scan_qr_modal").modal({
	    backdrop: 'static'
	});
	loginQRContent = "";
	isQRLoginExecuted = false;
	
	$(document).off("keydown");
	$(document).keydown(function(e){
		if (!isQRLoginExecuted) {
			if (e.which == 16) {
				return;
			} else if (e.which == 13){
				$("#loading_modal").modal("show");
				
				isQRLoginExecuted = true;
				
				$("input#qrContent").val(loginQRContent);
				$("form#qrForm").submit();
				
				/* $(document).off("keydown"); */
				$("#scan_qr_modal").modal("hide");
			} else if (e.which == 191) {
				loginQRContent += "/";
			} else if (e.which == 186) {
				loginQRContent += ";";
			} else if (e.which == 107) {
				loginQRContent += "+";
			} else if (e.which == 109) {
				loginQRContent += "-";
			} else if (e.which == 189) {
				loginQRContent += "_";
			} else {
				if (e.shiftKey) {
					loginQRContent += String.fromCharCode(e.keyCode || e.which).toUpperCase();
				} else {
					loginQRContent += String.fromCharCode(e.keyCode || e.which).toLowerCase();
				}
			}
		}
	});
});

function syncMenu() {
	updateProgressbar("Checking Menu Update...", 10);
	$.ajax({
	    type: "POST",
	    url: "${pageContext.request.contextPath}/syncMenu",
	    contentType: "application/json; charset=utf-8",
	    dataType: "json",
	    timeout: 30 * 1000,
	    success: function(response) {
	    	if (response != null && response.resultCode != null) {
				if (response.resultCode == "00") {
					isSyncMenu = true;
					setTimeout(function(){
						syncStore();
					}, 500);
				} else if (response.resultCode=='E02' || response.resultCode=='E03') {
					location.reload();
				} else {
					loadFailed(response.resultMessage);
				}
			} else {
				loadFailed("Invalid Server Response.");
			}
	    },
	    failure: function(errMsg) {
	    	loadFailed("System Error. Please Try Again.");
	    },
	    error: function (xhr, ajaxOptions, thrownError) {
	    	loadFailed("System Error. Please Try Again.");
	    }
	});
}

function syncStore() {
	updateProgressbar("Checking Store Update...", 50);
	$.ajax({
	    type: "POST",
	    url: "${pageContext.request.contextPath}/syncStore",
	    contentType: "application/json; charset=utf-8",
	    dataType: "json",
	    timeout: 30 * 1000,
	    success: function(response) {
	    	console.log(response.result);
	    },
	    failure: function(errMsg) {
	    	loadFailed("System Error. Please Try Again.");
	    },
	    error: function (xhr, ajaxOptions, thrownError) {
	    	loadFailed("System Error. Please Try Again.");
	    }
	});
}

function updateProgressbar(message, percentage) {
	$("span#progressText").html(message);
	$("div#progressBar").css("width", percentage + "%");
	$("p#progressPercentage").html(percentage + "%");
}

function updateProgressbarMessage(message) {
	$("span#progressText").html(message);
}

function loadFailed(message) {
	$("div#progressBar").removeClass("progress-bar-info active");
	$("div#progressBar").addClass("progress-bar-danger");
	updateProgressbarMessage(message);
	$("button#retryBtn").show();
	$("button#skipBtn").show();
}

function loadSuccess() {
	$("div#progressBar").removeClass("progress-bar-info progress-bar-danger");
	$("div#progressBar").addClass("progress-bar-success active");
	updateProgressbar("Loading Completed.", 100);
	setTimeout(function(){
		$("div#loginPanel").fadeIn();
		$("div#loadingPanel").fadeOut();
	}, 1500);
}

function skipLoading() {
	$("div#progressBar").removeClass("progress-bar-info progress-bar-danger");
	$("div#progressBar").addClass("progress-bar-success active");
	$("button#retryBtn").hide();
	$("button#skipBtn").hide();
	updateProgressbar("Loading Skipped.", 100);
	setTimeout(function(){
		$("div#loginPanel").fadeIn();
		$("div#loadingPanel").fadeOut();
	}, 1500);
}

function beginLoading() {
	$("div#progressBar").addClass("progress-bar-info active");
	$("div#progressBar").removeClass("progress-bar-danger");
	$("button#retryBtn").hide();
	$("button#skipBtn").hide();
	updateProgressbar("Loading...", 0);
	setTimeout(function(){
		if (!isSyncMenu) {
			syncMenu();
		} else if (!isSyncStore) {
			syncStore();
		} else {
			loadSuccess();
		}
	}, 500);
}

function shutdownPC() {
	$.ajax({
	    type: "POST",
	    url: "${pageContext.request.contextPath}/rc/configuration/shutdown_device",
	    contentType: "application/json; charset=utf-8",
	    dataType: "json",
	  /*   timeout: 30 * 1000, */
	    success: function(response) {
	    	console.log("response " + response.result);
	    },
	    failure: function(errMsg) {
	    	console.log("error");
	    }
	});
}

$(document).ready(function() {
	updateLoginUI();
	console.log(isLoad);
	if (isLoad) {
		$("div#loadingPanel").show();
		beginLoading();
	} else {
		$("div#loginPanel").show();
		$("div#loadingPanel").hide();
	}
});
</script>
</html>