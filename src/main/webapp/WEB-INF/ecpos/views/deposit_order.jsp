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
</style>
</head>

<body>
	<div ng-controller="deposit_order_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-top: 8px;">
				
					<div id="depositOrderCarousel" class="carousel" data-interval="false">
						<div class="carousel-inner">
							<div class="item active" id="checkList">
								<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
									<div class="col-sm-2" ng-repeat="existing_check in checks" style="padding-left: 2px; padding-right: 2px;">
										<div data-dismiss='modal' ng-click="redirect_to_check_detail(existing_check)">
											<div class="panel panel-default text-center">
												<div class="panel-heading" style="color: #333333e0; background-color: #00FA9A">
													<h3 class="panel-title">CHECK NO</h3>
												</div>
												<div class="panel-body">
													<div class="panel-body center-block" style="color: grey; font-weight: bold; font-size: medium;">
														{{existing_check}}
													</div>
												</div>
											</div>
										</div>
									</div>
			
									<div class="col-sm-2" style="padding-left: 2px; padding-right: 2px;">
										<div data-dismiss='modal'>
											<div class="panel panel-default text-center">
												<div class="panel-heading">
													<h3 class="panel-title">NEW</h3>
												</div>
												<div class="panel-body" data-target="#depositOrderCarousel" data-slide="next">
													<div class="panel-body center-block" style="color: grey; font-weight: bold; font-size: medium;">
														<i class="fa fa-plus" aria-hidden="true"></i>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							
							<div class="item" id="newCheck">
								<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
									<div class="col-sm-12" style="padding-right: 2px; padding-left: 2px;">
										<div class="well" style="height: 89vh; overflow-y: auto; background-color: white; margin-bottom: 0px; padding-top: 10px; padding-bottom: 10px; padding-left: 15px; padding-right: 15px;">
											<div class="row" style="text-align: center">
												<div class="col-sm-1" style="font-size: large">
													<a data-target="#depositOrderCarousel" data-slide="prev">
														<i class="fa fa-arrow-left" style="color: black;"></i>
													</a>
												</div>
												<div class="col-sm-10">
													<font size="4">DEPOSIT ORDER</font>
												</div>
												<div class="col-sm-1"></div>
											</div>
											<div class="row">
												<div class="col-sm-2 form-group"></div>
												<div class="col-sm-8 form-group">
													<label style="font-size: medium;">Customer name:</label> 
													<input type="text" class="form-control" id ="customerName" required/>
													<br>
													<div id="keyboard"></div>
													<br>
													<div style="text-align: center;">
														<button class="btn btn-info" ng-click="create_new_check()">Create New Check</button>
													</div>
												</div>
												<div class="col-sm-2 form-group"></div>
											</div>
										</div>
									</div>
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