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
<script src="${pageContext.request.contextPath}/angular-1.7.5/js/angular.min.js"></script>

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

<body ng-app="activationApp" ng-controller="ActivationController" style="background: url(${pageContext.request.contextPath}/img/cover/Cover.jpg) no-repeat; background-size: cover; min-height: 100vh; opacity: 0.9;">
 	<div style="min-height: 100vh; display: flex; align-items: center;">
		<div class="col-md-4 col-md-offset-4">
			<div class="panel" style="background-color: rgba(0, 0, 0, 0.9);">
				<div class="panel-heading text-center">
					<img src="${pageContext.request.contextPath}/meta/img/ecpos_logo.png" style="height:200px; padding-top: 15px;">
				</div>

				<div class="panel-body">
					<%if (!http_message.equals("")) { %>
					<span style="color: red;">${http_message}</span>
					<%} %>
					<form role="form" ng-submit="submitActivation()" class="form-signin" autocomplete="off">
						<fieldset>
							<label class="login-label" style="color: white;">Brand ID</label>
							<input class="form-control" name="brandId" placeholder="Brand ID" type="text" ng-model="activationData.brand_id" required> 
							<br>
							<label class="login-label"  style="color: white;">Activation ID</label>
							<input class="form-control" name="activationId" placeholder="Activation ID" type="text" ng-model="activationData.act_id" required>
							<br>
							<label class="login-label" style="color: white;">Activation Key</label>
							<input class="form-control" name="activationKey" placeholder="Activation Key" type="password" ng-model="activationData.key" required> 
							<br> 
							<input class="btn btn-lg btn-block" style="background-color: #00FA9A; color: white;" type="submit" value="Activate">
						</fieldset>
					</form>
				</div>
			</div>
		</div>
	</div>
	
	<!-- Loading Modal [START] -->
	<div class="modal fade" data-backdrop="static" id="loading_modal" role="dialog">
		<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-body">
				<div class="text-center">
					<img style="width:75%" src="${pageContext.request.contextPath}/img/gif/loading.gif"><br>
						<span>Activating...</span>
				</div>
			</div>
		</div>
		</div>
	</div>
	<!-- Loading Modal [END] -->

	<div class="modal fade" id="modal-dialog" tabindex="-1" role="dialog"
		aria-hidden="true">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						&times;
					</button>
					<h4 class="modal-title"
						ng-show="dialogData.title != ''">{{dialogData.title}}</h4>
				</div>
				<div class="modal-body"
					ng-show="dialogData.message != ''">{{dialogData.message}}</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary btn-main"
						ng-click="dialogData.button1.fn()" ng-show="dialogData.isButton1">{{dialogData.button1.name}}</button>
					<button type="button" class="btn btn-primary btn-main"
						ng-click="dialogData.button2.fn()" ng-show="dialogData.isButton2">{{dialogData.button2.name}}</button>
				</div>
			</div>
			</div>
	</div>
</body>
<script>
	var activationApp = angular.module('activationApp', []);

	activationApp.controller('ActivationController', function($scope, $http) {
		$scope.activationData = {};

		$scope.submitActivation = function() {
			$('#loading_modal').modal('show');
			$http({
				method : 'POST',
				headers : {
					'Content-Type' : 'application/json'
				},
				params : {
					brandId : $scope.activationData.brand_id,
					activationId : $scope.activationData.act_id,
					activationKey : $scope.activationData.key
				},
				url : '${pageContext.request.contextPath}/activation'
			}).then(
					function(response) {
						if (response != null && response.data != null
								&& response.data.resultCode != null) {
							if (response.data.resultCode == "00") {						
								$scope.activateSuccess();
							} else {
								$scope.activateFailed(response.data.resultMessage);
							}
						} else {
							$scope.activateFailed("Invalid server response!");
						}
					}, function(error) {
						$scope.activateFailed("Unable to connect to server!");
					});
		}

		$scope.activateFailed = function(message) {
			$('#loading_modal').modal('hide');
			var dialogOption = {};
			dialogOption.title = "Activation Failed!";
			dialogOption.message = message;
			dialogOption.button1 = {
					name: "OK",
					fn: function() {
						$("div#modal-dialog").modal("hide");
					}
			}
			$scope.displayDialog(dialogOption);
		}

		$scope.activateSuccess = function() {
			$('#loading_modal').modal('hide');
			var dialogOption = {};
			dialogOption.title = "Activation Success!";
			dialogOption.message = "";
			dialogOption.button1 = {
					name: "OK",
					fn: function() {
						$("div#modal-dialog").modal("hide");
						location.reload();
					}
			}
			$scope.displayDialog(dialogOption);
		}
		
		$scope.displayDialog = function(dialogOption) {
			$scope.dialogData = {};
			$scope.dialogData.title = dialogOption.title;
			$scope.dialogData.message = dialogOption.message;
			$scope.dialogData.button1 = dialogOption.button1;
			$scope.dialogData.button2 = dialogOption.button2;
			$scope.dialogData.isButton1 = typeof $scope.dialogData.button1 !== "undefined";
			$scope.dialogData.isButton2 = typeof $scope.dialogData.button2 !== "undefined";
			$('#modal-dialog').modal({backdrop: 'static', keyboard: false});
		}
	});
</script>
</html>