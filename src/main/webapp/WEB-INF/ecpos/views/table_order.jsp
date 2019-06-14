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
	<div ng-controller="table_order_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-top: 8px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div ng-repeat="table in manager_tablelist" class="col-sm-2" style="padding-left: 2px; padding-right: 2px;">
							<div ng-click="get_table_checklist(table.table_number)" ng-model="table.table_number">
								<div class="panel text-center" style="margin: 5px;">
									<div class="panel-heading" ng-style="{'background-color':display_table_check_no(table.total_check)}">
										<h3 class="panel-title" ng-style="{'color':display_table_check_no_title_color(table.total_check)}">CHECKS ({{table.total_check}})</h3>
									</div>
									<div class="panel-body" style="color: grey; font-weight: bold; font-size: small;">
										TABLE<br>{{table.table_name}}
									</div>
								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
		
			<!-- MODAL START -->
			<div class="modal fade" id="modal_table_check_list" role="dialog" data-keyboard="false" data-backdrop="static">
				<div class="modal-dialog">
					<div class="modal-content">
						<!-- <div class="modal-header">
							<h4 class="modal-title text-center">
								<i class="fa fa-info-circle"></i> <b>INFORMATION</b>
							</h4>
						</div> -->
						<div class="modal-body text-center">
							<div id="select_trxtypeModal_tblno" style="color: green; font-weight: bold; font-size: large;"></div>
						</div>
						<div class="modal-footer" style="padding: 30px;">
							<div class="row">
								<div class="col-sm-3 col-lg-4" ng-repeat="existing_check in checks" style="padding-left: 10px; padding-right: 10px;">
									<div data-dismiss='modal'>
										<div class="panel panel-default text-center">
											<div class="panel-heading" style="color: #333333e0; background-color: #00FA9A">
												<h3 class="panel-title">CHECK NO</h3>
											</div>
											<div class="panel-body" ng-click="redirect_to_check_detail(existing_check)">
												<div class="panel-body center-block" style="color: grey; font-weight: bold; font-size: medium;">
													{{existing_check}}
												</div>
											</div>
										</div>
									</div>
								</div>
	
								<div class="col-sm-3 col-lg-4" style="padding-left: 10px; padding-right: 10px;">
									<div data-dismiss='modal'>
										<div class="panel panel-default text-center">
											<div class="panel-heading">
												<h3 class="panel-title">NEW</h3>
											</div>
											<div class="panel-body" ng-click="create_new_check()">
												<div class="panel-body center-block" style="color: grey; font-weight: bold; font-size: medium;">
													<i class="fa fa-plus" aria-hidden="true"></i>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="row">
								<button class="btn btn-block btn-google center-block" style="width: 97%; background-color: #ff0000d0;" data-dismiss="modal">CANCEL</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>