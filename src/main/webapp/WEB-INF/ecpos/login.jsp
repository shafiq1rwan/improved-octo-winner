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
<title>ManagePay | ECPOS</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/css_responsive/demo.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/css_responsive/agent_login.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/css_responsive/mygroup.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/bootstrap/dist/css/bootstrap.min.css">
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery/dist/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery-ui/jquery-ui.min.js"></script>
<script src='https://www.google.com/recaptcha/api.js'></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/font-awesome.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/meta/jquery/jeffect.css">
<script src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<script src="https://ajax.aspnetcdn.com/ajax/jquery.validate/1.14.0/jquery.validate.js"></script>

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
						style="height: 200px; padding-top: 15px;">
				</div>

				<div class="panel-body text-center">
					<%if (!http_message.equals("")) { %>
					<span style="color: red;">${http_message}</span>
					<%} %>
					<form id="normalForm" action="${pageContext.request.contextPath}/authentication" method="post" accept-charset="UTF-8" role="form" class="form-signin" autocomplete="off">
						<fieldset>
							<label class="login-label" style="color: white;">User Name</label>
							<input class="form-control" name="username" placeholder="User Name" type="text" required> 
							<br>
							<label class="login-label"  style="color: white;">Password</label>
							<input class="form-control" name="password" placeholder="Password" type="password" required>
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
</body>

<script>
var isSyncMenu = false;
var isSyncStore = false;

var formTypeID = ${loginType};
var isFormSwitchable = ${isLoginSwitch};

var isQRLoginExecuted = true;
var loginQRContent = "";

$("div#loginPanel").hide();
$("button#retryBtn").hide();
$("button#skipBtn").hide();

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

$("#switchBtn").click(function() {
	formTypeID = (formTypeID + 1) % 3;
	if (formTypeID == 0) {
		formTypeID += 1;
	}
	
	if (formTypeID == 1) {
		$("#switchBtn").html("Switch to QR Login");
		$("form#normalForm").show();
		$("form#qrForm").hide();
	} else if (formTypeID == 2) {
		$("#switchBtn").html("Switch to Form Login");
		$("form#normalForm").hide();
		$("form#qrForm").show();
	}
});

$("#showQRLoginBtn").click(function() {
	$("#scan_qr_modal").modal({
	    backdrop: 'static'
	});
	$("#scan_qr_modal").modal("show");
	loginQRContent = "";
	isQRLoginExecuted = false;
	
	$(document).off("keydown");
	$(document).keydown(function(e){
		if (!isQRLoginExecuted) {
			if (e.which == 16) {
				return;
			} else if (e.which == 13){
				isQRLoginExecuted = true;
				
				$("input#qrContent").val(loginQRContent);
				$("form#qrForm").submit();
				
				$(document).off("keydown");
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
	    	if (response != null && response.resultCode != null) {
				if (response.resultCode == "00") {
					isSyncStore = true;
					loadSuccess();
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

function updateProgressbar(message, percentage) {
	$("span#progressText").html(message);
	$("div#progressBar").css("width", percentage + "%");
	$("p#progressPercentage").html(percentage + "%");
}

function loadFailed(message) {
	updateProgressbar(message, 0);
	$("button#retryBtn").show();
	$("button#skipBtn").show();
}

function loadSuccess() {
	updateProgressbar("Loading Completed.", 100);
	setTimeout(function(){
		$("div#loginPanel").fadeIn();
		$("div#loadingPanel").fadeOut();
	}, 1500);
}

function skipLoading() {
	updateProgressbar("Loading Skipped.", 100);
	setTimeout(function(){
		$("div#loginPanel").fadeIn();
		$("div#loadingPanel").fadeOut();
	}, 1500);
}

function beginLoading() {
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

$(document).ready(function() {
	beginLoading();
});
</script>
</html>