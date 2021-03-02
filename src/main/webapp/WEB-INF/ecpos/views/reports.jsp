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
				<section class="content sectioncalibrator" style="padding-top: 8px; padding-bottom: 8px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-sm-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="box box-success" style="height: 89vh; overflow-y: auto; background-color: white; margin-bottom: 0px; padding: 15px;">
								<div class="row" style="text-align: center">
									<div class="col-sm-12">
										<font size="4">REPORTS</font>
									</div>
								</div>
								</br>
								<div class="row">
       							<div class="col-md-6">
								<div class="box box-success">
									<div class="box-header with-border">
										<h3 class="box-title">Sales Report</h3>
									</div>
									<div class="box-body" style="height: 280px">
										<canvas id="myChart"></canvas>
									</div>
								</div></div>
								<div class="col-md-6">
								<div class="box box-success">
									<div class="box-header with-border">
										<h3 class="box-title">Best Selling Item</h3>
									</div>
									<div class="box-body" style="height: 280px">
										<canvas id="myChart2"></canvas>
									</div>
								</div></div></div>
								<br>
								<div class="row">
									<div class="col-sm-4 col-lg-3 form-group">
										<label>Start Date</label> 
										<input type="datetime-local" class="form-control" ng-model="date.start" ng-model-options="{timezone: 'UTC'}" ng-change="getSalesSummary()" required />
									</div>
									<div class="col-sm-4 col-lg-3 form-group">
										<label>End Date</label> 
										<input type="datetime-local" class="form-control" ng-model="date.end" ng-model-options="{timezone: 'UTC'}" ng-change="getSalesSummary()" required />
									</div>
								</div>
								<div class="box-header with-border" style="border:1px grey solid;">
					              <h3 class="box-title">Sales Report</h3>
								<div>
									<table id="datatable_salesSummary" class="table table-bordered table-striped">
										<thead>
											<tr>
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
								</br>
								<div class="row">
									<div class="col-sm-4 col-lg-3 form-group">
										<label>Start Date</label> 
										<input type="datetime-local" class="form-control" ng-model="date.startItem" ng-model-options="{timezone: 'UTC'}" ng-change="getSalesSummary()" required />
									</div>
									<div class="col-sm-4 col-lg-3 form-group">
										<label>End Date</label> 
										<input type="datetime-local" class="form-control" ng-model="date.endItem" ng-model-options="{timezone: 'UTC'}" ng-change="getSalesSummary()" required />
									</div>
								</div>
								<div class="box-header with-border" style="border:1px grey solid;">
					              <h3 class="box-title">Best Selling Item</h3>
								<div>
									<table id="datatable_itemSummary" class="table table-bordered table-striped">
										<thead>
											<tr>
												<th>Name of Item</th>
												<th>Total Item</th>
											</tr>
										</thead>
										<tbody></tbody>
										<tfoot></tfoot>
									</table>
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