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
	<div ng-controller="reports_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-right: 15px; padding-left: 15px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-md-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="well" style="background-color: white; margin-bottom: 0px; min-height: 30vh; padding-top: 0px;">
								<div class="row">
									<div class="col-sm-12" style="text-align: center">
										<h3>Reports</h3>
									</div>
								</div>
								<br>
								<div class="row">
									<div class="col-sm-3 form-group">
										<label>Start Date</label> 
										<input type="datetime-local" class="form-control" ng-model="date.start" ng-model-options="{timezone: 'UTC'}" ng-change="getSalesSummary()" required />
									</div>
									<div class="col-sm-3 form-group">
										<label>End Date</label> 
										<input type="datetime-local" class="form-control" ng-model="date.end" ng-model-options="{timezone: 'UTC'}" ng-change="getSalesSummary()" required />
									</div>
								</div>
								<br>
								<div>
									<table id="datatable_salesSummary" class="table table-bordered table-striped">
										<thead>
											<tr>
												<th>Device Type</th>
												<th>Payment Method</th>
												<th>Total Sales</th>
												<th>Total Amount</th>
											</tr>
										</thead>
										<tbody></tbody>
										<tfoot></tfoot>
									</table>
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