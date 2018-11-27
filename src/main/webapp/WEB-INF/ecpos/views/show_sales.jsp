<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>

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

<body>
	<div>
		<div class="content-wrapper" style="font-size: 0.9em;">

			<section class="content sectioncalibrator">

				<div class="active item" data-slide-number="0">
					<!-- 		<div class="row container-fluid">
											<nav class="navbar navbar-inverse">
											<div class="container-fluid">
												<div class="navbar-header">
													<a class="navbar-brand" href="#">SALES TYPE</a>
												</div>
												<ul class="nav navbar-nav">
													<li class="active"><a data-toggle="pill" href="#"
														onclick="show_tableselection('tablelist')">[ <i
															class="fa fa-cutlery" aria-hidden="true"></i> ] TABLE
															ORDER
													</a></li>
													<li><a data-toggle="pill" href="#"
														onclick="show_Viewchecklist('tablelist')">[ <i
															class="fa fa-tablet" aria-hidden="true"></i> ] KIOSK
															ORDER
													</a></li>
														<li><a data-toggle="pill" href="#"
														onclick="">[ <i
															class="fa fa-parachute-box" aria-hidden="true"></i> ] TAKE 
															AWAY
													</a></li>
																								
												</ul>
											</div>
											</nav>

											<hr class="divider">
										</div> -->
					<!-- 								<div class="row container-fluid">
											<hr class="divider">
										</div> -->
					<div class="row container-fluid" style="overflow-y:auto;padding-right:2px;padding-left:2px;">

						<div ng-repeat="table in manager_tablelist" class="col-lg-2 col-md-3 col-sm-4 col-xs-6" style="padding-left:2px;padding-right:2px;">
							<a ng-click="get_table_checklist(table.table)"
								ng-model="table.table">
								<div class="panel text-center">
									<div class="panel-heading"
										ng-style="{'background-color':display_table_check_no(table.check)}">
										<h3 class="panel-title"
											ng-style="{'color':display_table_check_no_title_color(table.check)}">Checks
											({{table.check}})</h3>
									</div>
									<div class="panel-body"
										style="color: grey; font-weight: bold; font-size: small;">
										TABLE<br> {{table.table}}
									</div>
								</div>
							</a>
						</div>





					</div>
				</div>

			</section>

		</div>

		<!-- MODAL START -->
		<div class="row">
			<div class="modal fade" id="modal_table_check_list" role="dialog"
				data-keyboard="false" data-backdrop="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<h4 class="modal-title text-center content-format">
								<i class="fa fa-info"></i> <b>INFORMATION</b>
							</h4>
						</div>
						<div class="modal-body text-center">
							<div id="select_trxtypeModal_tblno"
								style="color: green; font-weight: bold; font-size: large;"></div>
							<h4>Do you want to seat your customer at this table?</h4>
						</div>
						<div class="modal-footer center-block text-center">
							<div class="form-group">
								<div class="col-md-12">
									<div class="row">
										<div class="col-md-12">
											<div class="panel-body">

												<div class="row">
													<div class="col-lg-4 col-md-4 col-sm-6 col-xs-12"
														ng-repeat="existing_check in check_list">
														<a data-dismiss='modal'
															ng-click="redirect_to_check_detail(existing_check)">
															<div class="panel panel-primary text-center">
																<div class="panel-heading">
																	<h3 class="panel-title">CHECK NUM</h3>
																</div>
																<div class="panel-body">
																	<div class="panel-body center-block"
																		style="color: grey; font-weight: bold; font-size: small;">
																		{{existing_check}}</div>
																</div>
															</div>
														</a>
													</div>


													<div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
														<a data-dismiss='modal' ng-click="create_new_check()">
															<div class="panel panel-default text-center">
																<div class="panel-heading">
																	<h3 class="panel-title">NEW</h3>
																</div>
																<div class="panel-body">
																	<div class="panel-body center-block"
																		style="mcolor: grey; font-weight: bold; font-size: small;">
																		<i class="fa fa-plus" aria-hidden="true"></i>
																	</div>
																</div>
															</div>
														</a>
													</div>


												</div>
												<br>

												<div class="row">
													<a class="btn btn-danger center-block" data-dismiss="modal">CANCEL</a>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>

						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- MODAL END -->

	</div>

</body>
</html>