<!DOCTYPE html>
<html>
<head>
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
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-sm-2" ng-repeat="existing_check in checks" style="padding-left: 2px; padding-right: 2px;">
							<div data-dismiss='modal' ng-click="redirect_to_check_detail(existing_check)">
								<div class="panel panel-default text-center">
									<div class="panel-heading" style="color: #333333e0; background-color: #00ff7f">
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
							<div data-dismiss='modal' ng-click="create_new_check()">
								<div class="panel panel-default text-center">
									<div class="panel-heading">
										<h3 class="panel-title">NEW</h3>
									</div>
									<div class="panel-body">
										<div class="panel-body center-block" style="color: grey; font-weight: bold; font-size: medium;">
											<i class="fa fa-plus" aria-hidden="true"></i>
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