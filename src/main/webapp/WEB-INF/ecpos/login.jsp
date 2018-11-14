<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="icon"
	href="${pageContext.request.contextPath}/meta/img/ecpos_logo.png"
	type="image/x-icon">
<title>ECPOS Manager - Sign In</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/meta/css_responsive/demo.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/meta/css_responsive/agent_login.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/meta/css_responsive/mygroup.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/bootstrap/dist/css/bootstrap.min.css">
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery/dist/jquery.min.js"></script>
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/jquery-ui/jquery-ui.min.js"></script>
<script src='https://www.google.com/recaptcha/api.js'></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/meta/font-awesome.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/meta/jquery/jeffect.css">
<script
	src="${pageContext.request.contextPath}/adminLTE-2.4.5/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<script
	src="https://ajax.aspnetcdn.com/ajax/jquery.validate/1.14.0/jquery.validate.js"></script>

<script type="text/javascript">
var recaptchaCallBack = function(response){
	if(response != null){
		var robot = false;
	}else{
		var robot = true;
	}
	//alert(robot);
	document.getElementById("recap").value = robot;
	
}

$(document).ready(function() {
	$('#loading_modal').hide();
	$('#msform').on('submit', function(e){
		$('#btnLogin').prop('disabled', true);
        e.preventDefault();
        
        //$(this).attr('action', "/member/authentication");
        $('#loading_modal').show();
        this.submit();
        /* sendSignInReq(); */
    });
})

function sendSignInReq() {
	setTimeout(function(){
		$('#loading_modal').hide();
		$(location).attr('href', '/member_login');
	}, 2500);
}

</script>
<style>
	input[type="text"]:focus, input[type="password"]:focus, input[type="datetime"]:focus,
		input[type="datetime-local"]:focus, input[type="date"]:focus, input[type="month"]:focus,
		input[type="time"]:focus, input[type="week"]:focus, input[type="number"]:focus,
		input[type="email"]:focus, input[type="url"]:focus, input[type="search"]:focus,
		input[type="tel"]:focus, input[type="color"]:focus, input[type="phone"]:focus,
		select[name="gender"]:focus, select[name="country"]:focus, select[name="state"]:focus,
		.uneditable-input:focus {
		border-color: rgba(179, 17, 44, 0.8);
		box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075) inset, 0 0 8px
			rgba(179, 17, 44, 0.6);
		outline: 0 none;
	}

	@font-face {
	font-family: 'robotofontregular';
	src:
		url('bodydiagnosis/font/Roboto-Regular.ttf');
	}
	
	.font-roboto-regular {
		font-family: robotofontregular;
	}
		
</style>
</head>
<body class="font-roboto-regular">
	<header style="background-color:green;">
		<h1>
			<img
				src="${pageContext.request.contextPath}/meta/img/ecpos_logo.png"
				width="200" />
		</h1>
	</header>
	
	<form id="msform" name="msform" method="post"
		action="${pageContext.request.contextPath}/ecpos/authentication">
		<div id="loading_modal" class="outer-div modal-content" tabindex="-1"
			role="dialog">
			<div class="inner-div">
				<div style="display: inline-block" class="loader"></div>
				</br>
				<div class="modal-body text-center">
					<p>Signing in, please wait...</p>
				</div>
			</div>
		</div>
		<fieldset id="account-creation">
			<div class="form-title-row">
				<h1>ECPOS</h1>
			</div>

		
				<label for="member_username" class="cols-sm-2 control-label">Username</label>
			<input type="text" id="username" name="username"
				placeholder="Enter username" required /> 
					
				<label for="password" class="control-label">Password</label>
				<input type="password" id="password" name="password" placeholder="Enter Password"
				required />		
				<span style="color: red;">${http_message}</span>
				<br>
				<input type="submit" id="btnLogin" name="Login" class="action-button"
				value="Login">
		</fieldset>
	</form>
</body>
</html>