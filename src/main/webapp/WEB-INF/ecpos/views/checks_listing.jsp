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
	margin-top: 0px;
	margin-bottom: 5px;
}
</style>
</head>

<body>
	<div ng-controller="checks_listing_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-top: 8px; padding-bottom: 8px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">
						<div class="col-sm-12" style="padding-right: 2px; padding-left: 2px;">
							<div class="well" style="height: 89vh; overflow-y: auto; background-color: white; margin-bottom: 0px; padding-top: 10px; padding-bottom: 10px; padding-left: 15px; padding-right: 15px;">
								<div class="row" style="text-align: center">
									<div class="col-sm-12">
										<font size="4">CHECKS LISTING</font>
									</div>
								</div>
								<table id="datatable_checks" class="table table-bordered table-striped" style="width:100%;">
									<thead>
										<tr>
											<th>ID</th>
											<th>Check No</th>
											<th>By</th>
											<th>Order Type</th>
											<th>Table Name</th>
											<th>Grand Total Amount</th>
											<th>Overdue Amount</th>
											<th>Status</th>
											<th>Created Date</th>
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
				<div class="modal-dialog modal-lg">
					<div class="modal-content">
						<!-- <div class="modal-header"></div> -->
						<div class="modal-body" style="padding: 20px;">
							<button class="close" data-dismiss="modal">&times;</button>
							<div class="row">
								<div class="col-sm-7">
									<div>
										<font><b>Check : {{checkDetail.checkNo}}</b> / <b>Table : {{checkDetail.tableName}}</b></font>
									</div>
									<div>
										<font><b>Created Date : {{checkDetail.createdDate}}</b></font>
									</div>
									<div>
										<font><b>Status : {{checkDetail.status}}</b></font>
									</div>
									<div ng-if="checkDetail.orderType != '1'">
										<font><b>Customer Name: : {{checkDetail.customerName}}</b></font>
									</div>
								</div>
								<div class="col-sm-4">
									<div ng-if="checkDetail.status == 'New' || checkDetail.status == 'Pending'")>
										<button class="btn btn-block btn-info" ng-click="redirectCheck()">Continue Action</button>
									</div>
								</div>
							</div>
							<div>
								<div class="row" style="padding-top: 8px; padding-right: 15px;">
									<div class='col-sm-1 text-center'><b>Code</b></div>
									<div class='col-sm-8 text-left'><b>Item</b></div>
									<div class='col-sm-1 text-center'><b>Quantity</b></div>
									<div class='col-sm-2 text-right'><b>Price</b></div>
								</div>
								<hr>
								<div style="padding-right: 15px; max-height: 33vh; overflow-y: auto; height: 33vh;">
									<div ng-repeat="grandParentItem in checkDetail.grandParentItemArray">
										
										<div style="padding-bottom: 8px;" ng-if="grandParentItem.itemStatus != 'Paid'">
											<div class="row">
												<div class='col-sm-1 text-center'>{{grandParentItem.itemCode}}</div>
												<div class='col-sm-8 text-left'>{{grandParentItem.itemName}}@{{grandParentItem.itemPrice| number:2}}</div>
												<div class='col-sm-1 text-center'>{{grandParentItem.itemQuantity}}</div>
												<div class='col-sm-2 text-right'>{{grandParentItem.totalAmount| number:2}}</div>
											</div>
											<div ng-repeat="parentItem in grandParentItem.parentItemArray">
												<div class="row">
													<div class='col-sm-1 text-center'></div>
													<div class='col-sm-8 text-left'>:{{parentItem.itemName}}@{{parentItem.itemPrice| number:2}}</div>
													<div class='col-sm-1 text-center'>{{parentItem.itemQuantity}}</div>
													<div class='col-sm-2 text-right'>{{parentItem.totalAmount| number:2}}</div>										
												</div>
												<div ng-repeat="childItem in parentItem.childItemArray">
													<div class="row">
														<div class='col-sm-1 text-center'></div>
														<div class='col-sm-8 text-left'>&nbsp;&nbsp;&nbsp;&nbsp;*{{childItem.itemName}}@{{childItem.itemPrice| number:2}}</div>
														<div class='col-sm-1 text-center'>{{childItem.itemQuantity}}</div>
														<div class='col-sm-2 text-right'>{{childItem.totalAmount| number:2}}</div>										
													</div>
												</div>
											</div>
										</div>
										
										<div style="padding-bottom: 8px; color: red;" ng-if="grandParentItem.itemStatus == 'Paid'">
											<div class="row">
												<div class='col-sm-1 text-center'>{{grandParentItem.itemCode}}</div>
												<div class='col-sm-8 text-left'>{{grandParentItem.itemName}}@{{grandParentItem.itemPrice| number:2}}</div>
												<div class='col-sm-1 text-center'>{{grandParentItem.itemQuantity}}</div>
												<div class='col-sm-2 text-right'>{{grandParentItem.totalAmount| number:2}}</div>
											</div>
											<div ng-repeat="parentItem in grandParentItem.parentItemArray">
												<div class="row">
													<div class='col-sm-1 text-center'></div>
													<div class='col-sm-8 text-left'>:{{parentItem.itemName}}@{{parentItem.itemPrice| number:2}}</div>
													<div class='col-sm-1 text-center'>{{parentItem.itemQuantity}}</div>
													<div class='col-sm-2 text-right'>{{parentItem.totalAmount| number:2}}</div>										
												</div>
												<div ng-repeat="childItem in parentItem.childItemArray">
													<div class="row">
														<div class='col-sm-1 text-center'></div>
														<div class='col-sm-8 text-left'>&nbsp;&nbsp;&nbsp;&nbsp;*{{childItem.itemName}}@{{childItem.itemPrice| number:2}}</div>
														<div class='col-sm-1 text-center'>{{childItem.itemQuantity}}</div>
														<div class='col-sm-2 text-right'>{{childItem.totalAmount| number:2}}</div>										
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<hr style="margin-bottom: 0;">
								<div class="row" style="color: red;">
									<div class='col-sm-12'>
										<label style="font-size: x-small; font-weight: normal;">*Item(s) in red indicates already paid</label>
									</div>
								</div>
								<div class="row" style="padding-right: 15px;">
									<div class='col-sm-10 text-left'><b>Subtotal</b></div>
									<div class='col-sm-2 text-right'><b>{{checkDetail.totalAmount| number:2}}</b></div>
								</div>
								<div ng-repeat="taxCharge in checkDetail.taxCharges">
										<div class='col-sm-10 text-left'><b>{{taxCharge.name}} {{taxCharge.rate}}%</b></div>
										<div class='col-sm-2 text-right'><b>{{taxCharge.chargeAmount| number:2}}</b></div>
									</div>
								</div>
								<div class="row" style="padding-right: 15px;">
									<div class='col-sm-10 text-left'><b>Rounding Adjustment</b></div>
									<div class='col-sm-2 text-right'><b>{{checkDetail.totalAmountWithTaxRoundingAdjustment| number:2}}</b></div>
								</div>
								<div class="row" style="padding-right: 15px;">
									<div class='col-sm-10 text-left'><b>Grand Total</b></div>
									<div class='col-sm-2 text-right' style="border-top: solid; border-top-width: thin; border-bottom: 3px double;"><b>{{checkDetail.grandTotalAmount| number:2}}</b></div>
								</div>
								<hr>
								<div class="row" style="padding-right: 15px;">
									<div class='col-sm-10 text-left'><b>Tender Amount</b></div>
									<div class='col-sm-2 text-right'><b>{{checkDetail.tenderAmount| number:2}}</b></div>
								</div>
								<div class="row" style="padding-right: 15px;">
									<div class='col-sm-10 text-left'><b>Overdue Amount</b></div>
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