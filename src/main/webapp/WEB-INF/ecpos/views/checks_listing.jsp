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

hr {
	margin-top: 5px;
	margin-bottom: 5px;
}
</style>
</head>

<body>
	<div ng-controller="checks_listing_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-right: 15px; padding-left: 15px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-md-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="well" style="background-color: white; min-height: 87vh; margin-bottom: 0px; padding-top: 0px;">
								<div class="row" style="text-align: center">
									<div class="col-sm-12">
										<h3>Checks Listing</h3>
									</div>
								</div>
								<br>
								<table id="datatable_checks" class="table table-bordered table-striped">
									<thead>
										<tr>
											<th>ID</th>
											<th>Check Number</th>
											<th>Performed By</th>
											<th>Order Type</th>
											<th>Table Number</th>
											<th>Total Item Quantity</th>
											<th>Grand Total Amount</th>
											<th>Overdue Amount</th>
											<th>Check Status</th>
											<th>Created Date</th>
											<!-- <th></th> -->
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
			
			<div class="modal fade" data-backdrop="static" id="checkDetailsModal" role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<!-- <div class="modal-header"></div> -->
						<div class="modal-body">
							<button class="close" data-dismiss="modal">&times;</button>
							<div class="row">
								<div class="col-sm-6 form-group">
									<div>
										<font><b>Check : {{checkDetail.checkNo}}</b>/<b>Table : {{checkDetail.tableNo}}</b></font>
									</div>
									<div>
										<font><b>Created Date : {{checkDetail.createdDate}}</b></font>
									</div>
									<div>
										<font><b>Status : {{checkDetail.status}}</b></font>
									</div>
								</div>
							</div>
							<div style="margin-right: 3px;">
								<div class="row" style="padding-right: 30px;">
									<div class='col-sm-1 text-center'><input type="checkbox" ng-click="allGrandParentItemCheckbox()" id="allGrandParentItemCheckbox" style="margin: 2px 0 0;"></div>
									<div class='col-sm-2 text-left'><b>Code</b></div>
									<div class='col-sm-5 text-left'><b>Item</b></div>
									<div class='col-sm-2 text-center'><b>Quantity</b></div>
									<div class='col-sm-2 text-right'><b>Price</b></div>
								</div>
								<hr>
								<div style="padding-right: 15px; max-height: 25vh; overflow-y: auto; height: 25vh;">
									<div ng-repeat="grandParentItem in checkDetail.grandParentItemArray">
										<div>
											<div class="row">
												<div class='col-sm-1 text-center'><input type="checkbox" ng-click="grandParentItemCheckbox()" name="grandParentItemCheckbox" value={{grandParentItem.checkDetailId}} style="margin: 2px 0 0;"></div>
												<div class='col-sm-2 text-left'>{{grandParentItem.itemCode}}</div>
												<div class='col-sm-5 text-left'><b>{{grandParentItem.itemName}}</b></div>
												<div class='col-sm-2 text-center'>{{grandParentItem.itemQuantity}}</div>
												<div class='col-sm-2 text-right'>{{grandParentItem.totalAmount| number:2}}</div>
											</div>
											<div ng-repeat="parentItem in grandParentItem.parentItemArray">
												<div class="row">
													<div class='col-sm-1 text-center'></div>
													<div class='col-sm-2 text-left'>{{parentItem.itemCode}}</div>
													<div class='col-sm-5 text-left'>*{{parentItem.itemName}}*</div>
													<div class='col-sm-2 text-center'>{{parentItem.itemQuantity}}</div>
													<div class='col-sm-2 text-right'>{{parentItem.totalAmount| number:2}}</div>										
												</div>
												<div ng-repeat="childItem in parentItem.childItemArray">
													<div class="row">
														<div class='col-sm-1 text-center'></div>
														<div class='col-sm-2 text-left'>{{childItem.itemCode}}</div>
														<div class='col-sm-5 text-left'>&nbsp;&nbsp;&nbsp;&nbsp;:{{childItem.itemName}}</div>
														<div class='col-sm-2 text-center'>{{childItem.itemQuantity}}</div>
														<div class='col-sm-2 text-right'>{{childItem.totalAmount| number:2}}</div>										
													</div>
												</div>
											</div>
										</div>
										<br>
									</div>
								</div>
								<hr>
								<div class="row" style="padding-right: 23px;">
									<div class='col-sm-1 text-center'></div>
									<div class='col-sm-9 text-left'><b>Subtotal</b></div>
									<div class='col-sm-2 text-right'><b>{{checkDetail.totalAmount| number:2}}</b></div>
								</div>
								<div ng-repeat="taxCharge in checkDetail.taxCharges">
									<div class="row" style="padding-right: 23px;">
										<div class='col-sm-1 text-center'></div>
										<div class='col-sm-9 text-left'><b>{{taxCharge.name}} {{taxCharge.rate}}%</b></div>
										<div class='col-sm-2 text-right'><b>{{taxCharge.chargeAmount| number:2}}</b></div>
									</div>
								</div>
								<div class="row" style="padding-right: 23px;">
									<div class='col-sm-1 text-center'></div>
									<div class='col-sm-9 text-left'><b>Rounding Adjustment</b></div>
									<div class='col-sm-2 text-right'><b>{{checkDetail.totalAmountWithTaxRoundingAdjustment| number:2}}</b></div>
								</div>
								<div class="row" style="padding-right: 23px;">
									<div class='col-sm-1 text-center'></div>
									<div class='col-sm-9 text-left'><b>Grand Total</b></div>
									<div class='col-sm-2 text-right' style="border-top: solid; border-top-width: thin; border-bottom: 3px double;"><b>{{checkDetail.grandTotalAmount| number:2}}</b></div>
								</div>
								<hr>
								<div class="row" style="padding-right: 23px;">
									<div class='col-sm-1 text-center'></div>
									<div class='col-sm-9 text-left'><b>Deposit Amount</b></div>
									<div class='col-sm-2 text-right'><b>{{checkDetail.depositAmount| number:2}}</b></div>
								</div>
								<div class="row" style="padding-right: 23px;">
									<div class='col-sm-1 text-center'></div>
									<div class='col-sm-9 text-left'><b>Tender Amount</b></div>
									<div class='col-sm-2 text-right'><b>{{checkDetail.tenderAmount| number:2}}</b></div>
								</div>
								<div class="row" style="padding-right: 23px;">
									<div class='col-sm-1 text-center'></div>
									<div class='col-sm-9 text-left'><b>Overdue Amount</b></div>
									<div class='col-sm-2 text-right'><b>{{checkDetail.overdueAmount| number:2}}</b></div>
								</div>
							</div>
						<!-- <div class="modal-footer"></div> -->
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>