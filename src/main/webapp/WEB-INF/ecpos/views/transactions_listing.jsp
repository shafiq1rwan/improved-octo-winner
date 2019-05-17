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
				<section class="content sectioncalibrator" style="padding-top: 8px; padding-bottom: 8px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-sm-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="well" style="height: 89vh; overflow-y: auto; background-color: white; margin-bottom: 0px; padding-top: 10px; padding-bottom: 10px; padding-left: 15px; padding-right: 15px;">
								<div class="row" style="text-align: center">
									<div class="col-sm-12">
										<font size="4">Transactions Listing</font>
									</div>
								</div>
								<table id="datatable_transactions" class="table table-bordered table-striped">
									<thead>
										<tr>
											<th>Check No</th>
											<th>By</th>
											<th>Transaction Type</th>
											<th>Payment Method</th>
											<th>Payment Type</th>
											<th>Amount</th>
											<th>Status</th>
											<th>Date</th>
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
			
			<div class="modal fade" data-backdrop="static" id="transactionDetailsModal" role="dialog">
				<div class="modal-dialog modal-lg">
					<div class="modal-content">
						<div class="modal-body" style="padding: 20px;">
							<button class="close" data-dismiss="modal">&times;</button>
					
					<!-- upper button group control -->
								<div class="row">
									<div class="col-sm-4" ng-if="!transaction.isVoid && transaction.isApproved">
										<div>
											<button class="btn btn-block btn-primary" ng-click="voidTransaction(transaction.id)">Void</button>
										</div>
									</div>
									<div class="col-sm-4">
										<div>
											<button class="btn btn-block btn-info" ng-click="printTransactionReceipt(transaction.id)">Print Receipt</button>
										</div>
									</div>
									<div class="col-sm-4">
										<div ng-if="">
											<button class="btn btn-block btn-info" ng-click="">Continue Action</button>
										</div>
									</div>
								</div>
					
					
						</div>
					</div>
				</div>		
			</div>
			
			
			<!-- Loading Modal [START] -->
			<div id="loading_modal" class="modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false" data-backdrop="static" role="dialog">
					<div class="modal-content">
						<div class="modal-body text-center">
							<p>{{voidMessage}}</p>
						</div>
					</div>
			</div>
			<!-- Loading Modal [END] -->

		</div>
	</div>
</body>
</html>