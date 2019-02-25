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
	<div ng-controller="transactions_listing_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-right: 15px; padding-left: 15px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-md-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="well" style="background-color: white; min-height: 87vh; margin-bottom: 0px; padding-top: 0px;">
								<div class="row" style="text-align: center">
									<div class="col-sm-12">
										<h3>Transactions Listing</h3>
									</div>
								</div>
								<br>
								<table id="datatable_transactions" class="table table-bordered table-striped">
									<thead>
										<tr>
											<th>No</th>
											<th>Performed By</th>
											<th>Check Number</th>
											<th>Transaction Date</th>
											<th>Transaction Type</th>
											<th>Payment Method</th>
											<th>Payment Type</th>
											<th>Terminal</th>
											<th>Transaction Amount</th>
											<th>Transaction Status</th>
											<th></th>
										</tr>
									</thead>
									<tbody></tbody>
									<tfoot></tfoot>
								</table>
							</div>
						</div>
					</div>
				</section>
			</div>
		</div>
	</div>
</body>
</html>