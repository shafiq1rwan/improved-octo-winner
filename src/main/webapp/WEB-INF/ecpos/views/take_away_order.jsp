<%@ page import="mpay.ecpos_manager.general.utility.UserAuthenticationModel"%>

<%
	UserAuthenticationModel user = (UserAuthenticationModel) session.getAttribute("session_user");
%>

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.2/css/all.css" integrity="sha384-oS3vJWv+0UjzBfQzYUhtDYW+Pj2yciDJxpsK1OYPAYjqT085Qq/1cq5FLXAZQ7Ay" crossorigin="anonymous">
<link rel="stylesheet" href="${pageContext.request.contextPath}/fontawesome-pro-5.6.1/css/all.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/keyboard/css/jkeyboard.css">
<script src="${pageContext.request.contextPath}/keyboard/js/jkeyboard.js"></script>
<script>
	$('#keyboard').hide();
	
    $('#keyboard').jkeyboard({
        layout: "english",
        input: $('#customerName')
    });
    
    setTimeout(function() { 
    	$('#keyboard').show();
    }, 100);
</script>
<style>
.sectioncalibrator {
	height: calc(100vh - 50px);
	overflow-y: scroll;
}

@media only screen and (max-width:600px) {
	.sectioncalibrator {
		height: calc(100vh - 100px);
		overflow-y: scroll;
	}
}

hr {
	margin-top: 5px;
	margin-bottom: 5px;
}
</style>
</head>

<body>
	<div ng-controller="take_away_order_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-top: 8px; padding-bottom: 8px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-sm-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="well box box-primary" style="height: 89vh; overflow-y: auto; background-color: white; margin-bottom: 0px; padding-top: 10px; padding-bottom: 10px; padding-left: 15px; padding-right: 15px;">
								<div class="row" style="text-align: center">
									<div class="col-sm-12">
										<font size="4">TAKE AWAY ORDER</font>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-2 form-group"></div>
									<div class="col-sm-8 form-group">
										<%if (user.isTakeAwayFlag() == true) {%>
										<label style="font-size: medium;">Customer name:</label> 
										<input type="text" class="form-control" id ="customerName" required/>
										<br>
										<div id="keyboard"></div>
										<br>
										<div style="text-align: center;">
											<button class="btn btn-info" ng-click="create_new_check()">Create New Check</button>
										</div>
										<%} else {%>
										<br>
										<div style="text-align: center;">
											<button class="btn btn-info btn-lg" ng-click="create_new_check()">Create New Check</button>
										</div>
										<%}%>
									</div>
									<div class="col-sm-2 form-group"></div>
								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
		</div>
	</div>
</body>
</html>