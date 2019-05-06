<%@ page import="mpay.ecpos_manager.general.utility.UserAuthenticationModel"%>

<%
	UserAuthenticationModel user = (UserAuthenticationModel) session.getAttribute("session_user");
%>

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

.green-button:hover {
	background-color: #3CB371!important;
}
</style>
</head>

<body>
	<div ng-controller="check_CTRL">
		<div ng-init="initiation();">
			<div class="content-wrapper" style="font-size: 0.9em;">
				<section class="content sectioncalibrator" style="padding-top: 8px; padding-bottom: 8px;">
					<div class="row container-fluid" style="padding-right: 2px; padding-left: 2px;">

						<!-- START of menu well -->
						<div id="menuWell">
							<div class="col-sm-6" style="padding-right: 2px; padding-left: 2px;">
								<jsp:include page="/WEB-INF/ecpos/views/menu.jsp" flush="true"></jsp:include>
							</div>
						</div>
						<!-- END of menu well -->
						
						<!-- START of check well -->
						<div id="checkWell">
							<div class="col-sm-6" style="padding-right: 2px; padding-left: 2px;">
								<div class="well" style="background-color: white; margin-bottom: 0px; padding: 10px;">
									<div class="row">
										<div class="col-sm-7">
											<div>
												<font><b>Check : {{checkDetail.checkNo}}</b> / <b>Table : {{checkDetail.tableNo}}</b></font>
											</div>
											<div>
												<font><b>Created Date : {{checkDetail.createdDate}}</b></font>
											</div>
											<div>
												<font><b>Status : {{checkDetail.status}}</b></font>
											</div>
										</div>
										<div class="col-sm-5">
											<div ng-if="mode == 1">
											<%if (user.getStoreType() == 2) {%>
											<button id="generateQRButton" class="btn btn-social btn-sm pull-right bg-maroon" style="width: 100%;" ng-click="generateQR()">
												<i class="fa fa-qrcode"></i> Generate QR
											</button>
											<br>
											<br>
											<%}%>
											<button id="barcodeOrderButton" class="btn btn-social btn-sm pull-right bg-maroon" style="width: 100%;" ng-click="openBarcodeModal()">
												<i class="fa fa-barcode"></i> Barcode Order
											</button>
											</div>
											<div ng-if="mode == 2">
												<button class="btn btn-block btn-info" ng-click="redirectMenu()">Back To Previous</button>
											</div>
										</div>
									</div>
									<div>
										<div class="row" style="padding-top: 8px; padding-right: 15px;">
											<div class='col-sm-1 text-center'><input type="checkbox" ng-click="allGrandParentItemCheckbox()" id="allGrandParentItemCheckbox" style="margin: 2px 0 0;"></div>
											<div class='col-sm-2 text-left'><b>Code</b></div>
											<div class='col-sm-5 text-left' style="padding: 0px;"><b>Item</b></div>
											<div class='col-sm-2 text-center'><b>Quantity</b></div>
											<div class='col-sm-2 text-right'><b>Price</b></div>
										</div>
										<hr>
										<div style="padding-right: 15px; max-height: 33vh; overflow-y: auto; height: 33vh;">
											<div ng-repeat="grandParentItem in checkDetail.grandParentItemArray">
												<div style="padding-bottom: 8px;">
													<div class="row">
														<div class='col-sm-1 text-center'><input type="checkbox" ng-click="grandParentItemCheckbox()" name="grandParentItemCheckbox" value={{grandParentItem.checkDetailId}} style="margin: 2px 0 0;"></div>
														<div class='col-sm-2 text-left'>{{grandParentItem.itemCode}}</div>
														<div class='col-sm-5 text-left' style="padding: 0px;">{{grandParentItem.itemName}}@{{grandParentItem.itemPrice| number:2}}</div>
														<%if (user.getStoreType() == 1) {%>
														<div class='col-sm-2 text-left' style="padding-left: 0px; padding-right: 0px;" ng-if="grandParentItem.isAlaCarte && !grandParentItem.hasModified">
															<div class="input-group">
												                <input type="number" id="{{grandParentItem.checkDetailId}}" name="itemQuantity" style="width: 100%; padding-left: 6px;" value={{grandParentItem.itemQuantity}} min="1" max="99" size="1" />
											                	<span class="input-group-btn">
											                    	<button class="btn btn-flat btn-info" style="height: 23px; width: 23px; padding: 0px;" ng-click="submitUpdateItemQuantity(grandParentItem.checkDetailId)">&#10004;</button>
											                    </span>
												            </div>
														</div>
														<div class='col-sm-2 text-center' ng-if="!(grandParentItem.isAlaCarte && !grandParentItem.hasModified)">{{grandParentItem.itemQuantity}}</div>
														<%} else {%>
														<div class='col-sm-2 text-center'>{{grandParentItem.itemQuantity}}</div>
														<%}%>
														<div class='col-sm-2 text-right'>{{grandParentItem.totalAmount| number:2}}</div>
													</div>
													<div ng-repeat="parentItem in grandParentItem.parentItemArray">
														<div class="row">
															<div class='col-sm-1 text-center'></div>
															<div class='col-sm-2 text-left'>{{parentItem.itemCode}}</div>
															<div class='col-sm-5 text-left' style="padding: 0px;">:{{parentItem.itemName}}@{{parentItem.itemPrice| number:2}}</div>
															<div class='col-sm-2 text-center'>{{parentItem.itemQuantity}}</div>
															<div class='col-sm-2 text-right'>{{parentItem.totalAmount| number:2}}</div>										
														</div>
														<div ng-repeat="childItem in parentItem.childItemArray">
															<div class="row">
																<div class='col-sm-1 text-center'></div>
																<div class='col-sm-2 text-left'>{{childItem.itemCode}}</div>
																<div class='col-sm-5 text-left' style="padding: 0px;">&nbsp;&nbsp;&nbsp;&nbsp;*{{childItem.itemName}}@{{childItem.itemPrice| number:2}}</div>
																<div class='col-sm-2 text-center'>{{childItem.itemQuantity}}</div>
																<div class='col-sm-2 text-right'>{{childItem.totalAmount| number:2}}</div>										
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
										<hr>
										<div class="row" style="padding-right: 15px;">
											<div class='col-sm-1 text-center'></div>
											<div class='col-sm-9 text-left'><b>Subtotal</b></div>
											<div class='col-sm-2 text-right'><b>{{checkDetail.totalAmount| number:2}}</b></div>
										</div>
										<div ng-repeat="taxCharge in checkDetail.taxCharges">
											<div class="row" style="padding-right: 15px;">
												<div class='col-sm-1 text-center'></div>
												<div class='col-sm-9 text-left'><b>{{taxCharge.name}} {{taxCharge.rate}}%</b></div>
												<div class='col-sm-2 text-right'><b>{{taxCharge.chargeAmount| number:2}}</b></div>
											</div>
										</div>
										<div class="row" style="padding-right: 15px;">
											<div class='col-sm-1 text-center'></div>
											<div class='col-sm-9 text-left'><b>Rounding Adjustment</b></div>
											<div class='col-sm-2 text-right'><b>{{checkDetail.totalAmountWithTaxRoundingAdjustment| number:2}}</b></div>
										</div>
										<div class="row" style="padding-right: 15px;">
											<div class='col-sm-1 text-center'></div>
											<div class='col-sm-9 text-left'><b>Grand Total</b></div>
											<div class='col-sm-2 text-right' style="border-top: solid; border-top-width: thin; border-bottom: 3px double;"><b>{{checkDetail.grandTotalAmount| number:2}}</b></div>
										</div>
										<hr>
										<div class="row" style="padding-right: 15px;">
											<div class='col-sm-1 text-center'></div>
											<div class='col-sm-9 text-left'><b>Deposit Amount</b></div>
											<div class='col-sm-2 text-right'><b>{{checkDetail.depositAmount| number:2}}</b></div>
										</div>
										<div class="row" style="padding-right: 15px;">
											<div class='col-sm-1 text-center'></div>
											<div class='col-sm-9 text-left'><b>Tender Amount</b></div>
											<div class='col-sm-2 text-right'><b>{{checkDetail.tenderAmount| number:2}}</b></div>
										</div>
										<div class="row" style="padding-right: 15px;">
											<div class='col-sm-1 text-center'></div>
											<div class='col-sm-9 text-left'><b>Overdue Amount</b></div>
											<div class='col-sm-2 text-right'><b>{{checkDetail.overdueAmount| number:2}}</b></div>
										</div>
									</div>
									<hr style="margin-bottom: 10px;">
									<div id="checkActionButtons">
										<div class="row" style="margin-bottom: 5px;">
											<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
												<button id="cancelItemButton" class="btn btn-block btn-info" ng-click="cancelItem()">CANCEL ITEM</button>
											</div>
											<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
												<button id="paymentButton" class="btn btn-block btn-info" ng-click="redirectPayment()">PAYMENT</button>
											</div>
										</div>
										<div class="row">
											<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
												<button id="cancelCheckButton" class="btn btn-block btn-info" ng-click="cancelCheck()">CANCEL CHECK</button>
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
			
			<div class="modal fade" data-backdrop="static" id="QRImageModal" role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<!-- <div class="modal-header"></div> -->
						<div class="modal-body">
							<div class="row" style="font-size: large">
								<div class="col-sm-11"></div>
								<div class="col-sm-1">
									<button class="close" data-dismiss="modal">&times;</button>
								</div>
							</div>
							<div class="row" style="text-align: center;">
								<img id="QRImage" style="height: 50%; width: 50%;" />
							</div>
 							<div class="row">
								<div class="col-sm-12" >
									<div class="pull-right">
										<input type="button" class="btn bg-maroon" ng-click="printQR()" value="Print" />
									</div>
								</div>
							</div>
						</div>
						<!-- <div class="modal-footer"></div> -->
					</div>
				</div>
			</div>
			
			<div class="modal fade" data-backdrop="static" id="barcodeModal" role="dialog">
				<div class="modal-dialog">
					<div class="modal-content">
						<!-- <div class="modal-header"></div> -->
						<div class="modal-body">
							<div class="row" style="font-size: large">
								<div class="col-sm-11"></div>
								<div class="col-sm-1">
									<button class="close" data-dismiss="modal">&times;</button>
								</div>
							</div>
							<div style="height: 15vh; display: flex; align-items: center;">
								<div class="col-sm-1 form-group"></div>
								<div class="col-sm-10 form-group">
									<label>Please scan item's barcode:</label> 
									<input type="text" class="form-control" ng-model="barcode" ng-click="barcodeOrder()" ng-keydown="$event.keyCode === 13 && barcodeOrder()" id = "barcode_input" required/>
								</div>
								<div class="col-sm-1 form-group"></div>
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