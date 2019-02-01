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
	<div ng-controller="check_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-right: 15px; padding-left: 15px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">

						<!-- START of menu well -->
						<div id="menuWell">
							<div class="col-md-6" style="padding-right: 2px; padding-left: 2px;">
								<jsp:include page="/WEB-INF/ecpos/views/menu.jsp" flush="true"></jsp:include>
							</div>
						</div>
						<!-- END of menu well -->
						
						<!-- START of check well -->
						<div id="checkWell">
							<div class="col-md-6" style="padding-right: 2px; padding-left: 2px;">
								<div class="well" style="background-color: white; margin-bottom: 0px;">
									<div class="box box-success">
										<div class="box-body">
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
												<div class="col-sm-6 form-group">
													<button id="printKitchenReceiptButton" class="btn btn-social btn-sm pull-right btn-primary" style="background-color: #00FA9A; border-color: #00FA9A;" ng-click="printKitchenReceipt()">
														<i class="fa fa-print"></i> PRINT KITCHEN RECEIPT
													</button>
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
												<div style="padding-right: 15px; max-height: 30vh; overflow-y: auto; height: 30vh;">
													<div ng-repeat="grandParentItem in checkDetail.grandParentItemArray">
														<div>
															<div class="row">
																<div class='col-sm-1 text-center'><input type="checkbox" ng-click="grandParentItemCheckbox()" name="grandParentItemCheckbox" value={{grandParentItem.checkDetailId}} style="margin: 2px 0 0;"></div>
																<div class='col-sm-2 text-left'>{{grandParentItem.itemCode}}</div>
																<div class='col-sm-5 text-left'>{{grandParentItem.itemName}}</div>
																<div class='col-sm-2 text-center'>{{grandParentItem.itemQuantity}}</div>
																<div class='col-sm-2 text-right'>{{grandParentItem.subtotal| number:2}}</div>
															</div>
															<div ng-repeat="parentItem in grandParentItem.parentItemArray">
																<div class="row">
																	<div class='col-sm-1 text-center'></div>
																	<div class='col-sm-2 text-left'>{{parentItem.itemCode}}</div>
																	<div class='col-sm-5 text-left'>*{{parentItem.itemName}}*</div>
																	<div class='col-sm-2 text-center'>{{parentItem.itemQuantity}}</div>
																	<div class='col-sm-2 text-right'>{{parentItem.subtotal| number:2}}</div>										
																</div>
																<div ng-repeat="childItem in parentItem.childItemArray">
																	<div class="row">
																		<div class='col-sm-1 text-center'></div>
																		<div class='col-sm-2 text-left'>{{childItem.itemCode}}</div>
																		<div class='col-sm-5 text-left'>&nbsp;&nbsp;&nbsp;&nbsp;:{{childItem.itemName}}</div>
																		<div class='col-sm-2 text-center'>{{childItem.itemQuantity}}</div>
																		<div class='col-sm-2 text-right'>{{childItem.subtotal| number:2}}</div>										
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
													<div class='col-sm-2 text-right'><b>{{checkDetail.subtotal| number:2}}</b></div>
												</div>
												<div class="row" style="padding-right: 23px;">
													<div class='col-sm-1 text-center'></div>
													<div class='col-sm-9 text-left'><b>Goods & Services Tax</b></div>
													<div class='col-sm-2 text-right'><b>{{checkDetail.tax| number:2}}</b></div>
												</div>
												<div class="row" style="padding-right: 23px;">
													<div class='col-sm-1 text-center'></div>
													<div class='col-sm-9 text-left'><b>Service Charge</b></div>
													<div class='col-sm-2 text-right'><b>{{checkDetail.serviceCharge| number:2}}</b></div>
												</div>
												<div class="row" style="padding-right: 23px;">
													<div class='col-sm-1 text-center'></div>
													<div class='col-sm-9 text-left'><b>Rounding Adjustment</b></div>
													<div class='col-sm-2 text-right'><b>{{checkDetail.roundingAdjustment| number:2}}</b></div>
												</div>
												<div class="row" style="padding-right: 23px;">
													<div class='col-sm-1 text-center'></div>
													<div class='col-sm-9 text-left'><b>Grand Total</b></div>
													<div class='col-sm-2 text-right' style="border-top: solid; border-top-width: thin; border-bottom: 3px double;"><b>{{checkDetail.grandTotal| number:2}}</b></div>
												</div>
												<hr>
												<div class="row" style="padding-right: 23px;">
													<div class='col-sm-1 text-center'></div>
													<div class='col-sm-9 text-left'><b>Deposit Amount</b></div>
													<div class='col-sm-2 text-right'><b>{{checkDetail.deposit| number:2}}</b></div>
												</div>
												<div class="row" style="padding-right: 23px;">
													<div class='col-sm-1 text-center'></div>
													<div class='col-sm-9 text-left'><b>Tender Amount</b></div>
													<div class='col-sm-2 text-right'><b>{{checkDetail.tender| number:2}}</b></div>
												</div>
												<div class="row" style="padding-right: 23px;">
													<div class='col-sm-1 text-center'></div>
													<div class='col-sm-9 text-left'><b>Overdue Amount</b></div>
													<div class='col-sm-2 text-right'><b>{{checkDetail.overdue| number:2}}</b></div>
												</div>
											</div>
										</div>
									</div>
									<div id="checkActionButtons">
										<div class="row" style="margin-bottom: 5px;">
											<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
												<button id="cancelItemButton" class="btn btn-block btn-info" ng-click="cancelItem()">CANCEL ITEM</button>
											</div>
											<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
												<button id="paymentButton" class="btn btn-block btn-info" ng-click="redirectPayment()">PAYMENT</button>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<!-- END of check well -->
						
						<!-- START of payment well -->
						<div id="paymentWell">
							<div class="col-md-6" style="padding-right: 2px; padding-left: 2px;">
								<jsp:include page="/WEB-INF/ecpos/views/payment.jsp" flush="true"></jsp:include>
							</div>
						</div>
						<!-- END of payment well -->
						
					</div>
				</section>
			</div>
		</div>
	</div>
</body>
</html>