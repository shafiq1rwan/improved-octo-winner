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
	<div ng-controller="Show_reports_CTRL">
		<div class="content-wrapper" style="font-size: 0.9em;">
			<section class="content-header">
				<h1>
					ECPOS <small>Report</small>
				</h1>
				<ol class="breadcrumb">
					<li>MAIN NAVIGATION</li>
					<li>Report</li>
				</ol>
			</section>
			<section class="content sectioncalibrator">
				<div class="row">
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="box">
							<div class="box-header">
								<div>
									<h3 class="box-title">ECPOS Report</h3>
								</div>
							</div>

							<div class="box-body">
								<div class="row">
									<div class="col-md-12">
										<!-- Display message -->

										<form>

											<div class="form-group row">
												<div class="col-md-4">
													<label for="datetextbar">Please select a Date to
														generate report:</label>
													<div id="myCalendar" class="input-group date"
														data-provide="datepicker">
														<input type="text" class="form-control" name="datetextbar"
															id="datetextbar" required ng-value="todayDate">
														<div class="input-group-addon">
															<span class="glyphicon glyphicon-th"></span>
														</div>
													</div>
												</div>
											</div>
																	
												<button class="btn-primary btn" type="button" ng-click="generateMonthlySalesReport()">Generate</button>
											
										</form>



										<!-- Take Away Order -->
										<div class="">
										
										
										
										</div>


									</div>

								</div>
							</div>
						</div>





					</div>
			</section>
		</div>
	</div>
</body>
</html>